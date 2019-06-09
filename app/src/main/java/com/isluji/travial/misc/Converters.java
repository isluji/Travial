package com.isluji.travial.misc;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isluji.travial.enums.TriviaDifficulty;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Set;

public class Converters {

    // Set<String> <-> String (JSON)

    @TypeConverter
    public static Set<String> toStringSet(String value) {
        return new Gson().fromJson(value, new TypeToken<Set<String>>() {}.getType());
    }

    @TypeConverter
    public static String fromStringSet(Set<String> stringSet) {
        return new Gson().toJson(stringSet);
    }

    // OffsetDateTime (ThreeTenABP) <-> String

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String value) {
        return formatter.parse(value, OffsetDateTime.FROM);
    }

    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime date) {
        return date.format(formatter);
    }


    // TriviaDifficulty (enum) <-> String

    @TypeConverter
    public static TriviaDifficulty toTriviaDifficulty(String value) {
        return TriviaDifficulty.valueOf(value);
    }

    @TypeConverter
    public static String fromTriviaDifficulty(TriviaDifficulty difficulty) {
        return difficulty.name();
    }
}
