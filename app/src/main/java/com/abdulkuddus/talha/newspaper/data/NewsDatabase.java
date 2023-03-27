package com.abdulkuddus.talha.newspaper.data;

import android.content.Context;

import com.abdulkuddus.talha.newspaper.News;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Room Database which will contain the {@link News} object as a table
 */
@Database(entities = News.class, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class NewsDatabase extends RoomDatabase {

    private static NewsDatabase sInstance;
    public abstract NewsDao newsDao();

    /**
     * Singleton pattern that creates a single instance of our database.
     * @param context to get the application context from
     * @return a single {@link NewsDatabase} object.
     */
    public static NewsDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NewsDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            NewsDatabase.class, "news_database").build();
                }
            }
        }
        return sInstance;
    }

}
