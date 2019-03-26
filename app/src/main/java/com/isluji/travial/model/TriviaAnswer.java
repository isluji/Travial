package com.isluji.travial.model;

public class TriviaAnswer {

    private String text;
    private boolean correct;

    public TriviaAnswer(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public boolean isCorrect() {
        return correct;
    }

}
