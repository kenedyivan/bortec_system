package com.project.ken.botec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import at.markushi.ui.CircleButton;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = DrawerActivity.class.getSimpleName();
    CircleButton mStockInBtn;
    CircleButton mStockOutBtn;
    private static final int REQUEST_SCAN_IN = 0;
    private static final int REQUEST_SCAN_OUT = 1;
    private static final int REQUEST_TRANSACTION = 2;
    private static final int PICK_IMAGE_REQUEST = 6004;

    View dialogView;
    AlertDialog dialog;
    //User info
    CircleImageView mAvatarIv;

    //Navigation view
    View nView;
    CircleImageView avatar;
    TextView nName;
    TextView nEmail;

    SessionManager sessionManager;
    Map<String, String> userMap;
    private String imageName;

    @Override
    protected void onResume() {
        super.onResume();
        if (userMap.get("image") != null
                && !TextUtils.isEmpty(userMap.get("image"))) {
            Picasso.with(this)
                    .load(Constants.getUserImage(userMap.get("image")))
                    .placeholder(R.drawable.avatar)
                    .into(avatar);
        }
    }

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

        sessionManager = new SessionManager(this);
        userMap = sessionManager.getUserData();

        nView = navigationView.getHeaderView(0);
        avatar = nView.findViewById(R.id.dialog_avatar);
        nName = nView.findViewById(R.id.name);
        nEmail = nView.findViewById(R.id.auth_id);


        nName.setText(userMap.get("firstName") + " " + userMap.get("lastName"));
        nEmail.setText(userMap.get("authId"));

        Log.d("Image", userMap.get("image"));

        if (userMap.get("image") != null
                && !TextUtils.isEmpty(userMap.get("image"))) {
            Picasso.with(this)
                    .load(Constants.getUserImage(userMap.get("image")))
                    .placeholder(R.drawable.avatar)
                    .into(avatar);
        }

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
            updateUserInfoDialog();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(DrawerActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUserInfoDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_update_user_info, null);

        mAvatarIv = dialogView.findViewById(R.id.dialog_avatar);
        ImageView mEditImage = dialogView.findViewById(R.id.edit_image);
        final EditText mFirstName = dialogView.findViewById(R.id.first_name);
        final EditText mLastName = dialogView.findViewById(R.id.last_name);
        final EditText mEmail = dialogView.findViewById(R.id.email);
        final EditText mPhone = dialogView.findViewById(R.id.phone);

        mFirstName.setHint("First name");
        mFirstName.setText(userMap.get("firstName"));
        mLastName.setHint("Last name");
        mLastName.setText(userMap.get("lastName"));
        mEmail.setEnabled(false);
        mEmail.setText(userMap.get("authId"));
        mPhone.setHint("New password");


        if (userMap.get("image") != null
                && !TextUtils.isEmpty(userMap.get("image"))) {
            Picasso.with(this)
                    .load(Constants.getUserImage(userMap.get("image")))
                    .placeholder(R.drawable.avatar)
                    .into(mAvatarIv);
        }

        mEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DrawerActivity.this);

        builder.setView(dialogView);
        // Set other dialog properties
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                saveChanges(mFirstName.getText().toString(), mLastName.getText().toString(), mPhone.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // Create the AlertDialog
        dialog = builder.create();


        dialog.show();

    }

    private void saveChanges(String s, String s1, String s2) {
        RequestParams params = new RequestParams();
        params.put("id", sessionManager.getUserID());
        params.put("first_name", s);
        params.put("last_name", s1);
        params.put("password", s2);

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(DrawerActivity.this);
        mProgressDialog.setMessage("Saving........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.saveChangesUri(), params, new AsyncHttpResponseHandler() {

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
                        JSONObject user = jsonObject.getJSONObject("user");
                        String firstName = user.getString("first_name");
                        String lastName = user.getString("last_name");

                        sessionManager.updateData(firstName, lastName);

                        nName.setText(firstName + " " + lastName);

                        Toast.makeText(DrawerActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();

                    } else if (error == 1 && success == 0) {
                        Toast.makeText(DrawerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DrawerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                Toast.makeText(DrawerActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture: "), PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            final Uri saveUri = data.getData();
            assert saveUri != null;
            upload(saveUri);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void dumpImageMetaData(Uri uri) {

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                imageName = displayName;

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void upload(Uri saveUri) {
        mAvatarIv.setImageURI(saveUri);
        dumpImageMetaData(saveUri);
        String str = getImageBase64(saveUri);

        RequestParams params = new RequestParams();
        params.put("encoded_string", str);
        params.put("image", imageName);
        params.put("id", sessionManager.getUserID());

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(DrawerActivity.this);
        mProgressDialog.setMessage("Saving........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout((50 * 1000));
        client.setResponseTimeout((50 * 1000));
        client.post(Constants.upload(), params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(TAG, "Started request");
                mProgressDialog.show();

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                mProgressDialog.dismiss();
                Log.d(TAG, "Status: " + statusCode);
                String resp = new String(response);
                Log.d(TAG, "Response: " + resp);

                //End work from here

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String image = jsonObject.getString("image");
                    if (image != null
                            && !TextUtils.isEmpty(image)) {
                        Picasso.with(DrawerActivity.this)
                                .load(Constants.getUserImage(image))
                                .placeholder(R.drawable.avatar)
                                .into(avatar);
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
                Toast.makeText(DrawerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(TAG, "retryNO: " + retryNo);
                mProgressDialog.dismiss();
                Toast.makeText(DrawerActivity.this, "Taking too long", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getImageBase64(Uri selectedImage) {
        Bitmap myImg = null;
        try {
            myImg = decodeUri(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        return android.util.Base64.encodeToString(byte_arr, 0);
    }

    //Reducing Image Size of a selected Image
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 500;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
