package com.example.nfcproject;

public class User {
    String xnum;
    String fname;
    String lname;
    String email;

    public User(){

    }
    public User(String xnum, String fname, String lname, String email){
        this.xnum=xnum;
        this.fname=fname;
        this.lname=lname;
        this.email=email;
    }

    public String getXnum() {
        return xnum;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }
}
