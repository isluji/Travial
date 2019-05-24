package com.isluji.travial.model;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = @Index(name = "google_id", value = "google_id", unique = true))
public class User {
    @NonNull
    @PrimaryKey // Internal ID
    private String email;

    @NonNull    // External ID
    @ColumnInfo(name = "google_id")
    private String googleId;

    @NonNull
    @ColumnInfo(name = "registration_date")
    private Date registrationDate;

    private boolean premium;


    public User(@NonNull String email, @NonNull String googleId) {
        this.email = email;
        this.googleId = googleId;

        this.registrationDate = new Date(); // Current date
        this.premium = false;   // False by default
    }


    /** Getters and setters */

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getGoogleId() {
        return googleId;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
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
