package javax.microedition.media;

import j2me.Host;
import j2me.PlayerImpl;
import j2me.ResourceStream;

import java.io.IOException;
import java.io.InputStream;

public final class Manager {
    public static final String TONE_DEVICE_LOCATOR = "device://tone";
    public static final String MIDI_DEVICE_LOCATOR = "device://midi";

    private Manager() {
    }

    public static String[] getSupportedContentTypes(String protocol) {
        return new String[]{"audio/midi", "audio/x-wav", "audio/amr", "audio/mpeg"};
    }

    public static String[] getSupportedProtocols(String contentType) {
        return new String[]{"file", "device"};
    }

    public static Player createPlayer(String locator) throws IOException, MediaException {
        if (locator == null) {
            throw new IllegalArgumentException();
        }
        return new PlayerImpl(null, null, null);
    }

    public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
        if (stream == null) {
            throw new IllegalArgumentException("stream is null");
        }
        String path = null;
        byte[] data = null;
        if (stream instanceof ResourceStream) {
            path = ((ResourceStream) stream).getPath();
            data = ((ResourceStream) stream).remainingBytes();
        } else {
            data = ResourceStream.readAll(stream);
        }
        return new PlayerImpl(path, type, data);
    }

    public static void playTone(int note, int duration, int volume) throws MediaException {
        if (Host.backend != null) {
            Host.backend.log("playTone " + note);
        }
    }
}
