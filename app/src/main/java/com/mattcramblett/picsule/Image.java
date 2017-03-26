package com.mattcramblett.picsule;

/**
 * Created by joebu on 3/26/2017.
 */

public class Image {
    public String imageName;
    public String imageURL;
    public String imageLat;
    public String imageLon;

    public Image(String name, String url, String lat, String lon){
        imageName = name;
        imageURL = url;
        imageLat = lat;
        imageLon = lon;
    }
}
