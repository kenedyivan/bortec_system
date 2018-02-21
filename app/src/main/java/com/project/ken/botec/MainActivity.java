package com.project.ken.botec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {
    CircleButton mStockInBtn;
    CircleButton mStockOutBtn;
    private static final int REQUEST_SCAN_IN = 0;
    private static final int REQUEST_SCAN_OUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStockInBtn = findViewById(R.id.stock_in_button);
        mStockOutBtn = findViewById(R.id.stock_out_button);

        mStockInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScanActivity.class);
                startActivityForResult(i, REQUEST_SCAN_IN);
            }
        });

        mStockOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScanActivity.class);
                startActivityForResult(i, REQUEST_SCAN_OUT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_IN) {
            if (resultCode == RESULT_OK) {
                String scan = data.getExtras().getString(ScanActivity.SCAN_RESULT,"");
                Log.d("InScanResult", data.getExtras().getString(ScanActivity.SCAN_RESULT,""));
                Intent i = new Intent(MainActivity.this, SubmitActivity.class);
                i.putExtra("scanResult",scan);
                startActivity(i);
            }
        }

        if (requestCode == REQUEST_SCAN_OUT) {
            if (resultCode == RESULT_OK) {
                Log.d("OutScanResult", data.getExtras().getString(ScanActivity.SCAN_RESULT,""));
            }
        }
    }
}
