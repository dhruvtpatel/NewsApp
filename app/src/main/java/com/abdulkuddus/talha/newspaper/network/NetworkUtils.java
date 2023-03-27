package com.abdulkuddus.talha.newspaper.network;

import androidx.preference.PreferenceManager;
import android.util.Log;

import com.abdulkuddus.talha.newspaper.News;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Utility class that provides information to and from newsapi.org, including parsing json.
 */
public final class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    // NOTE: Remove key before uploading to GitHub
    private static final String API_KEY = "Get your own one at https://newsapi.org/";
    private static final String BASE_URL = "https://newsapi.org/v2/";

    private NetworkUtils(){}

    /**
     * Gets the latest headlines from a specified source/publisher.
     * @param query The source which headlines should be from
     * @return News objects which contain the latest headlines
     */
    public static ArrayList<News> getHeadlines(String query, String searchType){
        ArrayList<News> newsList = null;

        // Creates a Gson object used to parse JSON from server
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        /*
         * Creates a Retrofit object used to query the server, passing in the base url for the api
         * and a GsonConverterFactory to parse the returned JSON using our specified Gson object above.
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        NewsApi api = retrofit.create(NewsApi.class);

        /*
         * Create a Call object which will hold the response from the server, in the form of an
         * ApiNewsResponse object. We call execute on the object to fetch the data from the server
         * on the thread it's run on. This is going to be called from a bg thread in NewsRepository
         * so network calls won't be on the ui thread.
         */
        Call<ApiNewsResponse> call;
        switch (searchType) {
            case News.SOURCES:
                call = api.getSourceHeadlines(query, 40, API_KEY);
                break;
            case News.LOCAL:
                call = api.getLocalHeadlines(query, 40, API_KEY);
                break;
            default:
                throw new IllegalArgumentException("Can only pass News.SOURCES or News.LOCAL");
        }

        // Iterate through the list, if there is an invalid news item (e.g. No URL) then remove it.
        // Then we set the category of the news type based on the search, and return.
        try {
            Response<ApiNewsResponse> response = call.execute();
            newsList = response.body().articles;
            Iterator<News> iterator = newsList.iterator();
            while (iterator.hasNext()) {
                News news = iterator.next();
                if (news.getUrl() == null || news.getUrl().equals("")){ iterator.remove(); continue; }
                news.setCategory(searchType);
            }
        } catch (IOException e) {
            Log.v(TAG, "Error trying to fetch data");
            e.printStackTrace();
        }

        return newsList;
    }

}