package com.isluji.travial.model;

import java.util.Date;

public class User {

    private String username;
    private String email;
    private String password;
    private boolean premium;
    private Date registrationDate;
    private String country;

    public boolean isPremium() {
        return premium;
    }

    // -----------------------------

    public boolean hasCompletedAnyTrivia() {
        return false; // TODO
    }
}
