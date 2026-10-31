package j2me;

import javax.microedition.media.control.VolumeControl;

public class VolumeControlImpl implements VolumeControl {
    private final PlayerImpl player;
    private int level = 100;
    private boolean muted;

    VolumeControlImpl(PlayerImpl p) {
        player = p;
    }

    public void setMute(boolean mute) {
        muted = mute;
        player.applyVolume();
    }

    public boolean isMuted() {
        return muted;
    }

    public int setLevel(int l) {
        if (l < 0) {
            l = 0;
        }
        if (l > 100) {
            l = 100;
        }
        level = l;
        player.applyVolume();
        return level;
    }

    public int getLevel() {
        return level;
    }
}
