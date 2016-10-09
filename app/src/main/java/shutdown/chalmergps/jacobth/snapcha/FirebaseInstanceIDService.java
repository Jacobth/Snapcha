package shutdown.chalmergps.jacobth.snapcha;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static String message;
    public static final String PREFS_NAME = "token";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println(token);

        SharedPreferences deviceToken = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = deviceToken.edit();
        editor.putString("token", token);
        editor.commit();

        //message = SendObject.createAccount(SignActivity.username, SignActivity.password, SignActivity.email, token);
    }


}
