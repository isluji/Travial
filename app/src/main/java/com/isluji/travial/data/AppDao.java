package com.isluji.travial.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.isluji.travial.model.trivias.Answer;
import com.isluji.travial.model.trivias.QuestionWithAnswers;
import com.isluji.travial.model.trivias.Result;
import com.isluji.travial.model.trivias.Trivia;
import com.isluji.travial.model.trivias.Question;
import com.isluji.travial.model.trivias.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;
import java.util.Set;

@Dao
public interface AppDao {

    // --------------- SELECTS ---------------

    // By default, return order is not guaranteed,
    // and ordering makes testing straightforward.

    @Query("SELECT poi_id FROM trivia ORDER BY id ASC")
    LiveData<List<String>> getAllPoiIds();

    @Transaction
    @Query("SELECT * FROM trivia WHERE poi_id IN (:userPoiIds) ORDER BY id ASC")
    LiveData<List<TriviaWithQuestions>> getUserTrivias(Set<String> userPoiIds);

    @Transaction
    @Query("SELECT * FROM question WHERE trivia_id = :triviaId ORDER BY id ASC")
    LiveData<List<QuestionWithAnswers>> getTriviaQuestions(int triviaId);

    @Query("SELECT * FROM Result WHERE user_email = :userEmail ORDER BY id ASC")
    LiveData<List<Result>> getUserResults(String userEmail);

    @Query("SELECT * FROM user WHERE email = :email")
    User findUserByEmail(String email);

    // --------------- INSERTS ---------------

    @Insert
    void insertUser(User user);

    @Insert
    long insertTrivia(Trivia trivia);

    @Insert
    long insertResult(Result result);

    @Insert
    long[] insertQuestions(Question... questions);

    @Insert
    long[] insertAnswers(Answer... answers);

    // --------------- UPDATES ---------------

    @Update
    int updateUser(User user);

    // --------------- DELETES ---------------

    @Query("DELETE FROM user")
    void deleteAllUsers();

    @Query("DELETE FROM trivia")
    void deleteAllTrivias();

    @Query("DELETE FROM Question")
    void deleteAllQuestions();

    @Query("DELETE FROM Answer")
    void deleteAllAnswers();

    @Query("DELETE FROM Result")
    void deleteAllResults();
}
