package com.isluji.travial.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
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
    // TODO? Define conflict strategy

    @Insert
    void insertUser(User user);

    @Insert
    void insertPoi(PointOfInterest poi);

    @Insert
    void insertTrivia(Trivia trivia);

    @Insert
    void insertQuestions(TriviaQuestion... questions);

    @Insert
    void insertAnswers(TriviaAnswer... answers);

    // ----------------------------------

    @Query("SELECT id FROM poi WHERE name = :name")
    int getPoiId(String name);

    @Query("SELECT id FROM trivia WHERE title = :title")
    int getTriviaId(String title);

    @Query("SELECT id FROM trivia_question WHERE statement = :statement")
    int getQuestionId(String statement);

    // ----------------------------------

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
