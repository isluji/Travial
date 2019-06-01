package com.isluji.travial.model;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.isluji.travial.R;
import com.isluji.travial.misc.TriviaUtils;

import java.util.List;

/** POJO to include OneToMany Relation with TriviaQuestion */
public class TriviaWithQuestions {

    @Embedded
    private Trivia trivia;

    @Relation(parentColumn = "id", entityColumn = "trivia_id",
              entity = TriviaQuestion.class)
    private List<TriviaQuestionWithAnswers> questions;

    public TriviaWithQuestions(Trivia trivia/*, List<TriviaQuestionWithAnswers> questions*/) {
        this.trivia = trivia;
//        this.questions = questions;
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

    public int getId() {
        return this.getTrivia().getId();
    }


    /* ******** Implemented methods ******** */

    public double getMaxScore() {
        int maxScore = 0;

        for (TriviaQuestionWithAnswers qwa: questions) {
            maxScore += qwa.getQuestion().getScore();
        }

        return maxScore;
    }

    public boolean isValidScore(double score) {
        return ( (score >= 0) && (score <= this.getMaxScore()) );
    }

    public boolean isPassed(double score) {
        return (score >= trivia.getPassingScore());
    }

    public void storeChoices(RecyclerView rv) {
        List<View> cvQuestions = TriviaUtils.getViewsByTag(rv, rv.getContext().getString(R.string.cv_question_tag));

        for (int i = 0; i < cvQuestions.size(); i++) {
            CardView cvQuestion = (CardView) cvQuestions.get(i);
            TriviaQuestionWithAnswers qwa = this.getQuestions().get(i);

            RadioGroup rgOptions = cvQuestion.findViewWithTag(rv.getContext().getString(R.string.rg_answers_tag));
            RadioButton rbSelected = cvQuestion.findViewById(rgOptions.getCheckedRadioButtonId());

            for (TriviaAnswer answer : qwa.getAnswers()) {
                if (rbSelected != null) {
                    answer.setSelected(answer.getText().contentEquals(rbSelected.getText()));
                } else {
                    answer.setSelected(false);
                }
            }
        }
    }
}
