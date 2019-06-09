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
import com.isluji.travial.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private AppDao dao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = db.getAppDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        User user = new User("email@prueba.com", "googleId");
        Trivia trivia = new Trivia("Trivia de Prueba", TriviaDifficulty.EASY, 5, "poiId");
        trivia.setId(Long.valueOf(dao.insertTrivia(trivia)).intValue());

//        user.setName("george");
//        dao.insert(user);
//        List<User> byName = dao.findUsersByName("george");
//        assertThat(byName.get(0), equalTo(user));
    }
}
