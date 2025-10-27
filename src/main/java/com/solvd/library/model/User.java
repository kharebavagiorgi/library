package com.solvd.library.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"userId", "isActive", "registrationDateTime", "address"})
public class User {
    @XmlAttribute
    private String userId;
    @XmlElement
    private LocalDateTime registrationDateTime;
    @XmlAttribute
    @JsonProperty("isActive")
    private boolean isActive;
    @XmlElement
    private Address address;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getRegistrationDateTime() { return registrationDateTime; }
    public void setRegistrationDateTime(LocalDateTime registrationDateTime) { this.registrationDateTime = registrationDateTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}