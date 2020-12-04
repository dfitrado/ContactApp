package com.example.uts;

import java.io.Serializable;

public class Contact implements Serializable {
    int id;
    private String name;
    private String number;
    private String email;
    private String lat;
    private String lng;

    public Contact(){

    }

    public Contact(int id, String name, String number, String email, String lat, String lng) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
    }

    public Contact(String name, String number, String email, String lat, String lng) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
