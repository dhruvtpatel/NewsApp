package com.abdulkuddus.talha.newspaper.data;

import com.abdulkuddus.talha.newspaper.News;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

/**
 * Room will implement this interface and create the DAO (Data Access Object) for us. This offers us
 * abstract access to our database.
 */
@Dao
public interface NewsDao {

    @Insert
    void insertNewsItem(News... news);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNewsList(List<News> newsList);

    @Update
    void updateNewsItem(News... news);

    @Query("SELECT * from news_table WHERE category = :category")
    LiveData<List<News>> getNewsFromCategory(String category);

    @Query("SELECT * from news_table WHERE category = 'saved'")
    LiveData<List<News>> getSavedNews();

    @Delete
    void deleteNewsItem(News... news);

    @Query("DELETE FROM news_table WHERE category = :category")
    void deleteNewsFromCategory(String category);
}