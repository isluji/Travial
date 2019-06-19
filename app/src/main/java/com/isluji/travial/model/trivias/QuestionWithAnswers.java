package com.isluji.travial.model.trivias;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** POJO to include OneToMany Relation with Answer */
public class QuestionWithAnswers {

    @Embedded
    private Question question;

    @Relation(parentColumn = "id", entityColumn = "question_id")
    private List<Answer> answers;


    public QuestionWithAnswers(Question question) {
        this.question = question;
    }


    /* ******** Getters and setters ******** */

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }


    /* ******** Custom methods ******** */

    public int getId() {
        return this.getQuestion().getId();
    }

    public Answer getAnswerAt(int index) {
        return answers.get(index);
    }

    // Returns null if there's still no answer selected
    public Answer getSelectedAnswer() {
        Answer selectedAnswer = null;

        for (Answer a: answers) {
            if (a.isSelected()) {
                selectedAnswer = a;
            }
        }

        return selectedAnswer;
    }

    // Returns null if there's no correct answer (impossible)
    public Answer getCorrectAnswer() {
        Answer correctAnswer = null;

        for (Answer a: answers) {
            if (a.isCorrect()) {
                correctAnswer = a;
            }
        }

        return correctAnswer;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equals = false;

        if (obj instanceof QuestionWithAnswers) {
            QuestionWithAnswers newQuestion = (QuestionWithAnswers) obj;

            boolean questionEquals = question.equals(newQuestion.question);
            boolean answerListEquals = answers.equals(newQuestion.answers);
            // TODO: Check List equals functionality

            equals = (questionEquals && answerListEquals);
        }

        return equals;
    }
}
