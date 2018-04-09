package com.project.ken.botec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
