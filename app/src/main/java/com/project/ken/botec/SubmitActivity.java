package com.project.ken.botec;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubmitActivity extends AppCompatActivity {
    Button mSubmitButton;
    EditText mScan;
    EditText mProductNameEt;
    EditText mQuantityEt;
    EditText mLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        //Sets actionbar back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String scanResult = getIntent().getStringExtra("scanResult");

        mSubmitButton = findViewById(R.id.submit_button);

        mScan = findViewById(R.id.scan);

        mScan.setText(scanResult);

        mProductNameEt = findViewById(R.id.product_name);
        mQuantityEt = findViewById(R.id.quantity);
        mLabel = findViewById(R.id.label);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDetails();
            }
        });
    }

    private void submitDetails(){

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
}
