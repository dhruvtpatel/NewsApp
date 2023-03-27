package com.abdulkuddus.talha.newspaper;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdulkuddus.talha.newspaper.data.NewsRepository;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

public class DetailActivity extends AppCompatActivity {

    private boolean mCustomTabOpened = false;
    private NewsRepository mRepository;
    private Toolbar mToolbar;
    private WebView mWebView;
    private LinearLayout mWebviewContainer;
    private ProgressBar mProgressBar;
    private News mNewsItem;
    private boolean mIsAlreadySaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get instance to repository (maybe use ViewModel?)
        mRepository = NewsRepository.getInstance(getApplication());

        // Get reference to views in XML.
        ImageView imageView = findViewById(R.id.app_bar_image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mWebView = findViewById(R.id.news_webview);
        mWebviewContainer = findViewById(R.id.webview_linear_layout);
        mToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the News object from the intent, set the title and ImageView to the news object contents.
        mNewsItem = DetailActivityArgs.fromBundle(getIntent().getExtras()).getNewsObject();
        getSupportActionBar().setTitle(mNewsItem.getSource().getName());
        mIsAlreadySaved = mNewsItem.getCategory().equals(News.SAVED);
        Glide.with(this).load(mNewsItem.getUrlToImage()).into(imageView);

        // Start loading the WebVIew.
        startWebView();
    }

    /**
     * Starts up the WebView and configures the Progress Bar, external links and Chrome Custom Tab.
     */
    private void startWebView() {
        // Enable JavaScript for the WebView (most sites don't function properly without it)
        mWebView.getSettings().setJavaScriptEnabled(true);

        /*
         * Create a new WebViewClient, which controls what happens when a new Url is loaded and what
         * happens when the page has finish loading.
         */
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                String origUrl = mNewsItem.getUrl();

                // Dealing with BBC news redirect
                if (url.getPath().endsWith(origUrl.substring(origUrl.lastIndexOf("-") + 1)))
                    return false;

                // Create a new Chrome Custom Tab, changing the toolbar color.
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(DetailActivity.this, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(DetailActivity.this, url);

                // Set this bool to true so onResume knows to exit the Activity.
                mCustomTabOpened = true;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // When the page has finished loading, remove the Progress Bar.
                mProgressBar.setVisibility(View.GONE);
            }
        });

        /*
         * Create a new WebChromeClient, which controls other things that we can't access with
         * WebViewClient. E.g. onProgressChanged.
         */
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Update the progress bar to the current progress. If platform version is or above
                // Nougat, animate the increase. Else, update normally.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressBar.setProgress(newProgress, true);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }

        });

        // Load the given url from the News object.
        mWebView.loadUrl(mNewsItem.getUrl());
    }

    /**
     * Redirects user to their browser of choice
     */
    private void showInBrowser() {
        // Create intent with article url
        Uri webpage = Uri.parse(mNewsItem.getUrl());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

        // Check if browser available, and start it.
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        } else {
            Toast.makeText(this, R.string.no_browser_available, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the current article the user's reading. (Warning: Bodged together)
     */
    private void saveArticle() {
        mRepository.setNewsAsSaved(mNewsItem, !mIsAlreadySaved);
        if (!mIsAlreadySaved) {
            Snackbar.make(findViewById(R.id.detail_coordinator_layout),
                    R.string.snackbar_saved_news, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.detail_coordinator_layout),
                    R.string.snackbar_removed_saved_news, Snackbar.LENGTH_LONG).show();
        }
        mIsAlreadySaved = !mIsAlreadySaved;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_saveArticle:
                saveArticle();
                return true;
            case R.id.action_viewInBrowser:
                showInBrowser();
                return true;
            case R.id.action_removeSource:
                Toast.makeText(this, "Remove the Source", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If we just came from a Chrome Custom Tab, exit the activity (go back to MainActivity).
        if (mCustomTabOpened) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up and destroy the WebView to avoid memory leaks.
        mWebviewContainer.removeAllViews();
        mWebView.destroy();
    }
}
