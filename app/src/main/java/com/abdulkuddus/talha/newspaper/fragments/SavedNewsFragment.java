package com.abdulkuddus.talha.newspaper.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abdulkuddus.talha.newspaper.NavGraphDirections;
import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.R;
import com.abdulkuddus.talha.newspaper.ui.NewsAdapter;
import com.abdulkuddus.talha.newspaper.ui.PersonalViewModel;
import com.abdulkuddus.talha.newspaper.ui.SavedViewModel;

import java.util.List;

public class SavedNewsFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    private SavedViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsAdapter mAdapter;
    private TextView mNoneSavedTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_saved_news, container, false);

        // Get reference to TextView + RecyclerView.
        mNoneSavedTextView = rootView.findViewById(R.id.no_saved_news_text_view);
        mRecyclerView = rootView.findViewById(R.id.saved_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create a new NewsAdapter and set it to the RecyclerView.
        mAdapter = new NewsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Gets the view model the data is contained it.
        mViewModel = ViewModelProviders.of(this).get(SavedViewModel.class);
        mViewModel.getNewsList().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> news) {
                mAdapter.submitList(news);
                if (news == null || news.size() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mNoneSavedTextView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoneSavedTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void OnItemClick(News news, int position) {
        NavGraphDirections.ActionGlobalDetailActivity action =
                SavedNewsFragmentDirections.actionGlobalDetailActivity(news);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
