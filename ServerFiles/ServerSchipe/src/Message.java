public class Message {
    private String sender;
    private String msg;

    public Message(String sender, String msg) {
        this.sender = sender;
        this.msg = msg;
    }

    public String getSender() {
        return this.sender;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getPerfectMessage() {
        return ("<" + this.sender + ">" + ":" + this.msg);
    }
}
