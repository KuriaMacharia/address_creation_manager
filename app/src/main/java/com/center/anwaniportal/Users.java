package com.center.anwaniportal;

public class Users {

    private String name;
    private String supervisor;
    private String email;
    private String phone;
    private String role;
    private String county;
    private String region;
    private String status;


    public Users() {}

    public Users(String name, String supervisor, String email, String phone, String role, String county, String region,
                 String status) {
        this.name = name;
        this.supervisor = supervisor;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.county = county;
        this.region = region;
        this.status = status;
    }

    public String getName(){
        return name;
    }
    public String getSupervisor(){
        return supervisor;
    }
    public String getEmail(){
        return email;
    }
    public String getPhone(){
        return phone;
    }
    public String getRole(){
        return role;
    }
    public String getCounty(){
        return county;
    }
    public String getRegion(){
        return region;
    }
    public String getStatus(){
        return status;
    }

}
