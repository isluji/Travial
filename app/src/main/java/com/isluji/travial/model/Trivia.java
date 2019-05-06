package com.isluji.travial.model;

import com.isluji.travial.enums.TriviaDifficulty;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity(foreignKeys = @ForeignKey(entity = PointOfInterest.class,
        parentColumns = "id", childColumns = "poi_id"))
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

    // Foreign key from PointOfInterest
    @ColumnInfo(name = "poi_id", index = true)
    private int poiId;


    public Trivia(@NonNull String title, TriviaDifficulty difficulty,
                  double passingScore, int poiId) {
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

    public int getPoiId() {
        return poiId;
    }

    public void setPoiId(int poiId) {
        this.poiId = poiId;
    }

    public List<TriviaQuestion> getQuestions() {
        return new ArrayList<>();
    }
}