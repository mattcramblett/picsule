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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SubmissionActivity extends AppCompatActivity {

    private Bitmap mPhoto;
    private ImageView mPhotoView;
    private TextView mPhotoCaption;
    private Button mSubmit;
    private String mPhotoFile;
    private String mPhotoLat;
    private String mPhotoLon;
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setup UI/Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        mPhotoView = (ImageView)findViewById(R.id.photo_preview);
        mPhotoCaption = (TextView)findViewById(R.id.photo_caption);
        mSubmit = (Button)findViewById(R.id.submit_button);
        mPhotoUri = Uri.parse(getIntent().getExtras().getString("ImageUri"));
        mPhotoFile = getIntent().getExtras().getString("ImageFile");


        initUI();
    }

    @Override
    protected void onStart() {
        System.out.println("onStart method for SubmissionActivity being called");
        super.onStart();
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
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy method for SubmissionActivity being called");
        super.onDestroy();
    }

    private void initUI(){

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPhoto();
            }
        });
        getCoordinates();
        getPhoto();
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void getCoordinates(){
        try {
            ExifInterface gpsRetriever = new ExifInterface(mPhotoFile);
            mPhotoLat = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            mPhotoLon = gpsRetriever.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void getPhoto(){
        try {
            //Get photo details
            mPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mPhotoUri);
            RotateBitmap(mPhoto, 90);
            //Set our views according to the details above
            mPhotoView.setImageBitmap(mPhoto);
            mPhotoCaption.setText("Latitude: " + mPhotoLat + " Longitude: " + mPhotoLon);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void submitPhoto(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapData = baos.toByteArray();

        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("https://picsule-eb269.firebaseio.com");
        StorageReference imagesRef = storageRef.child("images");

        UploadTask uploadTask = imagesRef.putBytes(bitmapData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });

        Intent menu = new Intent(this, MenuActivity.class);
    }
}
