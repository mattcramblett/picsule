package com.mattcramblett.picsule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.net.URL;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;
//AIzaSyDTUDzGBgyfg0GLpbpbnN2IhaKheY5FU9U maps api key

public class ExploreActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int LOCAT_PERMISSION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 1;
    private static final double MAPBOUNDSTHRESHOLD = .0002;
    private LatLng mDefaultLocation = new LatLng(0, 0);
    private boolean mLocationPermissionGranted = true;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private MapFragment mMapFragment;
    private Uri mPhotoUri;
    private GoogleApiClient client;
    private Map<String, LatLng> imagesToLoad;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate method for ExploreActivity being called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);


        //connect to the google api
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */,
                this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);

        //update the location UI
        updateLocationUI();
        //get the current location
        getDeviceLocation();

        LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(currentLatLng.latitude - MAPBOUNDSTHRESHOLD, currentLatLng.longitude - MAPBOUNDSTHRESHOLD),
                new LatLng(currentLatLng.latitude + MAPBOUNDSTHRESHOLD, currentLatLng.longitude + MAPBOUNDSTHRESHOLD));

      //  map.setMinZoomPreference(15);
        final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://picsule-eb269.appspot.com");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://picsule-eb269.firebaseio.com/");
        mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();
                int i = 0;
                for (HashMap.Entry<String, Object> entry : data.entrySet()) {
                    //get image map
                    HashMap<String, Object> singleImage = (HashMap) entry.getValue();
                    //get lat/lon field and append if nearby

                    //get the properties of the photo
                    final String name =  (String) singleImage.get("imageName");
                    String imUrl =  (String) singleImage.get("imageURL");
                    final LatLng ltln = new LatLng((double) singleImage.get("imageLat"), (double) singleImage.get("imageLon"));

                    //download the photo
                    StorageReference imageRef = storageRef.child("images/" + name);
                    final long THREE_MEGABYTE = 3072 * 3072;
                    imageRef.getBytes(THREE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //ON successful download, add a marker to the map with an icon
                            Bitmap b = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, 200, 200, false));
                            mMap.addMarker(new MarkerOptions().position(ltln).title(name).icon(icon));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("failed to download image");
                        }
                    });;
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //database error
            }
        });
    }


    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        //print the current location to the system
        System.out.println("Lat: " + mLastKnownLocation.getLatitude() + " Long: " + mLastKnownLocation.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    //set the UI for myLocation on the map
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }


    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        //create the map fragment when connected
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
    /**
     * *****************************
     * ACTIVITY LIFECYCLE LOGIC  *
     * *****************************
     */

    @Override
    protected void onStart() {
        System.out.println("onStart method for ExploreActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart method for ExploreActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause method for ExploreActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume method for ExploreActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop method for ExploreActivity being called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy method for ExploreActivity being called");
        super.onDestroy();
    }
}
