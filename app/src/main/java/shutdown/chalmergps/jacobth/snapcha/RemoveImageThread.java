package shutdown.chalmergps.jacobth.snapcha;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class RemoveImageThread extends Thread{

    private List<Sender> senderList;
    private List<Integer> indexList;
    private String send;
    private Thread t;

    public RemoveImageThread(List<Sender> senderList, String send) {
        this.senderList = senderList;
        this.send = send;
    }

    @Override
    public void run() {
        try{
            removeImage();
        }catch (Exception e) {

        }
    }

    public void start() {
        if(t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    private void removeImage() {
        Iterator<Sender> i = MainActivity.sendList.iterator();
        while (i.hasNext()) {
            Sender sender = i.next();

            try {
                // indexList.add(new Integer(i));
                System.out.println("win");
                URL url = new URL(Strings.SERVER_IP + "delimage");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "multipart/form-data");

                conn.setReadTimeout(35000);
                conn.setConnectTimeout(35000);

                String message = StartActivity.user + "," + sender.getSender() + "," + sender.getLink();

                DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
                ds.writeBytes(message);
                ds.flush();
                ds.close();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                in.close();

                i.remove();

                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        MainActivity.sendList.clear();
    }
}

