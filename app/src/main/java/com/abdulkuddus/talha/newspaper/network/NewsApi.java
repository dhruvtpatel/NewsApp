package com.abdulkuddus.talha.newspaper.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for Retrofit that queries newsapi.org for information.
 */
public interface NewsApi {

    @GET("top-headlines")
    Call<ApiNewsResponse> getSourceHeadlines(
            @Query("sources") String source, @Query("pageSize") int pageSize, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<ApiNewsResponse> getLocalHeadlines(
            @Query("country")String country, @Query("pageSize") int pageSize, @Query("apiKey") String apiKey);

}
