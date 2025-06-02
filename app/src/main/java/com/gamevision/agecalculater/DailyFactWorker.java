package com.gamevision.agecalculater;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyFactWorker extends Worker {
    List<UserModel> userList;
    UserDatabaseHelper userDatabaseHelper;
    public DailyFactWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        userDatabaseHelper = new UserDatabaseHelper(context);
         userList = userDatabaseHelper.getAllUsers();
    }

    @NonNull
    @Override
    public Result doWork() {

        for (int i = 0; i < userList.size(); i++){
            String birthDateStr = userList.get(i).getBirthdate();     // e.g., "14/01/2010"
            String birthTimeStr = userList.get(i).getBirthtime();     // e.g., "10:04 PM"
            String currentDateStr = userList.get(i).getSpecialDate(); // e.g., "24/05/2025"
            String currentTimeStr = userList.get(i).getSpecialTime(); // e.g., "08:30 AM"
            String name = userList.get(i).getName();
            String gender = userList.get(i).getGender();
            String category = userList.get(i).getCategory();
            if(category.equalsIgnoreCase("myself")){
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd/MM/yyyy'T'hh:mm a")
                    .toFormatter(Locale.US);
            try {
                LocalDate today = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate birthdate = LocalDate.parse(birthDateStr, dateFormatter);

                LocalDate nextBirthday = birthdate.withYear(today.getYear());
                if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }
                long daysLeft = ChronoUnit.DAYS.between(today, nextBirthday);
                long weeksLeft = ChronoUnit.WEEKS.between(today, nextBirthday);
                long monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), nextBirthday.withDayOfMonth(1));
                if (daysLeft == 7) {
                    showNotification("Hurry UP","1 week to go for " + name + "'s birthday!");
                } else if (daysLeft == 1) {
                    showNotification("Hurry UP","Just 1 day left for " + name + "'s birthday!");
                } else if (daysLeft == 0) {
                    showNotification("Hurry UP","Today is " + name + "'s birthday!");
                }

                LocalDateTime birthDateTime = LocalDateTime.parse(birthDateStr + "T" + birthTimeStr, formatter);
                LocalDateTime currentDateTime = LocalDateTime.parse(currentDateStr + "T" + currentTimeStr, formatter);
                Period period = Period.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
                long totalDays = ChronoUnit.DAYS.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
                long totalWeeks = totalDays / 7;
                long totalSeconds = ChronoUnit.SECONDS.between(birthDateTime, currentDateTime);
                long totalMinutes = totalSeconds / 60;
                long totalHours = totalMinutes / 60;
                // 1. Heartbeats (~75 bpm)
                long totalHeartbeats = totalMinutes * 75;
                // 2. Sleep (8 hours/day)
                long sleepHours = totalDays * 8;
                // 3. Meals (3 meals/day)
                long meals = totalDays * 3;
                // 4. Water (2.5 liters/day)
                double water = totalDays * 2.5;
                // 5. Steps (average 6,000/day)
                long steps = totalDays * 6000;
                DayOfWeek dayOfWeek = birthDateTime.getDayOfWeek();
                String weekdayStr = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
                // Example fact — you can fetch from a list, API, or Firestore
                String[] facts = {
                        name+"’ve lived for over a "+totalSeconds+" seconds!",
                        name+" heart has beaten approx. " + totalHeartbeats + " times.",
                        name+" have walked approx. " + steps + " steps.",
                        name+" have consumed approx. " + water + " liters of water.",
                        name+" have eaten approx. " + meals + " meals.",
                        name+" have slept for approx. " + sleepHours + " hours.",
                        name+"’ve lived for over a "+totalDays+" Days!",
                };
                Calendar calendar = Calendar.getInstance();
                int dayOfWeeks = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 7 = Saturday

                String todayFact = facts[dayOfWeeks - 1]; // adjust index
                showNotification("Daily Age Fact", todayFact);
            }catch (Exception e){
                e.printStackTrace();
                String fact = "Did you know? You're about " + (365 * 24) + " hours old if you're 1 year old!";
                showNotification("Daily Age Fact", fact);
            }
            }
            else {
                String fact = "Did you know? You're about " + (365 * 24) + " hours old if you're 1 year old!";
                showNotification("Daily Age Fact", fact);
            }
        }


        return Result.success();
    }

    private void showNotification(String title, String message) {
        Context context = getApplicationContext();
        String channelId = "daily_fact_channel";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Daily Facts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, AllUsersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // 🔽 Wrap it into a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE is required for Android 12+
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
