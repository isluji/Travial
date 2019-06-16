package com.isluji.travial.model.trivias;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Trivia.class,
            parentColumns = "id", childColumns = "trivia_id"))
public class Question {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String statement;

    private double score;

    // Foreign key from Trivia
    @ColumnInfo(name = "trivia_id", index = true)
    private int triviaId;


    public Question(@NonNull String statement, double score, int triviaId) {
        this.statement = statement;
        this.score = score;
        this.triviaId = triviaId;
    }


    /* ******** Getters and setters ******** */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getStatement() {
        return statement;
    }

    public void setStatement(@NonNull String statement) {
        this.statement = statement;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTriviaId() {
        return triviaId;
    }
}