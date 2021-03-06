package com.project.ken.botec;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText mAuthId;
    EditText mPasswordEt;
    Button mLoginButton;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "edwin:edwin", "emma:emma"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthId = findViewById(R.id.auth_id);
        mPasswordEt = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProcess();
            }
        });
    }

    private void loginProcess() {
        String authId = mAuthId.getText().toString();
        String password = mPasswordEt.getText().toString();

        processLogin(authId, password);
    }

    private boolean dummyLoginProcess(String sUsername, String sPassword) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(sUsername)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(sPassword);
            }
        }

        return false;
    }

    private void processLogin(String authId, String password) {
        RequestParams params = new RequestParams();
        params.put("auth_id", authId);
        params.put("password", password);

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setMessage("Logging in........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.loginUser(), params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(TAG, "Started request");
                mProgressDialog.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                Log.d(TAG, "Status: " + statusCode);
                String resp = new String(response);
                Log.d(TAG, "Response: " + resp);
                mProgressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    int success = jsonObject.getInt("success");
                    int error = jsonObject.getInt("error");
                    int id = jsonObject.getInt("id");

                    Log.d("Success: ", String.valueOf(success));
                    Log.d("Error: ", String.valueOf(error));

                    if (error == 0 && success == 1 && id != 0) {

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        JSONObject user = jsonObject.getJSONObject("user");
                        String firstName = user.getString("first_name");
                        String lastName = user.getString("last_name");
                        String authId = user.getString("auth_id");
                        String image = user.getString("image");

                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.createLoginSession(String.valueOf(id));
                        sessionManager.setUserData(firstName, lastName, authId, image);

                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        // Closing all the Activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(i);

                        finish();

                    } else if (error == 1 && success == 0) {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();

                    } else if (error == 2 && success == 0) {
                        Toast.makeText(LoginActivity.this, "Check your credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "failed " + statusCode);
                mProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                Toast.makeText(LoginActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
