package com.isluji.travial.model.trivias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Question.class,
            parentColumns = "id", childColumns = "question_id"))
public class Answer {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String text;

    // TODO: If it's null, we'll take it as 'false'
    private boolean correct;

    // Foreign key from Question
    @ColumnInfo(name = "question_id", index = true)
    private int questionId;

    @Ignore
    private boolean selected;


    public Answer(@NonNull String text, boolean correct, int questionId) {
        this.text = text;
        this.correct = correct;
        this.questionId = questionId;

        this.selected = false;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    /* ******** Custom methods ******** */

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equals = false;

        if (obj instanceof Answer) {
            Answer newAnswer = (Answer) obj;

            boolean idEquals = (this.id == newAnswer.id);
            boolean questionIdEquals = (this.questionId == newAnswer.questionId);
            boolean textEquals = this.text.equals(newAnswer.text);
            boolean correctEquals = (this.correct == newAnswer.correct);
            boolean selectedEquals = (this.selected == newAnswer.selected);

            equals = (idEquals && questionIdEquals && textEquals
                    && correctEquals && selectedEquals);
        }

        return equals;
    }
}
