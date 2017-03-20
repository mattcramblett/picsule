package com.mattcramblett.picsule;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

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

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
//AIzaSyDTUDzGBgyfg0GLpbpbnN2IhaKheY5FU9U maps api key

public class ExploreActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int LOCAT_PERMISSION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 1;
    private LatLng mDefaultLocation = new LatLng(0, 0);
    private boolean mLocationPermissionGranted = true;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private MapFragment mMapFragment;

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

        LatLng bobaksOldApt = new LatLng(40.0074380, -83.0062790);
        //LatLng currentLatLng = new LatLng(0,0);
        LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        map.addMarker(new MarkerOptions().position(bobaksOldApt).title("Bobaks Old Apartment"));
        map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
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
