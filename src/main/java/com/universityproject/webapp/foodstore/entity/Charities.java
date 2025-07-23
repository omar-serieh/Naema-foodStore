package com.universityproject.webapp.foodstore.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "charities")
public class Charities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charity_id")
    private int charityId;
    @Column(name = "name_charity")
    private String charityName;
    @Column(name = "description")
    private String charityDescription;
    @Column(name = "contact_info")
    private String charityInfo;

    public Charities() {
    }

    public Charities(String charityName, String charityDescription, String charityInfo) {
        this.charityName = charityName;
        this.charityDescription = charityDescription;
        this.charityInfo = charityInfo;
    }

    public int getCharityId() {
        return charityId;
    }

    public void setCharityId(int charityId) {
        this.charityId = charityId;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getCharityDescription() {
        return charityDescription;
    }

    public void setCharityDescription(String charityDescription) {
        this.charityDescription = charityDescription;
    }

    public String getCharityInfo() {
        return charityInfo;
    }

    public void setCharityInfo(String charityInfo) {
        this.charityInfo = charityInfo;
    }
}
