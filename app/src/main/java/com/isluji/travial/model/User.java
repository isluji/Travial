package com.isluji.travial.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.threeten.bp.OffsetDateTime;

import java.util.HashSet;
import java.util.Set;

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
    private OffsetDateTime registrationDate;

    // List with the Place ID of the registered POIs
    @ColumnInfo(name = "unlocked_poi_ids")
    private Set<String> unlockedPoiIds;

    private boolean premium;


    public User(@NonNull String email, @NonNull String googleId) {
        this.email = email;
        this.googleId = googleId;

        this.premium = false;   // False by default
        this.registrationDate = OffsetDateTime.now(); // Current date
        this.unlockedPoiIds = new HashSet<>();
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
    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(@NonNull OffsetDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<String> getUnlockedPoiIds() {
        return unlockedPoiIds;
    }

    public void setUnlockedPoiIds(Set<String> unlockedPoiIds) {
        this.unlockedPoiIds = unlockedPoiIds;
    }

    public void unlockPoi(String poiId) {
        this.unlockedPoiIds.add(poiId);
    }


    /** Implemented methods */

    public boolean hasCompletedAnyTrivia() {
        return false; // TODO
    }
}
