package com.mattcramblett.picsule;

import android.net.Uri;
import android.support.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by joebu on 3/26/2017.
 */

public class DatabaseHelper {

    private static final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://picsule-eb269.appspot.com");
    private StorageReference imageRef;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://picsule-eb269.firebaseio.com/");
    private HashMap<String,LatLng> listOfImageNames;

    public DatabaseHelper(){

    }

    public void uploadPhoto(Uri uri, final String fileName, final double lat, final double lon){

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
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                Image newImage = new Image(fileName, downloadUrl, lat, lon);
                mDatabase.push().setValue(newImage);
            }
        });
    }
}
