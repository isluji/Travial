package com.isluji.travial.data;

import com.isluji.travial.model.PoiType;

import java.time.LocalDateTime;
import java.util.Date;

import androidx.room.TypeConverter;

public class Converters {

    // Date <-> Long (Timestamp)

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // PoiType (enum) <-> String

    @TypeConverter
    public static PoiType fromString(String value) {
        return PoiType.valueOf(value);
    }

    @TypeConverter
    public static String poiTypeToString(PoiType poiType) {
        return poiType.name();
    }
}
