package shutdown.chalmergps.jacobth.snapcha;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

public class StartActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
SignupFragment.OnFragmentInteractionListener, LoginFragment.OnLogListener, SignupFragment.OnSignListener{

    private Button logButton;
    private Button signButton;
    public static String user;
    public static final String SESSION_NAME = "session";
    private SignupFragment signupFragment;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        logButton = (Button)findViewById(R.id.logButton);
        signButton = (Button)findViewById(R.id.signButton);

        SharedPreferences deviceToken = getSharedPreferences(SESSION_NAME, 0);
        String session = deviceToken.getString("username", "");

        System.out.println(session);

        if(session != "") {
            user = session;
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment fragment = new LoginFragment();
                replaceFragment(fragment);
                loginFragment = fragment;
            }
        });

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupFragment fragment = new SignupFragment();
                replaceFragment(fragment);
                signupFragment = fragment;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void replaceFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.start_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onLogin(List<String> list) {
        System.out.println("started");
        loginFragment.progressBar.setVisibility(View.VISIBLE);
        loginFragment.loginButton.setText("");
        loginFragment.wrongText.setVisibility(View.INVISIBLE);
        new LoginTask().execute(list);
    }

    @Override
    public void onSignUp(List<String> list) {
        if(signupFragment.progressBar != null) {
            signupFragment.progressBar.setVisibility(View.VISIBLE);
            signupFragment.createButton.setText("");
        }
        new SignUpTask().execute(list);
    }

    private class LoginTask extends AsyncTask<List<String>, Integer, Long> {
        boolean isWrong = true;
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected Long doInBackground(List<String>... params) {
            String username = params[0].get(0);
            String password = params[0].get(1);
            String token = params[0].get(2);

            String hash = SendObject.sendLogin(username, password, token);
            String stored_hash = hash.substring(0, hash.length() - 1);
            System.out.println(stored_hash.length());

            if(BCrypt.checkpw(password, stored_hash)) {
                isWrong = false;
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                startActivity(i);
                StartActivity.user = username;

                SharedPreferences session = getSharedPreferences(StartActivity.SESSION_NAME, 0);
                SharedPreferences.Editor editor = session.edit();
                editor.putString("username", username);
                editor.commit();
                finish();
            }
            return new Long(0);
        }

        @Override
        protected void onPostExecute(Long result) {
            loginFragment.progressBar.setVisibility(View.INVISIBLE);
            loginFragment.loginButton.setText("Log In");
            if(isWrong)
                loginFragment.wrongText.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class SignUpTask extends AsyncTask<List<String>, Integer, Long> {
        boolean isWrong = true;
        String message;
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected Long doInBackground(List<String>... params) {
            String username = params[0].get(0);
            String password = params[0].get(1);
            String email = params[0].get(2);

            message = SendObject.createAccount(username, password, email);

            return new Long(0);
        }

        @Override
        protected void onPostExecute(Long result) {
            signupFragment.progressBar.setVisibility(View.INVISIBLE);
            signupFragment.createButton.setText("Sign Up");
            String m = message.substring(0, message.length() - 1);

            Toast.makeText(StartActivity.this, m, Toast.LENGTH_SHORT).show();

            System.out.println(m);

            if(m.equals("User created")) {
                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.start_container)).commit();
                System.out.println("truer");
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
