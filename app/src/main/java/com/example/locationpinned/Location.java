package com.example.locationpinned;

import java.util.ArrayList;
import java.util.Date;

public class Location {
    //Initializing variables which will be used outside this class
    public static ArrayList<Location> locationArrayList = new ArrayList<>();
    public static String LOCATION_EDIT_EXTRA =  "locationEdit";
    //Initializing the required variables to be used by this class
    private int id;
    private double latitude;
    private double longitude;
    private String address;
    private Date deleted;

    //Creating constructors which will be used to assign values to each location
    public Location(int id, double latitude, double longitude, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        deleted = null;
    }

    public Location(int id, double latitude, double longitude, String address, Date deleted) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.deleted = deleted;
    }

    //Getters and setters for each location parameter
    public Location(){}

    public static Location getNoteForID(int passedNoteID)
    {
        for (Location location : locationArrayList)
        {
            if(location.getId() == passedNoteID)
                return location;
        }

        return null;
    }

    public static ArrayList<Location> nonDeletedNotes()
    {
        ArrayList<Location> nonDeleted = new ArrayList<>();
        for(Location location : locationArrayList)
        {
            if(location.getDeleted() == null)
                nonDeleted.add(location);
        }

        return nonDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
