package shutdown.chalmergps.jacobth.snapcha;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    public static boolean hasImage;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("sent");
        String message = remoteMessage.getData().get("message");
        System.out.println(message);
        if(message.startsWith("!")) {
            System.out.println("true");
            onOpen(message.substring(1));
        }
        else {
            showNotification(remoteMessage.getData().get("message"));
            hasImage = true;
        }
    }

    private void showNotification(String message) {
        Intent intent = new Intent("load");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Snapcha")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }

    private void onOpen(String message) {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

