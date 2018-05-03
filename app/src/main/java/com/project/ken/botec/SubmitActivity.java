package com.project.ken.botec;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SubmitActivity extends AppCompatActivity {
    Button mSubmitButton;
    EditText mScan;
    EditText mProductNameEt;
    EditText mUnitEt;
    EditText mQuantityEt;
    EditText mLabel;
    private String TAG = SubmitActivity.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    boolean isSale;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        //Sets actionbar back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String scanResult = getIntent().getStringExtra("scanResult");
        String title = getIntent().getStringExtra("title");

        getSupportActionBar().setTitle(title);

        isSale = Objects.equals(title, "Sale");

        getProductDetails(scanResult);

        mSubmitButton = findViewById(R.id.submit_button);

        mScan = findViewById(R.id.scan);
        mScan.setEnabled(false);

        mScan.setText(scanResult);

        mProductNameEt = findViewById(R.id.product_name);
        mProductNameEt.setEnabled(false);

        mUnitEt = findViewById(R.id.units);
        mUnitEt.setEnabled(false);

        mQuantityEt = findViewById(R.id.quantity);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mQuantityEt.getText().toString())){
                    submitTransaction(scanResult, Integer.parseInt(mQuantityEt.getText().toString()),
                            Integer.parseInt(new SessionManager(SubmitActivity.this).getUserID()));
                }else{
                    Toast.makeText(SubmitActivity.this,"Quantiy empty", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProductDetails(String codes) {
        RequestParams params = new RequestParams();
        params.put("codes", codes);

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(SubmitActivity.this);
        mProgressDialog.setMessage("Wait........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.getProductDetails(), params, new AsyncHttpResponseHandler() {

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

                    Log.d("Success: ", String.valueOf(success));
                    Log.d("Error: ", String.valueOf(error));

                    if (error == 0 && success == 1) {

                        mProductNameEt.setText(jsonObject.getString("item_name"));
                        mUnitEt.setText(jsonObject.getString("units"));

                    } else if (error == 1 && success == 0) {
                        Toast.makeText(SubmitActivity.this, "Item details not found", Toast.LENGTH_SHORT).show();
                        mSubmitButton.setEnabled(false);

                    } else {
                        Toast.makeText(SubmitActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                Toast.makeText(SubmitActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitTransaction(String codes, int quantity, int operator_id) {
        RequestParams params = new RequestParams();
        params.put("codes", codes);
        params.put("quantity", quantity);
        params.put("operator_id", operator_id);

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(SubmitActivity.this);
        mProgressDialog.setMessage("Processing........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        String uri;
        if(isSale){
            uri = Constants.saleProduct();
        }else{
            uri = Constants.receiveProduct();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(uri, params, new AsyncHttpResponseHandler() {

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

                    Log.d("Success: ", String.valueOf(success));
                    Log.d("Error: ", String.valueOf(error));

                    if (error == 0 && success == 1) {
                        mediaPlayer = MediaPlayer.create(SubmitActivity.this, R.raw.plucky);
                        //mediaPlayer.setLooping(true);
                        mediaPlayer.start();

                        if(isSale){
                            Toast.makeText(SubmitActivity.this, "Sale captured", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SubmitActivity.this, "Received captured", Toast.LENGTH_SHORT).show();
                        }


                        getIntent().putExtra("type", "sale");

                        setResult(RESULT_OK, getIntent());

                        finish();

                    } else if (error == 1 && success == 0) {
                        Toast.makeText(SubmitActivity.this, "Item codes can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (error == 3 && success == 0) {
                        Toast.makeText(SubmitActivity.this, "Out of stock", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SubmitActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                Toast.makeText(SubmitActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }
}
