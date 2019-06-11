package com.isluji.travial.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;

@Dao
public interface AppDao {

    // --------------- SELECTS ---------------

    // By default, return order is not guaranteed,
    // and ordering makes testing straightforward.

    @Query("SELECT poi_id FROM trivia ORDER BY id ASC")
    LiveData<List<String>> getAllPoiIds();

    @Transaction
    @Query("SELECT * FROM trivia WHERE poi_id IN " +
            "(SELECT unlocked_poi_ids FROM user WHERE email = :userEmail)")
    LiveData<List<TriviaWithQuestions>> getUserTrivias(String userEmail);

    @Query("SELECT * FROM trivia_result WHERE user_email = :userEmail ORDER BY id ASC")
    LiveData<List<TriviaResult>> getUserResults(String userEmail);

    @Query("SELECT * FROM user WHERE email = :email")
    User findUserByEmail(String email);

    // --------------- INSERTS ---------------

    @Insert
    void insertUser(User user);

    @Insert
    long insertTrivia(Trivia trivia);

    @Insert
    long insertResult(TriviaResult result);

    @Insert
    long[] insertQuestions(TriviaQuestion... questions);

    @Insert
    long[] insertAnswers(TriviaAnswer... answers);

    // --------------- UPDATES ---------------

    @Update
    void updateUser(User user);

    // --------------- DELETES ---------------

    @Query("DELETE FROM user")
    void deleteAllUsers();

    @Query("DELETE FROM trivia")
    void deleteAllTrivias();

    @Query("DELETE FROM trivia_question")
    void deleteAllQuestions();

    @Query("DELETE FROM trivia_answer")
    void deleteAllAnswers();

    @Query("DELETE FROM trivia_result")
    void deleteAllResults();
}
