package j2me;

import javax.microedition.media.control.TempoControl;

/** Tempo is emulated as a playback-rate change relative to the MIDI file's initial tempo. */
public class TempoControlImpl implements TempoControl {
    private final PlayerImpl player;
    private final int baseTempo;
    private int tempo;
    private int rate = 100000;

    TempoControlImpl(PlayerImpl p, int initialTempo) {
        player = p;
        baseTempo = initialTempo > 0 ? initialTempo : 120000;
        tempo = baseTempo;
    }

    int ratePermille() {
        long r = (long) tempo * rate / baseTempo / 100;
        return (int) r;
    }

    public int setTempo(int millitempo) {
        if (millitempo < 10000) {
            millitempo = 10000;
        }
        if (millitempo > 300000) {
            millitempo = 300000;
        }
        tempo = millitempo;
        player.applyRate(ratePermille());
        return tempo;
    }

    public int getTempo() {
        return tempo;
    }

    public int setRate(int millirate) {
        if (millirate < getMinRate()) {
            millirate = getMinRate();
        }
        if (millirate > getMaxRate()) {
            millirate = getMaxRate();
        }
        rate = millirate;
        player.applyRate(ratePermille());
        return rate;
    }

    public int getRate() {
        return rate;
    }

    public int getMaxRate() {
        return 300000;
    }

    public int getMinRate() {
        return 10000;
    }
}
