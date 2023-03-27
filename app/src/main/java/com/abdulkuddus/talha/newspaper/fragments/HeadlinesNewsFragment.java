package com.abdulkuddus.talha.newspaper.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkuddus.talha.newspaper.NavGraphDirections;
import com.abdulkuddus.talha.newspaper.News;
import com.abdulkuddus.talha.newspaper.R;
import com.abdulkuddus.talha.newspaper.ui.HeadlinesViewModel;
import com.abdulkuddus.talha.newspaper.ui.NewsAdapter;

import java.util.List;

public class HeadlinesNewsFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    private HeadlinesViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;
    private NewsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_headlines_news, container, false);

        mRecyclerView = rootView.findViewById(R.id.headlines_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mSwipeRefresh = rootView.findViewById(R.id.headlines_swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(HeadlinesViewModel.class);

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
                HeadlinesNewsFragmentDirections.actionGlobalDetailActivity(news);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
