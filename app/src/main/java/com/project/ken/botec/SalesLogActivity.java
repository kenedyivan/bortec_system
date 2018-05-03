package com.project.ken.botec;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.ken.botec.models.SalesLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SalesLogActivity extends AppCompatActivity {

    private String TAG = SalesLogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_log);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        getMySalesLog(Integer.parseInt(new SessionManager(SalesLogActivity.this).getUserID()));
    }

    private void getMySalesLog(int operatorId) {
        RequestParams params = new RequestParams();
        params.put("operator_id", operatorId);

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(SalesLogActivity.this);
        mProgressDialog.setMessage("loading........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.mySalesLog(), params, new AsyncHttpResponseHandler() {

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

                if (SalesLog.ITEMS.size() > 0) {
                    SalesLog.ITEMS.clear();
                }


                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    int success = jsonObject.getInt("success");
                    int error = jsonObject.getInt("error");

                    Log.d("Success: ", String.valueOf(success));
                    Log.d("Error: ", String.valueOf(error));

                    if (error == 0 && success == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("logs");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject0 = jsonArray.getJSONObject(i);
                            String id = jsonObject0.getString("id");
                            String itemName = jsonObject0.getString("item_name");
                            String quantity = jsonObject0.getString("quantity");
                            String date = jsonObject0.getString("date");
                            String unit_price = jsonObject0.getString("unit_price");

                            SalesLog.addSaleLog(SalesLog.createLogItem(id, itemName, quantity, date, unit_price));
                        }

                        // Set the adapter

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.log_list);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SalesLogActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                linearLayoutManager.getOrientation());
                        recyclerView.addItemDecoration(mDividerItemDecoration);

                        recyclerView.setAdapter(new SalesLogRecyclerAdapter(SalesLog.ITEMS, SalesLogActivity.this));


                        Log.d("MYLOGS ", SalesLog.ITEMS.toString());

                    } else if (error == 1 && success == 0) {
                        Toast.makeText(SalesLogActivity.this, "No sales logs found", Toast.LENGTH_SHORT).show();

                    } else if (error == 2 && success == 0) {
                        Toast.makeText(SalesLogActivity.this, "No operator id found", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SalesLogActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "failed " + statusCode);
                mProgressDialog.dismiss();
                Toast.makeText(SalesLogActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                Toast.makeText(SalesLogActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
