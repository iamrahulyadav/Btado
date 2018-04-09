package com.cybussolutions.bataado.Model;

/**
 * Created by Rizwan Jillani on 26-Feb-18.
 */

public class Locations_Model {
    String id;
    String name;
    String city;
    String address;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    String area;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    String primary;
}
