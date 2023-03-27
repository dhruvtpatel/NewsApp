package com.abdulkuddus.talha.newspaper;

import android.os.Parcel;
import android.os.Parcelable;

import com.abdulkuddus.talha.newspaper.data.DateConverter;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * The News class contains details about a single article
 */
@Entity(tableName = "news_table", primaryKeys = {"url", "category"}) //Room
public class News implements Parcelable {

    @Ignore public static final String SOURCES = "sources";
    @Ignore public static final String LOCAL = "local";
    @Ignore public static final String SAVED = "saved";

    private String title;
    private String description;
    @NonNull private String url;
    private String urlToImage;
    @Embedded private Source source;
    private Date publishedAt;
    @NonNull private String category = "uncategorised";

    /**
     * Constructs a News object which is an article displayed to the user.
     * @param title The title of the article
     * @param description The subtitle/first few lines of the article
     * @param url The url to the article
     * @param urlToImage The url to the refresh image for the article
     * @param source The publisher of the article
     * @param publishedAt The date and time of publication
     */
    public News(String title, String description, String url, String urlToImage,
                Source source, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.source = source;
        this.publishedAt = publishedAt;
    }

    public void setCategory(String category) { this.category = category; }

    /*
    * Getter methods for the News object
    */
    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getUrl() { return url; }

    public String getUrlToImage() { return urlToImage; }

    public Source getSource() { return source; }

    public Date getPublishedAt() { return publishedAt; }

    public String getCategory() { return category; }

    /**
     * Inner class that contains details about the source/publisher of the article, mainly here
     * for Gson parsing.
     */
    public static class Source {

        private String id;
        private String name;

        public Source(String id, String name){ this.id = id; this.name = name; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /**
     * Parcelable implementation which creates News object from a Parcel
     * @param in The parcel that needs to be 'unpacked/inflated'
     */
    protected News(Parcel in) {
        String[] sourceArray = new String[2];

        title = in.readString();
        description = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        in.readStringArray(sourceArray);
        Long timestamp = in.readLong();
        category = in.readString();

        source = new Source(sourceArray[0], sourceArray[1]);
        publishedAt = DateConverter.toDate(timestamp);
    }

    /**
     * Parcelable implementation which creates a Parcel from a News object.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] sourceArray = {this.source.getId(), this.source.getName()};
        Long timestamp = DateConverter.toTimestamp(this.publishedAt);

        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(urlToImage);
        dest.writeStringArray(sourceArray);
        dest.writeLong(timestamp);
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

}
