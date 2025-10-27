package com.solvd.library.model;

import jakarta.xml.bind.annotation.XmlAttribute;

public class Address {
    private String street;
    private String city;

    @XmlAttribute
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    @XmlAttribute
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() { return street + ", " + city; }
}