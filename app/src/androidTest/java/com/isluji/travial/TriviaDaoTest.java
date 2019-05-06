package com.isluji.travial;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.room.Room;
import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;
import com.isluji.travial.data.AppDao;
import com.isluji.travial.data.AppDatabase;
import com.isluji.travial.enums.PoiType;
import com.isluji.travial.enums.TriviaDifficulty;
import com.isluji.travial.model.PointOfInterest;
import com.isluji.travial.model.Trivia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4.class)
public class TriviaDaoTest {

    @Rule
    public InstantTaskExecutorRule iter = new InstantTaskExecutorRule();

    private AppDao mDao;
    private AppDatabase mDb;

    @Before
    public void createDb() {
        // ALTERNATIVE: InstrumentationRegistry.getInstrumentation().getContext()
        Context context = ApplicationProvider.getApplicationContext();
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        mDao = mDb.getAppDao();
    }

    @After
    public void closeDb() {
        mDb.close();
    }

    @Test
    public void insertAndGetTrivia() throws Exception {
        /* Insert test data */
        PointOfInterest poi = new PointOfInterest("Fuente del Rey", PoiType.MONUMENT, "Remigio del Mármol", 1803, true, null, null);
        mDao.insertPoi(poi);
        poi.setId(mDao.getPoiId(poi.getName()));

        Trivia trivia = new Trivia("Historia de la Fuente del Rey", TriviaDifficulty.EASY, 5, poi.getId());
        mDao.insertTrivia(trivia);

        /* Evaluate test condition */
        List<Trivia> allTrivias = LiveDataTestUtil.getValue(mDao.getAllTrivias());
        assertEquals(allTrivias.get(0).getTitle(), trivia.getTitle());
    }

    @Test
    public void getAllTrivias() throws Exception {
        /* Insert test data */
        PointOfInterest poi = new PointOfInterest("Fuente del Rey", PoiType.MONUMENT, "Remigio del Mármol", 1803, true, null, null);
        mDao.insertPoi(poi);
        poi.setId(mDao.getPoiId(poi.getName()));

        Trivia trivia = new Trivia("Historia de la Fuente del Rey", TriviaDifficulty.EASY, 5, poi.getId());
        mDao.insertTrivia(trivia);
        Trivia trivia2 = new Trivia("Arte de Lozano Sidro", TriviaDifficulty.MEDIUM, 6, poi.getId());
        mDao.insertTrivia(trivia2);

        /* Evaluate test conditions */
        List<Trivia> allTrivias = LiveDataTestUtil.getValue(mDao.getAllTrivias());
        assertEquals(allTrivias.get(0).getTitle(), trivia.getTitle());
        assertEquals(allTrivias.get(1).getTitle(), trivia2.getTitle());
    }

    @Test
    public void deleteAll() throws Exception {
        /* Insert test data */
        PointOfInterest poi = new PointOfInterest("Fuente del Rey", PoiType.MONUMENT, "Remigio del Mármol", 1803, true, null, null);
        mDao.insertPoi(poi);
        poi.setId(mDao.getPoiId(poi.getName()));

        Trivia trivia = new Trivia("Historia de la Fuente del Rey", TriviaDifficulty.EASY, 5, poi.getId());
        mDao.insertTrivia(trivia);
        Trivia trivia2 = new Trivia("Arte de Lozano Sidro", TriviaDifficulty.MEDIUM, 6, poi.getId());
        mDao.insertTrivia(trivia2);
        mDao.deleteAllTrivias();

        /* Evaluate test condition */
        List<Trivia> allTrivias = LiveDataTestUtil.getValue(mDao.getAllTrivias());
        assertTrue(allTrivias.isEmpty());
    }
}
