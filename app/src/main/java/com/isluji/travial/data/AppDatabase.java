package com.isluji.travial.data;

import android.content.Context;
import android.util.Log;

import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.User;

import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

// We need to declare all the entities here
@Database(entities = {User.class, PointOfInterest.class,
        Trivia.class, TriviaQuestion.class, TriviaAnswer.class,
        TriviaResult.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {
                    // Creates a DB object in the application context
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,"app_db")

                        /** Define onCreate and onOpen callbacks */
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            // TODO: do something after database has been created
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);

                                Log.v("testdb", "Ejecutando AppDB onCreate()");
                                db.execSQL("INSERT INTO trivia VALUES(3, 'titulo', 1, 2);");
                            }

                            @Override
                            // TODO? do something every time database is open
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);

                                // -----
                            }
                        })

                        .build();
                }
            }
        }

        return INSTANCE;
    }


    /** DAOs declaration */

    public abstract UserDao getUserDao();
    public abstract TriviaDao getTriviaDao();
}