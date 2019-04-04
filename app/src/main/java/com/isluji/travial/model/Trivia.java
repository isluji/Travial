package com.isluji.travial.model;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity(foreignKeys = @ForeignKey(entity = PointOfInterest.class,
        parentColumns = "id", childColumns = "poi_id"))
public class Trivia {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String title;

    // TODO: If it's null, we'll set the 'medium' difficulty
    private int difficulty;

    // TODO: If it's null, we'll set it as (maxScore / 2)
    @ColumnInfo(name = "passing_score")
    private int passingScore;

    // Foreign key from PointOfInterest
    @ColumnInfo(name = "poi_id", index = true)
    private int poiId;


    public Trivia() {
    }


    /** Getters and setters */

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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public int getPoiId() {
        return poiId;
    }

    public void setPoiId(int poiId) {
        this.poiId = poiId;
    }


    /** Implemented methods */

    public void addQuestion(TriviaQuestion question) {
//        questions.add(question);
    }

    public int getMaxScore() {
        int maxScore = 0;

//        for (TriviaQuestion q: questions) {
//            maxScore += q.getScore();
//        }

        return maxScore;
    }

    public List<TriviaQuestion> getQuestions() {
        return new ArrayList<>();   // TODO
    }
}


// POJO to include OneToMany Relation with TriviaQuestion
class TriviaWithQuestions {

    private int id;

    @Relation(parentColumn = "id", entityColumn = "trivia_id")
    private List<TriviaQuestion> questions;

    public TriviaWithQuestions() {
    }
}
