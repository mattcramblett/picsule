package com.mattcramblett.picsule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SubmissionActivity extends AppCompatActivity {

    private Bitmap mPhoto;
    private ImageView mPhotoView;
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setup UI/Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        mPhotoView = (ImageView)findViewById(R.id.photo_preview);
        mPhotoUri = Uri.parse(getIntent().getExtras().getString("ImageUri"));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mPhotoUri);
            mPhotoView.setImageBitmap(bitmap);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        //Find the Bitmap (photo)
        //byte[] byteArray = getIntent().getByteArrayExtra("BitmapImage");
        //mPhoto = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //Display the Bitmap (photo)
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

}
