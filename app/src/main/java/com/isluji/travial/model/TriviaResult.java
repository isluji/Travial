package com.isluji.travial.model;

import java.time.LocalDateTime;

public class TriviaResult {

    private User user;
    private Trivia trivia;

    private int score;
    private LocalDateTime dateTime;
    private int timeSpent;

    // -----------------------------

    public boolean hasPassed() {
        return (this.score >= trivia.getPassingScore());
    }
}
