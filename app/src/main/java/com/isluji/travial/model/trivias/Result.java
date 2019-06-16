package com.isluji.travial.model.trivias;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.isluji.travial.model.User;

import org.threeten.bp.OffsetDateTime;

@Entity(foreignKeys = {@ForeignKey(entity = Trivia.class,
            parentColumns = "id", childColumns = "trivia_id"),
        @ForeignKey(entity = User.class,
            parentColumns = "email", childColumns = "user_email")})
public class Result {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // Foreign key from Trivia
    @ColumnInfo(name = "trivia_id", index = true)
    private int triviaId;

    // Foreign key from User
    @ColumnInfo(name = "user_email", index = true)
    private String userEmail;

    private double score;

    @NonNull
    private OffsetDateTime finishedDate;


    public Result(int triviaId, String userEmail, double score) {
        this.triviaId = triviaId;
        this.userEmail = userEmail;
        this.score = score;

        this.finishedDate = OffsetDateTime.now();
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @NonNull
    public OffsetDateTime getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(@NonNull OffsetDateTime finishedDate) {
        this.finishedDate = finishedDate;
    }
}
