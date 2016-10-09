package shutdown.chalmergps.jacobth.snapcha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText userText;
    private EditText passwordText;
    private Button loginButton;
    private final String status = "ok";
    public static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        userText = (EditText)findViewById(R.id.editUser);
        passwordText = (EditText)findViewById(R.id.editPassword);
        userText.setText("jacob");
        passwordText.setText("jacke1");

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userText.getText().toString();
                String password = passwordText.getText().toString();

                SharedPreferences deviceToken = getSharedPreferences(FirebaseInstanceIDService.PREFS_NAME, 0);
                String token = deviceToken.getString("token", "empty");

                String hash = SendObject.sendLogin(username, password, token);
                String stored_hash = hash.substring(0, hash.length() - 1);
                System.out.println(stored_hash.length());

                if(BCrypt.checkpw(password, stored_hash)) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    StartActivity.user = username;

                    SharedPreferences session = getSharedPreferences(SESSION_NAME, 0);
                    SharedPreferences.Editor editor = session.edit();
                    editor.putString("username", username);
                    editor.commit();
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Wrong Password or Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
