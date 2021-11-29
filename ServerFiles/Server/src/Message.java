import javax.xml.crypto.Data;

public class Message {
    private String receiver;
    private String msg;
    private Data data;

    public Message(String receiver, String msg) {
        this.receiver = receiver;
        this.msg = msg;
    }

    public String getreceiver() {
        return this.receiver;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getPerfectMessage() {
        return ("<" + this.receiver + ">" + ":" + this.msg);
    }
}
