package com.abdulkuddus.talha.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.abdulkuddus.talha.newspaper.data.UpdateWorker;

import java.util.concurrent.TimeUnit;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Setup button
        Button button = findViewById(R.id.ob_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishOnboarding();
            }
        });
    }

    /**
     *  Method runs once user finishes, the preference "first_time" is set to false so that it is
     *  not shown again. User is then redirected to the main activity.
     */
    private void finishOnboarding() {

        // Ensure our bg work is only run when there is internet connection.
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Set up a new WorkRequest that will run our Worker, repeating every 12 hours.
        PeriodicWorkRequest updateRequest =
                new PeriodicWorkRequest.Builder(UpdateWorker.class, 12, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance().enqueue(updateRequest);

        // Make sure that this screen does not show up again.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(getString(R.string.pref_key_onboarding), false).apply();
        Intent backToMain = new Intent(this, MainActivity.class);
        startActivity(backToMain);
        finish();
    }

}
