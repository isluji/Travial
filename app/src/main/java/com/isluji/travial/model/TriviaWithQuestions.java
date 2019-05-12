package com.isluji.travial.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** POJO to include OneToMany Relation with TriviaQuestion */
public class TriviaWithQuestions {

    @Embedded
    private Trivia trivia;

    @Relation(parentColumn = "id", entityColumn = "trivia_id",
              entity = TriviaQuestion.class)
    private List<TriviaQuestionWithAnswers> questions;

    public TriviaWithQuestions(Trivia trivia, List<TriviaQuestionWithAnswers> questions) {
        this.trivia = trivia;
        this.questions = questions;
    }


    /* ******** Getters and setters ******** */

    public Trivia getTrivia() {
        return trivia;
    }

    public void setTrivia(Trivia trivia) {
        this.trivia = trivia;
    }

    public List<TriviaQuestionWithAnswers> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TriviaQuestionWithAnswers> questions) {
        this.questions = questions;
    }


    /* ******** Implemented methods ******** */

    public double getMaxScore() {
        int maxScore = 0;

        for (TriviaQuestionWithAnswers qwa: questions) {
            maxScore += qwa.getQuestion().getScore();
        }

        return maxScore;
    }
}
