import javax.sound.midi.*;
import javax.sound.sampled.*;
import com.sun.media.sound.AudioSynthesizer;
import java.io.*;
import java.util.*;

/**
 * Offline MIDI -> WAV renderer using the JDK's built-in Gervill synthesizer and an SF2 bank.
 * usage: MidiRender <sf2> <in.mid> <out.wav> <loop|once>
 *  loop : output length == one iteration of the sequence; the release tail that rings past the
 *         end is mixed back onto the beginning so the clip loops seamlessly in Unity.
 *  once : output == sequence + release tail (with a short fade).
 */
public class MidiRender {
    static final float RATE = 44100f;
    static final double TAIL_SEC = 2.5;

    public static void main(String[] a) throws Exception {
        File sf2 = new File(a[0]); File in = new File(a[1]); File out = new File(a[2]);
        boolean loop = a.length > 3 && a[3].equals("loop");
        Sequence seq = MidiSystem.getSequence(in);

        AudioSynthesizer synth = (AudioSynthesizer) MidiSystem.getSynthesizer();
        AudioFormat fmt = new AudioFormat(RATE, 16, 2, true, false);
        Map<String, Object> info = new HashMap<>();
        info.put("resampling", "sinc");
        info.put("max polyphony", 256);
        info.put("reverb", true);
        AudioInputStream stream = synth.openStream(fmt, info);
        Soundbank bank = MidiSystem.getSoundbank(sf2);
        synth.unloadAllInstruments(synth.getDefaultSoundbank());
        synth.loadAllInstruments(bank);

        Receiver recv = synth.getReceiver();
        // GM reset-ish: all controllers off on every channel
        for (int ch = 0; ch < 16; ch++) {
            recv.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, ch, 121, 0), 0);
        }
        long lenUs = send(seq, recv);
        double lenSec = lenUs / 1e6;
        int loopFrames = (int) Math.round(lenSec * RATE);
        int tailFrames = (int) Math.round(TAIL_SEC * RATE);
        int total = loopFrames + tailFrames;
        byte[] buf = new byte[total * 4];
        int off = 0;
        while (off < buf.length) {
            int r = stream.read(buf, off, buf.length - off);
            if (r < 0) break;
            off += r;
        }
        stream.close();
        synth.close();

        short[] pcm = new short[total * 2];
        for (int i = 0; i < pcm.length; i++) pcm[i] = (short) ((buf[i * 2] & 0xff) | (buf[i * 2 + 1] << 8));

        float[] outS;
        if (loop) {
            outS = new float[loopFrames * 2];
            for (int i = 0; i < outS.length; i++) outS[i] = pcm[i];
            for (int i = 0; i < tailFrames * 2 && i < outS.length; i++) outS[i] += pcm[loopFrames * 2 + i];
        } else {
            outS = new float[total * 2];
            for (int i = 0; i < outS.length; i++) outS[i] = pcm[i];
            int fade = (int) (0.5 * RATE);
            for (int f = 0; f < fade; f++) {
                float g = 1f - (f + 1) / (float) fade;
                int fi = total - fade + f;
                outS[fi * 2] *= g; outS[fi * 2 + 1] *= g;
            }
        }
        float peak = 1;
        for (float v : outS) peak = Math.max(peak, Math.abs(v));
        float gain = peak > 32000 ? 32000f / peak : 1f;
        byte[] ob = new byte[outS.length * 2];
        for (int i = 0; i < outS.length; i++) {
            int v = Math.round(outS[i] * gain);
            v = Math.max(-32768, Math.min(32767, v));
            ob[i * 2] = (byte) v; ob[i * 2 + 1] = (byte) (v >> 8);
        }
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(ob), fmt, outS.length / 2);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
        System.out.printf("%s: %.2fs (%s) peak=%.0f%n", in.getName(), outS.length / 2 / RATE, loop ? "loop" : "once", peak);
    }

    /** Sends every event with an absolute microsecond timestamp; returns the sequence length in us. */
    static long send(Sequence seq, Receiver recv) throws Exception {
        List<MidiEvent> evs = new ArrayList<>();
        for (Track t : seq.getTracks()) for (int i = 0; i < t.size(); i++) evs.add(t.get(i));
        evs.sort(Comparator.comparingLong(MidiEvent::getTick));
        boolean ppq = seq.getDivisionType() == Sequence.PPQ;
        int res = seq.getResolution();
        double usPerTick = ppq ? 500000.0 / res : 1e6 / (seq.getDivisionType() * res);
        long lastTick = 0; double us = 0;
        for (MidiEvent e : evs) {
            long tick = e.getTick();
            us += (tick - lastTick) * usPerTick; lastTick = tick;
            MidiMessage m = e.getMessage();
            if (m instanceof MetaMessage) {
                MetaMessage mm = (MetaMessage) m;
                if (mm.getType() == 0x51 && ppq) {
                    byte[] d = mm.getData();
                    int mpq = ((d[0] & 0xff) << 16) | ((d[1] & 0xff) << 8) | (d[2] & 0xff);
                    usPerTick = (double) mpq / res;
                }
                continue;
            }
            if (m instanceof SysexMessage) continue;
            recv.send(m, (long) us);
        }
        long endTick = seq.getTickLength();
        us += (endTick - lastTick) * usPerTick;
        return (long) us;
    }
}
