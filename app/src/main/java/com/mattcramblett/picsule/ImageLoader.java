package com.mattcramblett.picsule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joebu on 3/27/2017.
 */

public class ImageLoader extends AsyncTask<Object, Void, Bitmap> {

    private ImageView imgView;
    private String path;
    private String url;

    public ImageLoader(ImageView view, String url){
        this.url = url;
        this.imgView = view;


    }

    @Override
    protected Bitmap doInBackground(Object... params){
        Bitmap image = null;
        String link = url;
        try {
            URL url = new URL(link);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            image = RotateBitmap(image, 90);
        } catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }
    @Override
    protected void onPostExecute(Bitmap result){

        if(result != null && imgView != null){
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(result);
        }else{
            imgView.setVisibility(View.GONE);
        }
    }

    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
