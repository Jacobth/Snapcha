package shutdown.chalmergps.jacobth.snapcha;

/**
 * Created by jacobth on 2016-09-30.
 */
public class TokenThread extends Thread{

    private Thread t;
    private String user;

    public TokenThread(String user) {
        this.user = user;
    }

    @Override
    public void run() {
        try{
            SendObject.updateToken(user);
        }catch (Exception e) {

        }
    }

    public void start() {
        if(t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}
