package j2me;

import java.io.IOException;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

/**
 * SMS is not available in the port: outgoing messages are logged and dropped, nothing is ever received.
 * (The game's purchase flow never actually called send() in this build.)
 */
public class SmsConnection implements MessageConnection {
    private final String url;

    public SmsConnection(String url) {
        this.url = url;
    }

    public Message newMessage(String type) {
        return new SmsMessage(type, null);
    }

    public Message newMessage(String type, String address) {
        return new SmsMessage(type, address);
    }

    public void send(Message msg) throws IOException {
        Host.log("SMS (not sent) to " + msg.getAddress());
    }

    public Message receive() throws IOException {
        throw new IOException("SMS receive is not supported");
    }

    public void setMessageListener(MessageListener l) throws IOException {
    }

    public int numberOfSegments(Message msg) {
        return 1;
    }

    public void close() throws IOException {
    }
}
