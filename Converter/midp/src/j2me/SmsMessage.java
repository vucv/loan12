package j2me;

import java.util.Date;

import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.TextMessage;

public class SmsMessage implements TextMessage, BinaryMessage {
    private final String type;
    private String address;
    private String text;
    private byte[] data;

    public SmsMessage(String type, String address) {
        this.type = type;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addr) {
        address = addr;
    }

    public Date getTimestamp() {
        return null;
    }

    public String getPayloadText() {
        return text;
    }

    public void setPayloadText(String s) {
        text = s;
    }

    public byte[] getPayloadData() {
        return data;
    }

    public void setPayloadData(byte[] d) {
        data = d;
    }
}
