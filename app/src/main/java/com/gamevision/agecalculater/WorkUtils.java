// WorkUtils.java
package com.gamevision.agecalculater;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkUtils {
    public static void scheduleDailyFact(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest dailyWork = new PeriodicWorkRequest.Builder(
                DailyFactWorker.class,
                1, TimeUnit.DAYS
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyFactWork",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
        );
    }
}
