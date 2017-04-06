package com.mattcramblett.picsule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joebu on 4/5/2017.
 */

public class MapIconLoader extends AsyncTask<Void, Void, Bitmap>{
    private Bitmap image;
    private GoogleMap map;
    private LatLng ltln;
    private String title;
    private int height;
    private int width;

    public MapIconLoader(Bitmap image, GoogleMap map, LatLng ltln, String title, int width, int height){
        this.image = image;
        this.map = map;
        this.ltln = ltln;
        this.title = title;
        this.height = height;
        this.width = width;
    }

    @Override
    protected Bitmap doInBackground(Void... args){
        Bitmap translated = null;
        translated = RotateBitmap(this.image, 90);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(translated, 110, 177, false));
        if(icon != null){
            map.addMarker(new MarkerOptions().position(ltln).title(title).icon(icon));
        }
        return translated;
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
