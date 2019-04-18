package com.isluji.travial.data;

import android.content.Context;
import android.util.Log;

import com.isluji.travial.enums.PoiType;
import com.isluji.travial.enums.TriviaDifficulty;
import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.User;

import java.util.ArrayList;
import java.util.List;
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

    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {
                    // Create the builder that will create the DB
                    RoomDatabase.Builder rdbBuilder = Room.databaseBuilder(
                            context.getApplicationContext(), AppDatabase.class,"app-db");

                    // Add onCreate and onOpen callbacks to the builder
                    rdbBuilder.addCallback(new RoomDatabase.Callback() {
                            /** Called after database has been created (only once) */
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);

                                Log.v("app-db", "Ejecutando AppDB onCreate()");

                                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        prepopulateDb(getDatabase(context));
                                    }
                                });
                            }

                            /** Called every time database is opened */
                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);

                                Log.v("app-db", "Ejecutando AppDB onOpen()");
                            }
                        });

                        // Creates a DB object in the application context
                        INSTANCE = (AppDatabase) rdbBuilder.build();

                        Log.v("app-db", "Se ha construido la BD " +
                                "( rdbBuilder.build() )");
                }
            }
        }

        return INSTANCE;
    }


    public abstract AppDao getAppDao();

    public static void prepopulateDb(AppDatabase db) {

        // TODO: Implement POIs' image and location

        // Create POI and insert it in the DB, for its ID to be generated.
        PointOfInterest poi = new PointOfInterest("Fuente del Rey", PoiType.MONUMENT, "Remigio del Mármol", 1803, true, null, null);

        db.getAppDao().insertPoi(poi);

        // We can now use that ID in the Trivia constructor
        Trivia trivia = new Trivia("Historia de la Fuente del Rey", TriviaDifficulty.EASY, 5, poi.getId());

        // Repeat process with Trivia and the TriviaQuestions
        db.getAppDao().insertTrivia(trivia);

        TriviaQuestion q1 = new TriviaQuestion("¿Quién fue su autor?", 3.33, trivia.getId());
        TriviaQuestion q2 = new TriviaQuestion("¿En qué año fue construida?", 3.33, trivia.getId());
        TriviaQuestion q3 = new TriviaQuestion("¿Cuál es su estilo arquitectónico?", 3.33, trivia.getId());

        db.getAppDao().insertQuestions(q1, q2, q3);

        TriviaAnswer a1_1 = new TriviaAnswer("José Álvarez Cubero", false, q1.getId());
        TriviaAnswer a1_2 = new TriviaAnswer("Remigio del Mármol", true, q1.getId());
        TriviaAnswer a1_3 = new TriviaAnswer("Adolfo Lozano Sidro", false, q1.getId());

        TriviaAnswer a2_1 = new TriviaAnswer("1803", true, q2.getId());
        TriviaAnswer a2_2 = new TriviaAnswer("1805", false, q2.getId());
        TriviaAnswer a2_3 = new TriviaAnswer("1823", false, q2.getId());

        TriviaAnswer a3_1 = new TriviaAnswer("Románico", false, q3.getId());
        TriviaAnswer a3_2 = new TriviaAnswer("Renacentista", false, q3.getId());
        TriviaAnswer a3_3 = new TriviaAnswer("Barroco", true, q3.getId());

        db.getAppDao().insertAnswers(a1_1, a1_2, a1_3,
                a2_1, a2_2, a2_3,
                a3_1, a3_2, a3_3);
    }
}