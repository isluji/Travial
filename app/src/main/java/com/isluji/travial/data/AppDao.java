package com.isluji.travial.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;

import java.util.List;

@Dao
public interface AppDao {
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

    // By default, return order is not guaranteed,
    // and ordering makes testing straightforward.
    @Query("SELECT * from trivia ORDER BY id ASC")
    LiveData<List<Trivia>> getAllTrivias();

    // ----------------------------------

    @Query("DELETE FROM poi")
    void deleteAllPois();

    @Query("DELETE FROM trivia")
    void deleteAllTrivias();

    @Query("DELETE FROM trivia_question")
    void deleteAllQuestions();

    @Query("DELETE FROM trivia_answer")
    void deleteAllAnswers();

    @Query("SELECT * FROM trivia_question WHERE trivia_id = :triviaId")
    LiveData<List<TriviaQuestion>> findQuestionsByTriviaId(String triviaId);

    @Query("SELECT * FROM trivia_answer WHERE question_id = :questionId")
    LiveData<List<TriviaAnswer>> findAnswersByQuestionId(String questionId);
}
