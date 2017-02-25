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

    final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    final int length = alphabet.length();
    private String generated;
    private Uri content;
    private File file;
    private Random r = new Random();
    private Context activityContext;

    public PhotoHelper(Context context) {
        activityContext = context;
        buildPhotoFilePath();
    }

    public String getName(){
        return generated;
    }
    public Uri getUri() { return content; }
    public File getFile() { return file; }

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
        return "img_" + generated + ".jpg";
    }
}
