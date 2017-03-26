package com.mattcramblett.picsule;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by joebu on 3/26/2017.
 */

public class DatabaseHelper {

    private static final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://picsule-eb269.appspot.com");
    private StorageReference imageRef;

    public DatabaseHelper(){

    }

    public void uploadPhoto(Uri uri, String fileName){

        imageRef = storageRef.child("images/" + fileName);

        UploadTask uploadTask = imageRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl= taskSnapshot.getDownloadUrl();
                System.out.println("Photo URL: " + downloadUrl.toString());
            }
        });

    }

}
