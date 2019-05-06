package com.isluji.travial.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "trivia_result", foreignKeys =
        {@ForeignKey(entity = Trivia.class,
            parentColumns = "id", childColumns = "trivia_id"),
        @ForeignKey(entity = User.class,
            parentColumns = "email", childColumns = "user_email")})
public class TriviaResult {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // Foreign key from Trivia
    @ColumnInfo(name = "trivia_id", index = true)
    private int triviaId;

    // Foreign key from User
    @ColumnInfo(name = "user_email", index = true)
    private int userEmail;

    private int score;

    @NonNull
    private Timestamp timestamp;


    public TriviaResult() {
    }


    /** Getters and setters */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTriviaId() {
        return triviaId;
    }

    public void setTriviaId(int triviaId) {
        this.triviaId = triviaId;
    }

    public int getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(int userEmail) {
        this.userEmail = userEmail;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @NonNull
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    /** Implemented methods */

    public boolean hasPassed() {
        return false;
//        return (this.score >= trivia.getPassingScore());
    }
}
