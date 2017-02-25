package com.mattcramblett.picsule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    //TODO: Write the app.
    private Context mActivityContext;
    private PhotoHelper photo;
    private static final int REQUEST_PHOTO = 131;
    Uri photoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mActivityContext = this.getApplicationContext();
        Button btnCapture = (Button) findViewById(R.id.photo_button);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Setting up button listeners
        btnCapture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                photo = new PhotoHelper(mActivityContext);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photo.getUri());

                List<ResolveInfo> cameraActivities = mActivityContext.getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    mActivityContext.grantUriPermission(activity.activityInfo.packageName,
                            photo.getUri(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
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

    //ADDITIONAL METHODS
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        System.out.println("onActivityResult has been called");
        if (requestCode == REQUEST_PHOTO){
            switch (resultCode){
                case Activity.RESULT_OK:
                    if(photo.getFile().exists()){

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo.getUri());
                            startSubmissionIntent(bitmap);
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

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

    private void startSubmissionIntent(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent submission = new Intent(MenuActivity.this, SubmissionActivity.class);
        submission.putExtra("BitmapImage", byteArray);
        startActivity(submission);

    }
}
