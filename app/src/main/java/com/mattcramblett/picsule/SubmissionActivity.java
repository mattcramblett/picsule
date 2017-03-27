package com.mattcramblett.picsule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SubmissionActivity extends AppCompatActivity {

    private Bitmap mPhoto;
    private ImageView mPhotoView;
    private TextView mPhotoCaption;
    private Button mSubmit;
    private String mPhotoFile;
    private double mPhotoLat;
    private double mPhotoLon;
    private double mLongitude;
    private double mLatitude;
    private Uri mPhotoUri;
    private String mPhotoName;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setup UI/Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        mPhotoView = (ImageView) findViewById(R.id.photo_preview);
        mPhotoCaption = (TextView) findViewById(R.id.photo_caption);
        mSubmit = (Button) findViewById(R.id.submit_button);
        mPhotoUri = Uri.parse(getIntent().getExtras().getString("ImageUri"));
        mPhotoFile = getIntent().getExtras().getString("ImageFile");
        mPhotoName = getIntent().getExtras().getString("ImageName");

        initUI();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        System.out.println("onStart method for SubmissionActivity being called");
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart method for SubmissionActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause method for SubmissionActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume method for SubmissionActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop method for SubmissionActivity being called");
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy method for SubmissionActivity being called");
        super.onDestroy();
    }

    private void initUI() {

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPhoto();
            }
        });
        getPhoto();
        getCoordinates();
        mPhotoView.setImageBitmap(mPhoto);
        mPhotoCaption.setText("Latitude: " + mLatitude + " Longitude: " + mLongitude);
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void getCoordinates() {
        try {
            ExifInterface gpsRetriever = new ExifInterface(mPhotoFile);
            String Lat = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String Lon = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String Lat_REF = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String Lon_REF = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);


            if((Lat !=null) && (Lat_REF !=null) && (Lon != null) && (Lon_REF !=null))
            {
                if(Lat_REF.equals("N")){
                    mLatitude = convertToDegree(Lat);
                }
                else{
                    mLatitude = 0 - convertToDegree(Lat);
                }

                if(Lon_REF.equals("E")){
                    mLongitude = convertToDegree(Lon);
                }
                else{
                    mLongitude = 0 - convertToDegree(Lon);
                }

            } else{
                //geo-tagging is off
            }
            System.out.println("EXIF lat: " + Lat + " long: " + Lon);


            System.out.println("lat: " + mLatitude + " long: " + mLongitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return (String.valueOf(mLatitude)
                + ", "
                + String.valueOf(mLongitude));
    }

    public int getLatitudeE6(){
        return (int)(mLatitude*1000000);
    }

    public int getLongitudeE6(){
        return (int)(mLongitude*1000000);
    }



    private void getPhoto() {
        try {
            //Get photo details
            mPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mPhotoUri);
            mPhoto = RotateBitmap(mPhoto, 90);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void submitPhoto() {
        DatabaseHelper database = new DatabaseHelper();
        database.uploadPhoto(mPhotoUri, mPhotoName, mLatitude, mLongitude);
        Intent menu = new Intent(this, MenuActivity.class);
        startActivity(menu);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Submission Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
