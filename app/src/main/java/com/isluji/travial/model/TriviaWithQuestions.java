package com.isluji.travial.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** POJO to include OneToMany Relation with TriviaQuestion */
public class TriviaWithQuestions {

    @Embedded
    private Trivia trivia;

    @Relation(parentColumn = "id", entityColumn = "trivia_id")
    private List<TriviaQuestion> questions;

    public TriviaWithQuestions(Trivia trivia, List<TriviaQuestion> questions) {
        this.trivia = trivia;
        this.questions = questions;
    }


    // ----------------------------------

    public Trivia getTrivia() {
        return trivia;
    }

    public double getMaxScore() {
        int maxScore = 0;

        for (TriviaQuestion q: questions) {
            maxScore += q.getScore();
        }

        return maxScore;
    }
}
