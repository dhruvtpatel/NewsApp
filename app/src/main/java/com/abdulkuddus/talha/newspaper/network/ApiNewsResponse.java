package com.abdulkuddus.talha.newspaper.network;

import com.abdulkuddus.talha.newspaper.News;

import java.util.ArrayList;

/**
 * Class for Gson to correctly parse JSON into the object needed.
 */
public class ApiNewsResponse {

    public String status;
    public int totalResults;
    public ArrayList<News> articles = new ArrayList<>();

}
