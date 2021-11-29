import javax.xml.crypto.Data;

public class Message {
    private String receiver;
    private String msg;
    private String data;

    public Message(String receiver, String msg, String data) {
        this.receiver = receiver;
        this.msg = msg;
        this.data = data;
    }

    public String getreceiver() {
        return this.receiver;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getPerfectMessage() {
        return ("[" + data + "]<" + this.receiver + ">" + ":" + this.msg);
    }
}
