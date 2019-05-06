package com.isluji.travial.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.isluji.travial.enums.PoiType;
import com.isluji.travial.enums.TriviaDifficulty;
import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;
import com.isluji.travial.model.TriviaResult;
import com.isluji.travial.model.User;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

// We need to declare all the entities here
@Database(entities = {User.class, PointOfInterest.class,
        Trivia.class, TriviaQuestion.class, TriviaAnswer.class,
        TriviaResult.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract AppDao getAppDao();

    private static RoomDatabase.Callback sDbCallback =
        new RoomDatabase.Callback() {

            /** Called after database has been created (only once) */
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                Log.v("app-db", "Ejecutando AppDB onCreate()");

//                new PopulateDbAsync(INSTANCE).execute();
            }

            /** Called every time database is opened */
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);

                Log.v("app-db", "Ejecutando AppDB onOpen()");

                new PopulateDbAsync(INSTANCE).execute();
            }
        };

    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {
                    /* Creating the database */

                    // Create the builder that will create the DB
                    RoomDatabase.Builder rdbBuilder = Room.databaseBuilder(
                            context.getApplicationContext(), AppDatabase.class,"app-db");

                    // Add onCreate and onOpen callbacks to the builder
                    rdbBuilder = rdbBuilder.addCallback(sDbCallback);

                    // TODO: Migration path from version 1 to 2?
                    // Esto borra la BD entera y la crea de nuevo
                    rdbBuilder = rdbBuilder.fallbackToDestructiveMigration();

                    // Creates a DB object in the application context
                    INSTANCE = (AppDatabase) rdbBuilder.build();

                    Log.v("app-db", "Se ha construido la BD " +
                            "( rdbBuilder.build() )");
                }
            }
        }

        return INSTANCE;
    }


    /** AsyncTask that prepopulates the database */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.getAppDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAllAnswers();
            mDao.deleteAllQuestions();
            mDao.deleteAllTrivias();
            mDao.deleteAllPois();

            // TODO: Implement POIs' image and location

            // Create POI and insert it in the DB, for its ID to be generated.
            PointOfInterest poi = new PointOfInterest("Fuente del Rey", PoiType.MONUMENT, "Remigio del Mármol", 1803, true, null, null);

            mDao.insertPoi(poi);

            poi.setId(mDao.getPoiId(poi.getName()));
            Log.v("app-db", new Gson().toJson(poi));

            // We can now use that ID in the Trivia constructor
            Trivia trivia = new Trivia("Historia de la Fuente del Rey", TriviaDifficulty.EASY, 5, poi.getId());

            // Repeat process with Trivia and the TriviaQuestions
            mDao.insertTrivia(trivia);

            trivia.setId(mDao.getTriviaId(trivia.getTitle()));
            TriviaQuestion q1 = new TriviaQuestion("¿Quién fue su autor?", 3.33, trivia.getId());
            TriviaQuestion q2 = new TriviaQuestion("¿En qué año fue construida?", 3.33, trivia.getId());
            TriviaQuestion q3 = new TriviaQuestion("¿Cuál es su estilo arquitectónico?", 3.33, trivia.getId());

            mDao.insertQuestions(q1, q2, q3);

            q1.setId(mDao.getQuestionId(q1.getStatement()));
            TriviaAnswer a1_1 = new TriviaAnswer("José Álvarez Cubero", false, q1.getId());
            TriviaAnswer a1_2 = new TriviaAnswer("Remigio del Mármol", true, q1.getId());
            TriviaAnswer a1_3 = new TriviaAnswer("Adolfo Lozano Sidro", false, q1.getId());

            q2.setId(mDao.getQuestionId(q2.getStatement()));
            TriviaAnswer a2_1 = new TriviaAnswer("1803", true, q2.getId());
            TriviaAnswer a2_2 = new TriviaAnswer("1805", false, q2.getId());
            TriviaAnswer a2_3 = new TriviaAnswer("1823", false, q2.getId());

            q3.setId(mDao.getQuestionId(q3.getStatement()));
            TriviaAnswer a3_1 = new TriviaAnswer("Románico", false, q3.getId());
            TriviaAnswer a3_2 = new TriviaAnswer("Renacentista", false, q3.getId());
            TriviaAnswer a3_3 = new TriviaAnswer("Barroco", true, q3.getId());

            mDao.insertAnswers(a1_1, a1_2, a1_3,
                    a2_1, a2_2, a2_3,
                    a3_1, a3_2, a3_3);

            return null;
        }
    }
}

