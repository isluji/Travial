package com.isluji.travial.model;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @NonNull
    @PrimaryKey
    private String email;

    @NonNull
    private String password;

    // TODO: If it's not set, we'll use email's username (left of @)
    private String username;

    // TODO: If it's null, we'll take it as 'false'
    private boolean premium;

    private String country;

    @NonNull
    @ColumnInfo(name = "registration_date")
    private Date registrationDate;


    public User() {
    }


    /** Getters and setters */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @NonNull
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(@NonNull Date registrationDate) {
        this.registrationDate = registrationDate;
    }


    /** Implemented methods */

    public boolean hasCompletedAnyTrivia() {
        return false; // TODO
    }
}
