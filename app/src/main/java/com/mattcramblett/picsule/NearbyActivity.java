package com.mattcramblett.picsule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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

import static com.mattcramblett.picsule.R.attr.colorPrimary;
import static com.mattcramblett.picsule.R.attr.colorPrimaryDark;

public class NearbyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //Initialize all the views
    ImageView mPhotoView;
    private Button mNextButton;
    private Button mPreviousButton;

    //Initialize the data
    final ArrayList<String> mNearbyUrls = new ArrayList<String>();
    private int mIndex;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mDatabase.getReference();
    int mViewHeight = 0;
    int mViewWidth = 0;

    //Request codes
    final static int EXPLORE_REQUEST_LOCATION = 111;
    final static int NEARBY_REQUEST_LOCATION = 222;

    //Google api client
    private GoogleApiClient client;

    //Get activity context
    private Context mActivityContext;

    //Create a cache shared across all instances of this activity
    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/6th of the available memory for this memory cache.
    final static int cacheSize = maxMemory / 3;
    private static LruCache<String, Bitmap> mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        //Setup regarding views
        mPhotoView = (ImageView) findViewById(R.id.nearby_view);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mNextButton.setEnabled(false);
        mPreviousButton.setEnabled(false);
        setDisabledColor(mPreviousButton);
        setDisabledColor(mNextButton);

        //Setup regarding private data
        mNearbyUrls.clear();
        mIndex = -1;

        //Get the activity context
        mActivityContext = this.getApplicationContext();

        //Initialize the UI (Listeners and such)
        initUI();

        //Build a Google API client
        client = new GoogleApiClient.Builder(mActivityContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /*
    A method to initialize UI elements
     */
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
        mPhotoView.setVisibility(View.VISIBLE);
        ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                mPhotoView.measure(0,0);
                mViewHeight = mPhotoView.getMeasuredHeight();
                mViewWidth = mPhotoView.getMeasuredWidth();
                return true;
            }
        });
    }
    /*
    A method for handling getting the next photo to display to the user
     */
    private void getNextPhoto() {
        mIndex++;

        //Get and load the photo
        String url = mNearbyUrls.get(mIndex);
        Bitmap b = mBitmapCache.get(url);
        if(b != null){
            mPhotoView.setImageBitmap(b);
        }else {
            Glide.with(mActivityContext).load(url).override(mViewWidth, mViewHeight).crossFade().into(mPhotoView);
            new BitmapCacheLoader(url, mBitmapCache, mViewHeight, mViewWidth).execute();
        }


        //If there is not a next photo
        if(mIndex>mNearbyUrls.size()-2){
            mNextButton.setEnabled(false);
            setDisabledColor(mNextButton);
        }

        //If there is a previous photo
        if(mIndex>0){
            mPreviousButton.setEnabled(true);
            setEnabledColor(mPreviousButton);
        }

        updateCache(mIndex);
    }

    /*
    A method to handle getting the last photo viewed (previous) by the user
     */
    private void getPreviousPhoto() {
        mIndex--;

        //Get and load the photo
        String url = mNearbyUrls.get(mIndex);
        Bitmap b = mBitmapCache.get(url);
        if(b != null){
            mPhotoView.setImageBitmap(b);
        }else {
            Glide.with(mActivityContext).load(url).override(mViewWidth,mViewHeight).crossFade().into(mPhotoView);
            new BitmapCacheLoader(url, mBitmapCache, mViewHeight, mViewWidth).execute();
        }

        //If there is not a previous photo
        if(mIndex==0){
            mPreviousButton.setEnabled(false);
            setDisabledColor(mPreviousButton);
        }
        //If there is a next photo
        if(mIndex<mNearbyUrls.size()-1){
            mNextButton.setEnabled(true);
            setEnabledColor(mNextButton);
        }

        updateCache(mIndex);
    }

    private void updateCache(int index){
        for(int i = 1; i<=3; i++){
            if(index+i<=mNearbyUrls.size()-1){
                String url = mNearbyUrls.get(index+1);
                if(mBitmapCache.get(url) == null){
                    new BitmapCacheLoader(url, mBitmapCache, mViewHeight, mViewWidth).execute();
                }
            }
        }
    }

    private void setEnabledColor(Button b){
        b.getBackground().setColorFilter(null);
        b.setTextColor(Color.WHITE);
    }
    private void setDisabledColor(Button b){
        b.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        b.setTextColor(Color.LTGRAY);
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

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mNearbyUrls.clear();
    }

    //Google API overrides

    /*
    Once the client connects, then we can begin finding the location and nearby photos
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = getIntent();
        if(intent.hasExtra("Coords")){
            LatLng coords = intent.getParcelableExtra("Coords");
            final double lat = coords.latitude;
            final double lon = coords.longitude;
            //Retrieve data from firebase
            mDatabaseReference.orderByChild("imageLat").startAt(lat - .004).endAt(lat + .004).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    int entriesAdded = 0;
                    for (DataSnapshot child : children) {
                        Image image = child.getValue(Image.class);
                        if (image.imageLon > lon - .004 && image.imageLon < lon + .004 && !mNearbyUrls.contains(image.imageURL)) {
                            mNearbyUrls.add(image.imageURL);
                            if (mIndex < mNearbyUrls.size() - 1) {
                                mNextButton.setEnabled(true);
                                setEnabledColor(mNextButton);
                            }
                            if (entriesAdded < 3) {
                                entriesAdded++;
                                new BitmapCacheLoader(image.imageURL, mBitmapCache, mViewHeight, mViewWidth).execute();
                            }
                        }
                    }
                    updateCache(mIndex);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                alertNoGps();
            } else {
                try {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                    if (location != null) {
                        final double lat = location.getLatitude();
                        final double lon = location.getLongitude();
                        //Retrieve data from firebase
                        mDatabaseReference.orderByChild("imageLat").startAt(lat - .005).endAt(lat + .005).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                int entriesAdded = 0;
                                for (DataSnapshot child : children) {
                                    Image image = child.getValue(Image.class);
                                    if (image.imageLon > lon - .005 && image.imageLon < lon + .005 && !mNearbyUrls.contains(image.imageURL)) {
                                        mNearbyUrls.add(image.imageURL);
                                        if (mIndex < mNearbyUrls.size() - 1) {
                                            mNextButton.setEnabled(true);
                                            setEnabledColor(mNextButton);
                                        }
                                        if (entriesAdded < 3) {
                                            entriesAdded++;
                                            new BitmapCacheLoader(image.imageURL, mBitmapCache, mViewHeight, mViewWidth).execute();
                                        }
                                    }
                                }
                                updateCache(mIndex);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(mActivityContext, "Cannot find your location", Toast.LENGTH_LONG).show();
                        Log.d("Location", "Null location value");
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
