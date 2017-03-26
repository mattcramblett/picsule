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
    private String mPhotoLat;
    private String mPhotoLon;
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
        getCoordinates();
        getPhoto();
        mPhotoView.setImageBitmap(mPhoto);
        mPhotoCaption.setText("Latitude: " + mPhotoLat + " Longitude: " + mPhotoLon);
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void getCoordinates() {
        try {
            ExifInterface gpsRetriever = new ExifInterface(mPhotoFile);
            mPhotoLat = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            mPhotoLon = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        database.uploadPhoto(mPhotoUri, mPhotoName);
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
