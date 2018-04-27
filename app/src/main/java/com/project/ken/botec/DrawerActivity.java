package com.project.ken.botec;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import at.markushi.ui.CircleButton;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CircleButton mStockInBtn;
    CircleButton mStockOutBtn;
    private static final int REQUEST_SCAN_IN = 0;
    private static final int REQUEST_SCAN_OUT = 1;
    private static final int REQUEST_TRANSACTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
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
            Intent i = new Intent(DrawerActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(getApplicationContext(), SalesLogActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_IN) {
            if (resultCode == RESULT_OK) {
                String scan = data.getExtras().getString(ScanActivity.SCAN_RESULT, "");
                Log.d("InScanResult", data.getExtras().getString(ScanActivity.SCAN_RESULT, ""));
                Intent i = new Intent(DrawerActivity.this, SubmitActivity.class);
                i.putExtra("scanResult", scan);
                i.putExtra("title", "Sale");
                startActivityForResult(i, REQUEST_TRANSACTION);
            }
        }

        if (requestCode == REQUEST_SCAN_OUT) {
            if (resultCode == RESULT_OK) {
                String scan = data.getExtras().getString(ScanActivity.SCAN_RESULT, "");
                Log.d("OutScanResult", data.getExtras().getString(ScanActivity.SCAN_RESULT, ""));
                Intent i = new Intent(DrawerActivity.this, SubmitActivity.class);
                i.putExtra("scanResult", scan);
                i.putExtra("title", "Receive");
                startActivityForResult(i, REQUEST_TRANSACTION);
            }
        }

        if (requestCode == REQUEST_TRANSACTION) {
            Log.d("Transaction", "Transaction successful");
        } else {
            Log.d("Transaction", "Transaction unsuccessful");
        }
    }
}
