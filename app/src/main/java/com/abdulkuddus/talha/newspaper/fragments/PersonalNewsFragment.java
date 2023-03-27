package com.abdulkuddus.talha.newspaper.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkuddus.talha.newspaper.NavGraphDirections;
import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.ui.NewsAdapter;
import com.abdulkuddus.talha.newspaper.ui.PersonalViewModel;
import com.abdulkuddus.talha.newspaper.R;

import java.util.List;

public class PersonalNewsFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    private PersonalViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflates the fragment's XML layout (containing the RecyclerView).
        View rootView = inflater.inflate(R.layout.fragment_personal_news, container, false);

        // Get reference to RecyclerView.
        mRecyclerView = rootView.findViewById(R.id.personal_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create a new NewsAdapter and set it to the RecyclerView.
        mAdapter = new NewsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        // Get reference to our Swipe Refresh View and set it's colours.
        mSwipeRefresh = rootView.findViewById(R.id.personal_swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Gets the view model the data is contained it.
        mViewModel = ViewModelProviders.of(this).get(PersonalViewModel.class);

        // Observes the data and automatically refreshes the recycler view when a change is detected.
        mViewModel.getNewsList().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> news) {
                if (news != null && news.size() > 0) {
                    mSwipeRefresh.setRefreshing(false);
                    mAdapter.submitList(news);
                } else if (!mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(true);
                    mViewModel.refreshNews();
                }
            }
        });

        // Set a listener on the Refresh View to check when it is activated, and refresh the news
        // when it is.
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refreshNews();
            }
        });
    }

    @Override
    public void OnItemClick(News news, int position) {
        NavGraphDirections.ActionGlobalDetailActivity action =
                PersonalNewsFragmentDirections.actionGlobalDetailActivity(news);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

}