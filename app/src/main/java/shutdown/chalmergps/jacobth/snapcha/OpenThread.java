package shutdown.chalmergps.jacobth.snapcha;

/**
 * Created by jacobth on 2016-10-08.
 */
public class OpenThread extends Thread{

    private String from;
    private String to;
    private Thread t;

    public OpenThread(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        try{
            sendOpen();
        }catch (Exception e) {

        }
    }

    public void start() {
        if(t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    private void sendOpen() {
        SendObject.sendOpen(from, to);
    }
}