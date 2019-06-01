package com.isluji.travial.misc;

import com.isluji.travial.enums.PoiType;
import com.isluji.travial.enums.TriviaDifficulty;

import androidx.room.TypeConverter;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class Converters {

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
