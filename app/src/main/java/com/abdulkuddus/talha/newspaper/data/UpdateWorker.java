package com.abdulkuddus.talha.newspaper.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

// Worker class that is executed in the background, with WorkManager.
public class UpdateWorker extends Worker {

    private static final String TAG = "UpdateWorker";

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork: WORKER RUNNING");

        // Get access to our repository and refresh all news types.
        try {
            Application applicationContext = (Application) getApplicationContext();
            NewsRepository repository = NewsRepository.getInstance(applicationContext);
            repository.forceUpdateNewsSync();
            return Result.success();
        } catch (Throwable e) {
            Log.e(TAG, "doWork: Error trying to update.", e);
            return Result.failure();
        }
    }
}
