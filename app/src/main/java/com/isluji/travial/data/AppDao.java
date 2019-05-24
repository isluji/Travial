package com.isluji.travial.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;
import com.isluji.travial.model.TriviaWithQuestions;
import com.isluji.travial.model.User;

import java.util.List;

@Dao
public interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Insert
    long insertPoi(PointOfInterest poi);

    @Insert
    long insertTrivia(Trivia trivia);

    @Insert
    long[] insertQuestions(TriviaQuestion... questions);

    @Insert
    long[] insertAnswers(TriviaAnswer... answers);

    // ----------------------------------

    @Query("SELECT * FROM User ORDER BY email ASC")
    List<User> getAllUsers();

    @Transaction
    @Query("SELECT * FROM trivia WHERE id = :id")
    LiveData<TriviaWithQuestions> findTriviaById(int id);

    // By default, return order is not guaranteed,
    // and ordering makes testing straightforward.
    @Transaction
    @Query("SELECT * FROM trivia ORDER BY id ASC")
    LiveData<List<TriviaWithQuestions>> getAllTrivias();

    // ----------------------------------

    @Query("DELETE FROM user")
    void deleteAllUsers();

    @Query("DELETE FROM poi")
    void deleteAllPois();

    @Query("DELETE FROM trivia")
    void deleteAllTrivias();

    @Query("DELETE FROM trivia_question")
    void deleteAllQuestions();

    @Query("DELETE FROM trivia_answer")
    void deleteAllAnswers();
}
