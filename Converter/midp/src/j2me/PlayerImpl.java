package j2me;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

/** Player that forwards playback to the host's audio backend (pre-converted clips). */
public class PlayerImpl implements Player {
    private final String path;
    private final String type;
    private int state = UNREALIZED;
    private int loopCount = 1;
    private Object audioHandle;
    private PlayerListener[] listeners = new PlayerListener[2];
    private int listenerCount;
    private VolumeControlImpl volume;
    private TempoControlImpl tempo;
    private long lastPos;
    private boolean wasPlaying;
    private boolean pausedByHost;

    public PlayerImpl(String path, String type, byte[] data) {
        this.path = path;
        this.type = type;
        volume = new VolumeControlImpl(this);
        if (type != null && type.indexOf("midi") >= 0) {
            tempo = new TempoControlImpl(this, Midi.initialTempo(data));
        }
        Media.register(this);
    }

    public Object handle() {
        return audioHandle;
    }

    void applyVolume() {
        if (audioHandle != null) {
            Host.backend.audioVolume(audioHandle, volume.getLevel(), volume.isMuted());
        }
    }

    void applyRate(int permille) {
        if (audioHandle != null) {
            Host.backend.audioRate(audioHandle, permille);
        }
    }

    public void realize() throws MediaException {
        checkClosed();
        if (state == UNREALIZED) {
            state = REALIZED;
        }
    }

    public void prefetch() throws MediaException {
        checkClosed();
        if (state == UNREALIZED) {
            realize();
        }
        if (state == REALIZED) {
            if (audioHandle == null && path != null) {
                audioHandle = Host.backend.audioOpen(path, type);
            }
            state = PREFETCHED;
            applyVolume();
            if (tempo != null) {
                applyRate(tempo.ratePermille());
            }
        }
    }

    public void start() throws MediaException {
        checkClosed();
        if (state == STARTED) {
            return;
        }
        if (state != PREFETCHED) {
            prefetch();
        }
        if (audioHandle != null) {
            applyVolume();
            Host.backend.audioStart(audioHandle, loopCount);
        }
        state = STARTED;
        lastPos = 0;
        wasPlaying = true;
        post(PlayerListener.STARTED, null);
    }

    public void stop() throws MediaException {
        checkClosed();
        if (state != STARTED) {
            return;
        }
        if (audioHandle != null) {
            Host.backend.audioStop(audioHandle);
        }
        state = PREFETCHED;
        wasPlaying = false;
        post(PlayerListener.STOPPED, null);
    }

    public void deallocate() {
        if (state == CLOSED) {
            return;
        }
        if (state == STARTED) {
            try {
                stop();
            } catch (MediaException e) {
                // ignored
            }
        }
        if (state == PREFETCHED) {
            state = REALIZED;
        }
    }

    public void close() {
        if (state == CLOSED) {
            return;
        }
        if (audioHandle != null) {
            Host.backend.audioStop(audioHandle);
            Host.backend.audioClose(audioHandle);
            audioHandle = null;
        }
        state = CLOSED;
        Media.unregister(this);
        post(PlayerListener.CLOSED, null);
    }

    private void checkClosed() {
        if (state == CLOSED) {
            throw new IllegalStateException("Player is closed");
        }
    }

    public long setMediaTime(long now) throws MediaException {
        checkClosed();
        if (now < 0) {
            now = 0;
        }
        if (audioHandle != null) {
            Host.backend.audioSetPosition(audioHandle, now);
        }
        lastPos = now;
        return now;
    }

    public long getMediaTime() {
        if (audioHandle == null) {
            return TIME_UNKNOWN;
        }
        return Host.backend.audioPosition(audioHandle);
    }

    public int getState() {
        return state;
    }

    public long getDuration() {
        if (audioHandle == null) {
            return TIME_UNKNOWN;
        }
        return Host.backend.audioDuration(audioHandle);
    }

    public String getContentType() {
        return type;
    }

    public void setLoopCount(int count) {
        if (count == 0) {
            throw new IllegalArgumentException("loop count 0");
        }
        loopCount = count;
    }

    public void addPlayerListener(PlayerListener l) {
        if (l == null) {
            return;
        }
        if (listenerCount == listeners.length) {
            PlayerListener[] n = new PlayerListener[listenerCount * 2];
            System.arraycopy(listeners, 0, n, 0, listenerCount);
            listeners = n;
        }
        listeners[listenerCount++] = l;
    }

    public void removePlayerListener(PlayerListener l) {
        for (int i = 0; i < listenerCount; i++) {
            if (listeners[i] == l) {
                listenerCount--;
                for (int k = i; k < listenerCount; k++) {
                    listeners[k] = listeners[k + 1];
                }
                listeners[listenerCount] = null;
                return;
            }
        }
    }

    public Control[] getControls() {
        if (tempo != null) {
            return new Control[]{volume, tempo};
        }
        return new Control[]{volume};
    }

    public Control getControl(String controlType) {
        if (controlType == null) {
            throw new IllegalArgumentException();
        }
        if (controlType.endsWith("VolumeControl")) {
            return volume;
        }
        if (tempo != null && (controlType.endsWith("TempoControl") || controlType.endsWith("RateControl"))) {
            return tempo;
        }
        return null;
    }

    // events are delivered from Host.tick (asynchronously, like on a handset)
    private void post(String event, Object data) {
        Media.postEvent(this, event, data);
    }

    void deliver(String event, Object data) {
        for (int i = 0; i < listenerCount; i++) {
            try {
                listeners[i].playerUpdate(this, event, data);
            } catch (Exception e) {
                Host.logException("playerUpdate", e);
            }
        }
    }

    /** Called every tick: detects end of media / loop wrap-around. */
    void poll() {
        if (state != STARTED || audioHandle == null || pausedByHost) {
            return;
        }
        boolean playing = Host.backend.audioIsPlaying(audioHandle);
        long pos = Host.backend.audioPosition(audioHandle);
        if (!playing) {
            state = PREFETCHED;
            wasPlaying = false;
            post(PlayerListener.END_OF_MEDIA, null);
            return;
        }
        if (pos + 200000 < lastPos) {
            post(PlayerListener.END_OF_MEDIA, null); // a loop iteration finished
        }
        lastPos = pos;
    }

    void hostPause() {
        if (state == STARTED && audioHandle != null && !pausedByHost) {
            Host.backend.audioStop(audioHandle);
            pausedByHost = true;
        }
    }

    void hostResume() {
        if (pausedByHost) {
            pausedByHost = false;
            if (state == STARTED && audioHandle != null) {
                Host.backend.audioStart(audioHandle, loopCount);
            }
        }
    }
}
