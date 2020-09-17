package com.center.anwaniportal;

public class Street {

    private String road;
    private String fromroad;
    private String exitroad;
    private String entrylatitude;
    private String entrylongitude;
    private String exitlatitude;
    private String exitlongitude;
    private String verificationstatus;
    private String verificationstatusex;
    private String activestatus;

    public Street() {}

    public Street(String road, String fromroad, String exitroad, String entrylatitude, String entrylongitude,
                  String exitlatitude, String exitlongitude, String verificationstatus,String verificationstatusex , String activestatus) {
        this.road = road;
        this.fromroad = fromroad;
        this.exitroad = exitroad;
        this.entrylatitude = entrylatitude;
        this.entrylongitude=entrylongitude;
        this.exitlatitude=exitlatitude;
        this.exitlongitude=exitlongitude;
        this.verificationstatus=verificationstatus;
        this.verificationstatusex=verificationstatusex;
        this.activestatus=activestatus;
    }

    public String getRoad(){
        return road;
    }
    public String getFromroad(){
        return fromroad;
    }
    public String getExitroad(){
        return exitroad;
    }
    public String getEntrylatitude(){
        return entrylatitude;
    }
    public String getEntrylongitude(){
        return entrylongitude;
    }
    public String getExitlatitude(){
        return exitlatitude;
    }
    public String getExitlongitude(){
        return exitlongitude;
    }
    public String getVerificationstatus(){
        return verificationstatus;
    }
    public String getVerificationstatusex(){
        return verificationstatusex;
    }
    public String getActivestatus(){
        return activestatus;
    }

}
