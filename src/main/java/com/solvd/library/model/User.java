package com.solvd.library.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "isActive", "registrationDateTime", "address"})
public class User {
    @XmlAttribute
    private Long id;
    @XmlElement
    private LocalDateTime registrationDateTime;
    @XmlAttribute
    @JsonProperty("isActive")
    private boolean isActive;
    @XmlElement
    private Address address;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getRegistrationDateTime() { return registrationDateTime; }
    public void setRegistrationDateTime(LocalDateTime registrationDateTime) { this.registrationDateTime = registrationDateTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
