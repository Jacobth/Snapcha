package shutdown.chalmergps.jacobth.snapcha;

public class Sender {

    private String sender;
    private String link;

    public Sender(String sender, String link) {
        this.sender = sender;
        this.link = link;
    }

    public String getSender() {
        return sender;
    }

    public String getLink() {
        return link;
    }
}

