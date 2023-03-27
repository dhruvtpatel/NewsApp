package com.abdulkuddus.talha.newspaper.ui;

import android.app.Application;
import android.util.Log;

import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.data.NewsRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class HeadlinesViewModel extends AndroidViewModel {

    private NewsRepository mRepository;
    private LiveData<List<News>> mNewsList;

    /**
     * Constructor which initialises the ViewModel to get an instance of the repository and the
     * current data in the database
     * @param application The application context, used to initialise the database if not done so
     */
    public HeadlinesViewModel(Application application) {
        super(application);
        mRepository = NewsRepository.getInstance(application);
        mNewsList = mRepository.getAllNews(News.LOCAL);
    }

    /**
     * Simple method to send data from the ViewModel to the UI.
     * @return A LiveData object containing the list of news objects.
     */
    public LiveData<List<News>> getNewsList() {
        return mNewsList;
    }

    /**
     * Forces the repository to clear all news articles and get new ones.
     */
    public void refreshNews() {
        Log.v("HeadlinesViewModel", "Called refresh news");
        mRepository.forceUpdateNewsAsync(News.LOCAL);
    }

}
