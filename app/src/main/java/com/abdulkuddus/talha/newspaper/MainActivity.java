package com.abdulkuddus.talha.newspaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NavController navController;
    private BottomNavigationView bottomNav;

    /**
     * Sets up the activity to launch the navigation component.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // Check if it's user's first time. If so, show onboarding screen.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getString(R.string.pref_key_onboarding), true)) {
            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivity(onboarding);
            finish();
        }

        setContentView(R.layout.activity_main);

        // Get a reference to our toolbar from the layout and set it as the SupportActionBar.
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Creates a config that specifies the app's top level destinations (so the appbar
        // doesn't show an up arrow on these screens).
        AppBarConfiguration config = new AppBarConfiguration.Builder(R.id.mainFragment,
                R.id.headlinesNewsFragment, R.id.savedNewsFragment).build();

        // Get a reference to our navigation controller (the nav_host_fragment).
        navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
        NavigationUI.setupWithNavController(toolbar, navController, config);

        // Get a reference to our bottom navigation view.
        bottomNav = findViewById(R.id.bottom_nav);

        /*
         * When a new "item" is selected from our bottom nav bar, check to see if they're already
         * in that fragment. If so, don't do anything. If it's a new fragment, tell the
         * navController to navigate to the new destination, with our animations.
         * Can't use static method "setupWithNavController" because of bug that forces default
         * animations (ver. alpha09).
         */
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                NavDestination currentDestination = navController.getCurrentDestination();
                int destinationChosen = menuItem.getItemId();
                if (destinationChosen == getActionFromDestination(currentDestination.getId())) { return true; }
                navController.navigate(destinationChosen);
                return true;
            }
        });

        /*
         * When the destination has changed, update bottom nav bar.
         */
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                bottomNav.getMenu().findItem(getActionFromDestination(destination.getId())).setChecked(true);
            }
        });

    }

    public int getActionFromDestination(int destinationId) {
        int actionId = 0;
        switch (destinationId) {
            case R.id.mainFragment:
                actionId = R.id.action_global_mainFragment;
                break;
            case R.id.headlinesNewsFragment:
                actionId = R.id.action_global_headlinesNewsFragment;
                break;
            case R.id.savedNewsFragment:
                actionId = R.id.action_global_savedNewsFragment;
        }
        return actionId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_settings):
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}