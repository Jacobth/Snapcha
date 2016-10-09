package shutdown.chalmergps.jacobth.snapcha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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

public class SignActivity extends AppCompatActivity {

    private EditText emailEdit;
    private EditText userEdit;
    private EditText passwordEdit;
    private Button createButton;

    private String username;
    private String password;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        emailEdit = (EditText)findViewById(R.id.editNewEmail);
        userEdit = (EditText)findViewById(R.id.editNewUser);
        passwordEdit = (EditText)findViewById(R.id.editNewPassword);

        createButton = (Button)findViewById(R.id.accountButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userEdit.getText().toString();
                password = passwordEdit.getText().toString();
                email = emailEdit.getText().toString();

                String pw_hash = BCrypt.hashpw(password, BCrypt.gensalt());

                String message = SendObject.createAccount(username, pw_hash, email);

                Toast.makeText(SignActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

