package com.isluji.travial.model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity(foreignKeys = @ForeignKey(entity = Trivia.class,
        parentColumns = "id", childColumns = "trivia_id"))
public class TriviaQuestion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String statement;

    private double score;

    // Foreign key from Trivia
    @ColumnInfo(name = "trivia_id", index = true)
    private int triviaId;


    public TriviaQuestion(@NonNull String statement, double score, int triviaId) {
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

    public void setTriviaId(int triviaId) {
        this.triviaId = triviaId;
    }
}


/** POJO to include OneToMany Relation with TriviaAnswer */
class TriviaQuestionWithAnswers {

    private int id;

    @Relation(parentColumn = "id", entityColumn = "question_id")
    private List<TriviaAnswer> answers;

    public TriviaQuestionWithAnswers() {
    }


    /* *** Implemented methods *** */

    public TriviaAnswer getCorrectAnswer() {
        TriviaAnswer correctAnswer = null;

        for (TriviaAnswer a: answers) {
            if (a.isCorrect()) {
                correctAnswer = a;
            }
        }

        return correctAnswer;
    }
}