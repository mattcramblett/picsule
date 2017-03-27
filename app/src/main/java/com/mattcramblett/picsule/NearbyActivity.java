package com.mattcramblett.picsule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class NearbyActivity extends AppCompatActivity {

    //Initialize all the views
    ImageView mPhotoView;
    private Button mNextButton;
    private Button mPreviousButton;

    //Initialize the data
    final ArrayList<String> mNearbyUrls = new ArrayList<String>();
    private int mIndex;
    private Map<String, Bitmap> mNearbyCache;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mDatabase.getReference();

    //Get activity context
    private Context mActivityContext;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        mPhotoView = (ImageView) findViewById(R.id.nearby_view);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mNextButton.setEnabled(false);
        mPreviousButton.setEnabled(false);
        mNearbyUrls.clear();
        mIndex = -1;
        mActivityContext = this.getApplicationContext();

        try {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            final double lat = location.getLatitude();
            final double lon = location.getLongitude();

            //Retrieve data from firebase
            mDatabaseReference.orderByChild("imageLat").startAt(lat-.006).endAt(lat+.006).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for(DataSnapshot child: children){
                        Image image = child.getValue(Image.class);
                        if(image.imageLon>lon-.006 && image.imageLon<lon+.006 && !mNearbyUrls.contains(image.imageURL)) {
                            mNearbyUrls.add(image.imageURL);
                            if (mIndex < mNearbyUrls.size() - 1) {
                                mNextButton.setEnabled(true);
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } catch(SecurityException e){
            Toast.makeText(mActivityContext, "No GPS permissions given", Toast.LENGTH_LONG).show();
        }

        initUI();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initUI() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextPhoto();
            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreviousPhoto();
            }
        });
    }

    private void getNextPhoto() {
        mIndex++;
        String url = mNearbyUrls.get(mIndex);
        new ImageLoader(mPhotoView, url).execute();
        if(mIndex>mNearbyUrls.size()-2){
            mNextButton.setEnabled(false);
        }
        if(mIndex>0){
            mPreviousButton.setEnabled(true);
        }
    }

    private void getPreviousPhoto() {
        mIndex--;
        String url = mNearbyUrls.get(mIndex);
        new ImageLoader(mPhotoView, url).execute();
        if(mIndex==0){
            mPreviousButton.setEnabled(false);
        }
        if(mIndex<mNearbyUrls.size()-1){
            mNextButton.setEnabled(true);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Nearby Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
