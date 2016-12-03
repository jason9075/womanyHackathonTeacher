package com.jason9075.womanyhackathonteacher.model;

/**
 * Created by jason9075 on 2016/12/3.
 */

public class StudentLocationData {

    private String id;
    private String studentName;
    private double latitude; //經度
    private double longitude;//緯度

    private String address;

    public StudentLocationData(String id, String studentName, double latitude, double longitude, String address) {
        this.id = id;
        this.studentName = studentName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public StudentLocationData(String id, String studentName) {
        this.id = id;
        this.studentName = studentName;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
