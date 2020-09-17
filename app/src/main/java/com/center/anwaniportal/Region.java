package com.center.anwaniportal;

public class Region {

    private String county;
    private String region;
    private String road;

    public Region() {}

    public Region(String county, String region, String road) {
        this.county = county;
        this.region = region;
        this.road = road;
    }

    public String getCounty(){
        return county;
    }
    public String getRegion(){
        return region;
    }
    public String getRoad(){
        return road;
    }

}
