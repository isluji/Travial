package com.isluji.travial.data;

import com.isluji.travial.enums.PoiType;
import com.isluji.travial.enums.TriviaDifficulty;

import java.util.Date;

import androidx.room.TypeConverter;

public class Converters {

    // Date <-> Long (Timestamp)

    @TypeConverter
    public static Date timestampToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // PoiType (enum) <-> String

    @TypeConverter
    public static PoiType stringToPoiType(String value) {
        return PoiType.valueOf(value);
    }

    @TypeConverter
    public static String poiTypeToString(PoiType poiType) {
        return poiType.name();
    }

    // TriviaDifficulty (enum) <-> String

    @TypeConverter
    public static TriviaDifficulty stringToTriviaDifficulty(String value) {
        return TriviaDifficulty.valueOf(value);
    }

    @TypeConverter
    public static String triviaDifficultyToString(TriviaDifficulty difficulty) {
        return difficulty.name();
    }
}
