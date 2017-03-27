package com.mattcramblett.picsule;

/**
 * Created by joebu on 3/26/2017.
 */

public class Image {

    public String imageName;
    public String imageURL;
    public double imageLat;
    public double imageLon;

    public Image(){

    }

    public Image(String name, String url, double lat, double lon){
        imageName = name;
        imageURL = url;
        imageLat = lat;
        imageLon = lon;
    }

    public void setImageName(String name){
        imageName = name;
    }

    public void setImageURL(String url){
        imageURL = url;
    }

    public void setImageLat(double lat){
        imageLat = lat;
    }

    public void setImageLon(double lon){
        imageLon = lon;
    }
}
