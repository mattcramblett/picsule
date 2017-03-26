package com.mattcramblett.picsule;

/**
 * Created by joebu on 3/26/2017.
 */

public class Image {
    private String imageName;
    private String imageURL;
    private String imageLat;
    private String imageLon;

    public Image(String name, String url, String lat, String lon){
        imageName = name;
        imageURL = url;
        imageLat = lat;
        imageLon = lon;
    }
}
