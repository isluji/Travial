package com.isluji.travial.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.isluji.travial.enums.TriviaDifficulty;

@Entity
public class Trivia {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String title;

    // TODO: If it's null, we'll set it as MEDIUM
    private TriviaDifficulty difficulty;

    // TODO: If it's null, we'll set it as (maxScore / 2)
    @ColumnInfo(name = "passing_score")
    private double passingScore;

    // Google Places API ID of the related PointOfInterest
    @ColumnInfo(name = "poi_id", index = true)
    private String poiId;


    public Trivia(@NonNull String title, TriviaDifficulty difficulty,
                  double passingScore, String poiId) {
        this.title = title;
        this.difficulty = difficulty;
        this.passingScore = passingScore;
        this.poiId = poiId;
    }


    /* ******** Getters and setters ******** */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public TriviaDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(TriviaDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public double getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(double passingScore) {
        this.passingScore = passingScore;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }
}