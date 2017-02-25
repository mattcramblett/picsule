package com.mattcramblett.picsule;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.Random;

/**
 * Created by Joe on 2/23/2017.
 */

public class PhotoHelper {

    //Used to generate a random file name
    final static String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    final static int length = alphabet.length();
    private static final Random r = new Random();

    //Instance variables
    private String generated;
    private Uri content;
    private File file;
    private Context activityContext;


    public PhotoHelper(Context context) {
        activityContext = context;
        buildPhotoFilePath();
    }

    //Public methods to access photo information
    public String getName(){
        return generated;
    }
    public Uri getUri() { return content; }
    public File getFile() { return file; }

    //Private methods to set up the photo name, file and uri
    private void buildPhotoFilePath(){
        File filesDir = activityContext.getFilesDir();
        file = new File(filesDir, generateRandomFileName());
        content = FileProvider.getUriForFile(activityContext, activityContext.getPackageName() + ".provider",
                file);
    }

    private String generateRandomFileName(){
        generated = "";
        for(int i = 0; i<12; i++){
            generated += alphabet.charAt(r.nextInt(length));
        }
        generated = "img_" + generated + ".jpg";
        return generated;
    }
}
