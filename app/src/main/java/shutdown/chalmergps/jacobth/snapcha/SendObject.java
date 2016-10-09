package shutdown.chalmergps.jacobth.snapcha;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SendObject {

    public static void sendImage(String receiver) {
        try {
            //Thread.sleep(2000);
            URL url = new URL(Strings.SERVER_IP + "upload-image");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            String message = "start" + StartActivity.user + "," + receiver;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            int compress = 75;

            Bitmap b = BitmapFactory.decodeByteArray(CameraFragment.image, 0, CameraFragment.image.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth()/2, b.getHeight()/2, matrix, false);

            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, blob);
            byte[] data = blob.toByteArray();

            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(message);
            os.write(data);
            osw.flush();
            osw.close();

            os.flush();
            os.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            conn.disconnect();
            ContactFragment.iconMap.put(receiver, R.mipmap.sent);
            //CameraActivity.sentToList.add(receiver);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getImage(Sender sender) {
        try {
            URL url = new URL(Strings.SERVER_IP + "getimages");
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
            //  Drawable d = Drawable.createFromStream(in, "imagename");
            Bitmap b = BitmapFactory.decodeStream(in);

            if (b != null) {
                List<Bitmap> bitmaps = MainActivity.imageMap.get(sender.getSender());
                if(bitmaps == null) {
                    bitmaps = new ArrayList<>();
                }
                bitmaps.add(b);
                MainActivity.imageMap.put(sender.getSender(), bitmaps);
                System.out.println("size of photmap: " + bitmaps.size());
            }
            conn.disconnect();
            ContactFragment.iconMap.remove(sender.getSender());
            ContactFragment.iconMap.put(sender.getSender(), R.mipmap.recive);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String createAccount(String username, String password, String email) {
        try
        {
            URL url = new URL(Strings.SERVER_IP + "create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            //conn.setFixedLengthStreamingMode(1024);

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            // directly let .compress write binary image data
            // to the output-stream
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(username + "," + password + "," + email);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            conn.disconnect();
            return response;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendLogin(String username, String password, String token) {
        try
        {
            URL url = new URL(Strings.SERVER_IP + "login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            //conn.setFixedLengthStreamingMode(1024);

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            // directly let .compress write binary image data
            // to the output-stream
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(username + "," + password + "," + token);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            conn.disconnect();
            return response;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMessage() {
        try
        {
            URL url = new URL(Strings.SERVER_IP + "senders");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            //conn.setFixedLengthStreamingMode(1024);

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            String message = StartActivity.user;

            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(message);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            if(response.length() > 0)
                response = response.substring(0,response.length()-2);

            System.out.println("any senders? " + response);

            conn.disconnect();
            return response;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getContacts() {
        try
        {
            System.out.println("not even called?");
            URL url = new URL(Strings.SERVER_IP + "contacts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            String message = StartActivity.user;
            System.out.println("Message in sendObject: " + message);

            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(message);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            if(response.length() > 1)
                response = response.substring(0,response.length()-2);
            System.out.println(response);

            String[] contactList = null;
            contactList = response.split("\\,");

            for(String string : contactList)
                System.out.println(string);

            System.out.println(contactList.length);

            conn.disconnect();
            return contactList;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static void updateToken(String username) {
        try
        {
            URL url = new URL(Strings.SERVER_IP + "deltoken");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            //conn.setFixedLengthStreamingMode(1024);

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            // directly let .compress write binary image data
            // to the output-stream
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(username);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            conn.disconnect();
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendOpen(String from, String to) {
        try
        {
            URL url = new URL(Strings.SERVER_IP + "open");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            //conn.setFixedLengthStreamingMode(1024);

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            // directly let .compress write binary image data
            // to the output-stream
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(from + "," + to);
            ds.flush();
            ds.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            conn.disconnect();
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}

