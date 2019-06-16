package com.isluji.travial.model.trivias;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.isluji.travial.model.User;
/*
@Entity(foreignKeys = {@ForeignKey(entity = User.class,
            parentColumns = "email", childColumns = "user_email"),
        @ForeignKey(entity = Answer.class,
            parentColumns = "id", childColumns = "selected_answer_id")})
public class UserAnswer {

    // Foreign key from User
    @ColumnInfo(name = "user_email", index = true)
    private String userEmail;

    // Foreign key from Answer
    @ColumnInfo(name = "selected_answer_id", index = true)
    private int selectedAnswerId;
}
*/