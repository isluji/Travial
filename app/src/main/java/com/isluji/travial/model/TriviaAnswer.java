package com.isluji.travial.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "trivia_answer",
        foreignKeys = @ForeignKey(entity = TriviaQuestion.class,
            parentColumns = "id", childColumns = "question_id"))
public class TriviaAnswer {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String text;

    // TODO: If it's null, we'll take it as 'false'
    private boolean correct;

    // Foreign key from TriviaQuestion
    @ColumnInfo(name = "question_id", index = true)
    private int questionId;

    @Ignore
    private boolean selected;


    public TriviaAnswer(@NonNull String text, boolean correct, int questionId) {
        this.text = text;
        this.correct = correct;
        this.questionId = questionId;
    }


    /* ******** Getters and setters ******** */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
