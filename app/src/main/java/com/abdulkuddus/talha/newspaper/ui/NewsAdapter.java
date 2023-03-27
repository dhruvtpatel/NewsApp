package com.abdulkuddus.talha.newspaper.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends ListAdapter<News, NewsAdapter.NewsViewHolder> {

    private OnItemClickListener mClickListener;
    private Fragment mFragment;

    public NewsAdapter(Fragment fragment) {
        super(DIFF_CALLBACK);
        mFragment = fragment;
    }

    /**
     * Inflates a new item from news_item_layout.xml when needed
     * @return a {@link NewsViewHolder} object which contains references to the inflated layout.
     */
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflates news_item_layout.xml for this item.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_layout, parent, false);

        // Attaches references to layout and returns it.
        return new NewsViewHolder(view);
    }

    /**
     * Binds our data to the Views in the ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        // Get the news article for this position.
        News article = getItem(position);

        // Get the title, image, publisher and date for this news article.
        String title = article.getTitle();
        String url = article.getUrlToImage();
        String publisher = article.getSource().getName();
        String date = formatDate(article.getPublishedAt());

        // Set the news article context onto the views.
        holder.newsTitle.setText(title);
        holder.newsPublisher.setText(publisher);
        holder.newsDate.setText(date);
        Glide.with(mFragment)
                .load(article.getUrlToImage())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(holder.newsImage);
    }

    /**
     * Formats Dates into Strings
     * @param date The date object to be converted
     * @return A formatted String
     * TODO Change the string depending on how long ago article was posted (e.g. "35 mins ago, yesterday")
     */
    private String formatDate(Date date) {
        SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance();
        return formatter.format(date);
    }

    /**
     * Implement DiffUtil, which compares 2 objects to check if they are the same. This is used to
     * provide animations and fluid updates to the UI by only removing the items that have changed.
     */
    private static final DiffUtil.ItemCallback<News> DIFF_CALLBACK = new DiffUtil.ItemCallback<News>() {
        @Override
        public boolean areItemsTheSame(@NonNull News oldItem, @NonNull News newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull News oldItem, @NonNull News newItem) {
            // Checks if all variables are the same. If one is different, false is returned.
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getUrl().equals(newItem.getUrl()) &&
                    oldItem.getUrlToImage().equals(newItem.getUrlToImage()) &&
                    oldItem.getSource() == newItem.getSource() &&
                    oldItem.getPublishedAt() == newItem.getPublishedAt() &&
                    oldItem.getCategory().equals(newItem.getCategory());
        }
    };

    /**
     * Inner class that provides references to the views for each item/row
     */
    class NewsViewHolder extends RecyclerView.ViewHolder {

        ImageView newsImage;
        TextView newsTitle;
        TextView newsPublisher;
        TextView newsDate;

        public NewsViewHolder(View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsPublisher = itemView.findViewById(R.id.news_publisher);
            newsDate = itemView.findViewById(R.id.news_date);

            // Call OnItemClick, which should be implemented by the UI.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mClickListener != null && position != RecyclerView.NO_POSITION) {
                        mClickListener.OnItemClick(getItem(position), position);
                    }
                }
            });
        }

    }

    /**
     * Interface that allows implementers to get the News object of the item that is selected.
     */
    public interface OnItemClickListener {
        void OnItemClick(News news, int position);
    }

    /**
     * Sets the implemented click listener onto this object.
     * @param clickListener The click listener that is implemented.
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

}
