package com.mattcramblett.picsule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joebu on 4/4/2017.
 */

public class BitmapCacheLoader extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private LruCache<String, Bitmap> cache;
    private int height;
    private int width;

    public BitmapCacheLoader(String url, LruCache<String, Bitmap> cache, int height, int width){
        this.url = url;
        this.cache = cache;
        this.height = height;
        this.width = width;
    }
    @Override
    protected Bitmap doInBackground(Void... args){
        Bitmap image = null;
        try {
            URL url = new URL(this.url);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        return image;
    }

    @Override
    protected void onPostExecute( Bitmap result )  {
        if(result != null){
            Bitmap scaled = scaleBitmap(RotateBitmap(result,90), height, width);
            if(scaled != null) {
                this.cache.put(url, scaled);
            }
        }
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {

        Bitmap output;
        output = Bitmap.createScaledBitmap(bitmap, wantedWidth,wantedHeight, false);

        return output;
    }
}
