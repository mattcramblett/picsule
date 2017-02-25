package com.mattcramblett.picsule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class SubmissionActivity extends AppCompatActivity {

    private Bitmap mPhoto;
    private ImageView mPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setup UI/Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        mPhotoView = (ImageView)findViewById(R.id.photo_preview);

        //Find the Bitmap (photo)
        byte[] byteArray = getIntent().getByteArrayExtra("BitmapImage");
        mPhoto = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //Display the Bitmap (photo)
        mPhotoView.setImageBitmap(mPhoto);
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
