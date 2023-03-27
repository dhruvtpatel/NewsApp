package com.abdulkuddus.talha.newspaper.data;

import java.util.Date;

import androidx.room.TypeConverter;

/**
 * Simple class that converts the date given into a Unix timestamp for storing, and back into a Date
 * object when querying. Room will automatically use this class when it encounters a Date/Unix timestamp.
 */
public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
