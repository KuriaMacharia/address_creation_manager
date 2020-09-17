package com.center.anwaniportal;

public class Station {

    private String county;
    private String region;
    private String starttime;
    private String endtime;
    private String regionstatus;
    private String latitude;
    private String longitude;
    private String radius;

    public Station() {}

    public Station(String county, String region, String starttime, String endtime, String regionstatus,
                      String latitude, String longitude, String radius) {
        this.county = county;
        this.region = region;
        this.starttime = starttime;
        this.endtime = endtime;
        this.regionstatus=regionstatus;
        this.latitude = latitude;
        this.longitude=longitude;
        this.radius=radius;
    }

    public String getCounty(){
        return county;
    }
    public String getRegion(){
        return region;
    }
    public String getStarttime(){
        return starttime;
    }
    public String getEndtime(){
        return endtime;
    }
    public String getRegionstatus(){
        return regionstatus;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getRadius(){
        return radius;
    }

}

