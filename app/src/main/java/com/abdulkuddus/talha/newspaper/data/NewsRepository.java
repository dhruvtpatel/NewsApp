package com.abdulkuddus.talha.newspaper.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.preference.PreferenceManager;
import android.util.Log;

import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.network.NetworkUtils;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * A repository class that handles operations between our UI and our backend (Room + api).
 */
public class NewsRepository {

    private static final String TAG = "NewsRepository";
    private static NewsRepository sInstance;
    private final NewsDao mNewsDao;
    private LiveData<List<News>> mSourceNews;
    private LiveData<List<News>> mLocalNews;
    private LiveData<List<News>> mSavedNews;
    private SharedPreferences mSharedPreferences;
    private ConnectivityManager mConnectivityManager;

    /**
     * Private constructor which gets an instance of our database and retrieves news from it.
     */
    private NewsRepository(Application application) {
        NewsDatabase mDb = NewsDatabase.getInstance(application);
        mNewsDao = mDb.newsDao();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mSourceNews = mNewsDao.getNewsFromCategory(News.SOURCES);
        mLocalNews = mNewsDao.getNewsFromCategory(News.LOCAL);
        mSavedNews = mNewsDao.getSavedNews();
        mConnectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Singleton pattern that returns a single instance of the repository class.
     *
     * @param application application context used to initialise the database
     * @return a single instance of the {@link NewsRepository}.
     */
    public static NewsRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (NewsRepository.class) {
                if (sInstance == null) {
                    sInstance = new NewsRepository(application);
                }
            }
        }
        return sInstance;
    }

    /**
     * Simply returns our list of News Objects when asked for.
     *
     * @return Our List of News objects.
     */
    public LiveData<List<News>> getAllNews(String type) {
        switch (type) {
            case (News.SOURCES):
                return mSourceNews;
            case (News.LOCAL):
                return mLocalNews;
            default:
                throw new IllegalArgumentException("Can only pass News.SOURCES or News.LOCAL");
        }
    }

    public LiveData<List<News>> getSavedNews() {
        return mSavedNews;
    }

    /**
     * Method that does a few checks before creating a new thread to query for new news
     */
    public void forceUpdateNewsAsync(String searchType) {
        Log.v(TAG, "forceUpdateNewsAsync called");
        if (isNetworkAvailable()) {
            Log.d(TAG, "new asynctask");

            String query;
            switch (searchType) {
                case (News.SOURCES):
                    query = mSharedPreferences.getString("sources", "bbc-news");
                    break;
                case (News.LOCAL):
                    query = mSharedPreferences.getString("country", "gb");
                    break;
                default:
                    throw new IllegalArgumentException("Can only pass News.SOURCES or News.LOCAL");
            }
            new RefreshAsyncTask(mNewsDao, query, searchType).execute();
        } //TODO else { Let user know about lack of internet }
    }

    /**
     * Method that updates everything on the same thread it's called on.
     * ONLY USED FOR WORKER. horrible duplicated code.
     */
    public void forceUpdateNewsSync() {
        String query = mSharedPreferences.getString("sources", "bbc-news");
        List<News> newsList = NetworkUtils.getHeadlines(query, News.SOURCES);
        if (newsList != null) {
            mNewsDao.deleteNewsFromCategory(News.SOURCES);
            mNewsDao.insertNewsList(newsList);
        }

        query = mSharedPreferences.getString("country", "gb");
        newsList = NetworkUtils.getHeadlines(query, News.LOCAL);
        if (newsList != null) {
            mNewsDao.deleteNewsFromCategory(News.LOCAL);
            mNewsDao.insertNewsList(newsList);
        }
    }

    /**
     * Method that checks to see if the user is connected to the internet or not
     *
     * @return a true/false value if user is connected or not
     */
    private boolean isNetworkAvailable() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void setNewsAsSaved(News news, boolean setSaveStatus) {
        new SaveAsyncTask(mNewsDao, news, setSaveStatus).execute();
    }

    /**
     * An {@link AsyncTask} that will clear our database of News entries, query the api for the
     * latest news and store the latest news back into the database.
     */
    private static class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {

        private NewsDao mRefreshAsyncDao;
        private String mSourceQuery;
        private String mSearchType;

        public RefreshAsyncTask(NewsDao dao, String query, String searchType) {
            mRefreshAsyncDao = dao;
            mSourceQuery = query;
            mSearchType = searchType;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<News> newsList = NetworkUtils.getHeadlines(mSourceQuery, mSearchType);
            if (newsList != null) {
                mRefreshAsyncDao.deleteNewsFromCategory(mSearchType);
                mRefreshAsyncDao.insertNewsList(newsList);
            }
            return null;
        }
    }

    /**
     * Another {@link AsyncTask} that stores the "saved" status into the database. I shouldn't
     * really be using this to get to background thread (maybe ThreadPools?) but I don't know how yet.
     */
    private static class SaveAsyncTask extends AsyncTask<Void, Void, Void> {

        private NewsDao mSavedAsyncDao;
        private News mNews;
        private boolean mBeingSaved;

        public SaveAsyncTask(NewsDao dao, News news, boolean isSaving) {
            mSavedAsyncDao = dao;
            mNews = news;
            mBeingSaved = isSaving;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mBeingSaved) {
                mNews.setCategory(News.SAVED);
                mSavedAsyncDao.insertNewsItem(mNews);
            } else {
                mSavedAsyncDao.deleteNewsItem(mNews);
            }
            return null;
        }
    }
}