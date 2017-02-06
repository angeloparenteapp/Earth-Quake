package com.angeloparenteapp.earthquake;

/**
 * Created by angel on 06/02/2017.
 */

public class EarthQuake {

    private Double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;

    public EarthQuake(Double magnitude, String location, long timeInMilliseconds, String url){
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mTimeInMilliseconds = timeInMilliseconds;
        this.mUrl = url;
    }

    public Double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public  String getUrl(){
        return mUrl;
    }
}
