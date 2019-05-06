package com.isluji.travial.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** POJO to include OneToMany Relation with TriviaAnswer */
public class TriviaQuestionWithAnswers {

    @Embedded
    private TriviaQuestion question;

    @Relation(parentColumn = "id", entityColumn = "question_id")
    private List<TriviaAnswer> answers;

    public TriviaQuestionWithAnswers(TriviaQuestion question, List<TriviaAnswer> answers) {
        this.question = question;
        this.answers = answers;
    }


    // ----------------------------------

    public TriviaQuestion getQuestion() {
        return question;
    }

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
