package com.mattcramblett.picsule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private Button mPhotoButton;
    private Button mExploreButton;
    private Button mLogoutButton;
    private Button mNearbyButton;
    private Context mActivityContext;
    private PhotoHelper photo;
    private static final int REQUEST_PHOTO = 131;
    private static final int REQUEST_LOCATION_PERMISSIONS_NEARBY = 44;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION, };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mActivityContext = this.getApplicationContext();

        mPhotoButton = (Button) findViewById(R.id.photo_button);
        mExploreButton = (Button) findViewById(R.id.explore_button);
        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mNearbyButton = (Button) findViewById(R.id.nearby_button);

        initUI();
    }

    /**
     * *****************************
     * ACTIVITY LIFECYCLE LOGIC  *
     * *****************************
     */

    @Override
    protected void onStart() {
        System.out.println("onStart method for MenuActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart method for MenuActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause method for MenuActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume method for MenuActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop method for MenuActivity being called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy method for MenuActivity being called");
        super.onDestroy();
    }


    private void initUI() {

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Setting up button listeners
        mPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                photo = new PhotoHelper(mActivityContext);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photo.getUri());

                List<ResolveInfo> cameraActivities = mActivityContext.getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    mActivityContext.grantUriPermission(activity.activityInfo.packageName,
                            photo.getUri(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    mActivityContext.grantUriPermission(activity.activityInfo.packageName,
                            photo.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mLogoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(MenuActivity.this, LoginActivity.class);
                logout.putExtra("NAV_BACK", true);
                startActivity(logout);
            }
        });

        mExploreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explore = new Intent(MenuActivity.this, ExploreActivity.class);
                explore.putExtra("NAV_BACK", true);
                startActivity(explore);
            }
        });

        mNearbyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Intent nearby = new Intent(MenuActivity.this, NearbyActivity.class);
                    nearby.putExtra("NAV_BACK", true);
                    startActivity(nearby);
                }
                else{
                    alertNoGps();
                }
            }
        });
    }

    //ADDITIONAL METHODS

    /*
    This method will handle intents/activities that will give a result.  These intents
    are typically started with the startActivityForResult() method.  This method override will
    handle Activity Results for the following:
        1). Camera - Using ACTION_IMAGE_CAPTURE
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        System.out.println("onActivityResult has been called");
        if (requestCode == REQUEST_PHOTO){
            switch (resultCode){
                case Activity.RESULT_OK:
                    if(photo.getFile().exists()){

                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo.getUri());
                        startSubmissionIntent();

                    }
                    else{
                        Toast.makeText(this, "There was an error saving the photo.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "The photo will not be saved", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case REQUEST_LOCATION_PERMISSIONS_NEARBY:
                if(hasLocationPermission()){
                    Intent nearby = new Intent(MenuActivity.this, NearbyActivity.class);
                    nearby.putExtra("NAV_BACK", true);
                    startActivity(nearby);
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*
    This is a helper meant to start the Submission Activity
     */
    private void startSubmissionIntent(){

        String mPhotoLat = null;
        String mPhotoLon = null;
        try {
            ExifInterface gpsRetriever = new ExifInterface(photo.getFile().toString());
            mPhotoLat = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            mPhotoLon = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        } catch(IOException e){
            e.printStackTrace();
        }

        if(!(mPhotoLat == null || mPhotoLon == null)) {
            Intent submission = new Intent(MenuActivity.this, SubmissionActivity.class);
            submission.putExtra("ImageUri", photo.getUri().toString());
            submission.putExtra("ImageFile", photo.getFile().toString());
            submission.putExtra("ImageName", photo.getName());
            startActivity(submission);
        }else{
            //Toast for now, dialog later
            Toast.makeText(mActivityContext, "Turn on geo-tagging in camera settings", Toast.LENGTH_LONG).show();
        }


    }

    private boolean hasLocationPermission(){
        int result = ContextCompat.checkSelfPermission(mActivityContext, LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void alertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is disabled. We need it to find photos nearby. Would you like to turn it on?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
