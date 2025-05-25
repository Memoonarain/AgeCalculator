package com.gamevision.agecalculater;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class AgeDetailsActivity extends AppCompatActivity {
    UserDatabaseHelper userDatabaseHelper;
    private ConstraintLayout llBirthdayCountDown;
    private Button btnSaveUser, btnShowSavedUsers;
    TextView txtFactHeartBeat, txtFactSleep, txtFactMeals, txtFactWater, txtFactSteps,txtZodiac;
    private ImageButton btnShowBirthdayCountdown;
    private TextView tvNameGender, tvBornWeekday, tvAgeYears, tvAgeMonths, tvAgeWeeks, tvAgeDays, tvAgeHours,
            tvAgeMinutes, tvAgeSeconds, tvNextBirthday,txtBirthMonths, txtBirthWeeks, txtBirthDays, txtBirthHours, txtBirthMinutes, txtBirthSeconds;
    Handler handler = new Handler();
    Runnable countdownRunnable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_details);

        userDatabaseHelper = new UserDatabaseHelper(this);

        // Initialize UI components
        txtZodiac = findViewById(R.id.txtZodiacSign);
        txtFactHeartBeat = findViewById(R.id.txtFactHeartBeat);
        txtFactSleep = findViewById(R.id.txtFactSleep);
        txtFactMeals = findViewById(R.id.txtFactMeals);
        txtFactWater = findViewById(R.id.txtFactWater);
        txtFactSteps = findViewById(R.id.txtFactSteps);
        tvNameGender = findViewById(R.id.textView2);
        tvBornWeekday = findViewById(R.id.textView);
        tvAgeYears = findViewById(R.id.txtAgeYears);
        tvAgeMonths = findViewById(R.id.txtAgeMonths);
        tvAgeWeeks = findViewById(R.id.txtAgeWeeks);
        tvAgeDays = findViewById(R.id.txtAgeDays);
        tvAgeHours = findViewById(R.id.txtAgeHours);
        tvAgeMinutes = findViewById(R.id.txtAgeMinutes);
        tvAgeSeconds = findViewById(R.id.txtAgeSeconds);
        tvNextBirthday = findViewById(R.id.txtNextBirthday);
        txtBirthMonths = findViewById(R.id.txtBirthMonths);
        txtBirthWeeks = findViewById(R.id.txtBirthWeeks);
        txtBirthDays = findViewById(R.id.txtBirthDays);
        txtBirthHours = findViewById(R.id.txtBirthHours);
        txtBirthMinutes = findViewById(R.id.txtBirthMinutes);
        txtBirthSeconds = findViewById(R.id.txtBirthSeconds);
        btnSaveUser = findViewById(R.id.btnSaveUser);
        btnShowSavedUsers = findViewById(R.id.btnShowAllUsers);
        btnShowBirthdayCountdown = findViewById(R.id.btnShowBirthdayCountdown);
        llBirthdayCountDown = findViewById(R.id.BirthdayCountDownView);
        // Receive intent data
        Intent intent = getIntent();
        String birthDateStr = intent.getStringExtra("birthDate");     // e.g., "14/01/2010"
        String birthTimeStr = intent.getStringExtra("birthTime");     // e.g., "10:04 PM"
        String currentDateStr = intent.getStringExtra("currentDate"); // e.g., "24/05/2025"
        String currentTimeStr = intent.getStringExtra("currentTime"); // e.g., "08:30 AM"
        String gender = intent.getStringExtra("gender");
        String name = intent.getStringExtra("name");

        if (birthDateStr == null || birthTimeStr == null || currentDateStr == null || currentTimeStr == null) {
            finish();
            return;
        }

        String birthDateTimeStr = birthDateStr + "T" + birthTimeStr;
        String currentDateTimeStr = currentDateStr + "T" + currentTimeStr;

        // Define formatter to match input: "dd/MM/yyyyThh:mm a"
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/yyyy'T'hh:mm a")
                .toFormatter(Locale.US);

        Log.d("ParseDebug", "Birth DateTime String: '" + birthDateTimeStr + "'");
        Log.d("LocaleTest", "Default locale: " + Locale.getDefault().toString());

        try {
            LocalDateTime birthDateTime = LocalDateTime.parse(birthDateTimeStr, formatter);
            LocalDateTime currentDateTime = LocalDateTime.parse(currentDateTimeStr, formatter);

            tvNameGender.setText(name + " (" + gender + ")");
            updateAgeDetails(birthDateTime, currentDateTime);
            updateNextBirthday(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
            startBirthdayCountdown(birthDateTime);
            String zodiacSign = getZodiacSign(birthDateTime.toLocalDate());
            txtZodiac.setText("Zodiac Sign: "+zodiacSign);

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            finish(); // You could also show a Toast here
        }
        btnShowBirthdayCountdown.setOnClickListener(v -> {
            if (llBirthdayCountDown.getVisibility() == View.VISIBLE) {
                llBirthdayCountDown.setVisibility(View.GONE);
            } else {
                llBirthdayCountDown.setVisibility(View.VISIBLE);
            }

        });
        btnSaveUser.setOnClickListener(v -> {

            userDatabaseHelper.addUser(name, gender, birthDateStr, currentDateStr, birthTimeStr, currentTimeStr);

            Toast.makeText(AgeDetailsActivity.this, "User saved successfully!", Toast.LENGTH_SHORT).show();
        });
        btnShowSavedUsers.setOnClickListener(v -> {
            Intent intent1 = new Intent(AgeDetailsActivity.this, AllUsersActivity.class);
            startActivity(intent1);
        });

    }
    private void startBirthdayCountdown(LocalDateTime birthDateTime) {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();

                // Calculate next birthday based on birthDateTime
                LocalDateTime nextBirthday = birthDateTime.withYear(now.getYear());
                if (!nextBirthday.isAfter(now)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }

                Duration duration = Duration.between(now, nextBirthday);
                long totalSeconds = duration.getSeconds();

                long months = Period.between(now.toLocalDate(), nextBirthday.toLocalDate()).getMonths();
                long weeks = totalSeconds / (7 * 24 * 60 * 60);
                long days = (totalSeconds / (24 * 60 * 60)) % 7;
                long hours = (totalSeconds / (60 * 60)) % 24;
                long minutes = (totalSeconds / 60) % 60;
                long seconds = totalSeconds % 60;

                runOnUiThread(() -> {
                    txtBirthMonths.setText(String.valueOf(months));
                    txtBirthWeeks.setText(String.valueOf(weeks));
                    txtBirthDays.setText(String.valueOf(days));
                    txtBirthHours.setText(String.valueOf(hours));
                    txtBirthMinutes.setText(String.valueOf(minutes));
                    txtBirthSeconds.setText(String.valueOf(seconds));
                });

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(countdownRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(countdownRunnable);
    }

    private void updateAgeDetails(LocalDateTime birthDateTime, LocalDateTime currentDateTime) {
        Period period = Period.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
        long totalDays = ChronoUnit.DAYS.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
        long totalWeeks = totalDays / 7;
        long totalSeconds = ChronoUnit.SECONDS.between(birthDateTime, currentDateTime);
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;

        // Set age
        tvAgeYears.setText(String.valueOf(period.getYears()));
        tvAgeMonths.setText(String.valueOf(period.toTotalMonths()));
        tvAgeWeeks.setText(String.valueOf(totalWeeks));
        tvAgeDays.setText(String.valueOf(totalDays));
        tvAgeHours.setText(String.valueOf(totalHours));
        tvAgeMinutes.setText(String.valueOf(totalMinutes));
        tvAgeSeconds.setText(String.valueOf(totalSeconds));

        // Set weekday
        DayOfWeek dayOfWeek = birthDateTime.getDayOfWeek();
        String weekdayStr = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
        tvBornWeekday.setText("Born on: " + weekdayStr);

        // ====== Calculate Interesting Facts ======

        // 1. Heartbeats (~75 bpm)
        long totalHeartbeats = totalMinutes * 75;
        txtFactHeartBeat.setText("Your heart has beaten approx. " + totalHeartbeats + " times.");

        // 2. Sleep (8 hours/day)
        long sleepHours = totalDays * 8;
        txtFactSleep.setText("You have slept for approx. " + sleepHours + " hours.");

        // 3. Meals (3 meals/day)
        long meals = totalDays * 3;
        txtFactMeals.setText("You have eaten approx. " + meals + " meals.");

        // 4. Water (2.5 liters/day)
        double water = totalDays * 2.5;
        txtFactWater.setText("You have consumed approx. " + water + " liters of water.");

        // 5. Steps (average 6,000/day)
        long steps = totalDays * 6000;
        txtFactSteps.setText("You have walked approx. " + steps + " steps.");
    }
    private String getZodiacSign(LocalDate birthDate) {
        int day = birthDate.getDayOfMonth();
        int month = birthDate.getMonthValue();

        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Aquarius ♒";
        else if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Pisces ♓";
        else if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Aries ♈";
        else if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Taurus ♉";
        else if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Gemini ♊";
        else if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Cancer ♋";
        else if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Leo ♌";
        else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Virgo ♍";
        else if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Libra ♎";
        else if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Scorpio ♏";
        else if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Sagittarius ♐";
        else return "Capricorn ♑"; // (Dec 22 – Jan 19)
    }

    private void updateNextBirthday(LocalDate birthDate, LocalDate currentDate) {
        LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
        if (!nextBirthday.isAfter(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        Period untilNextBirthday = Period.between(currentDate, nextBirthday);
        String months = untilNextBirthday.getMonths() > 0 ? untilNextBirthday.getMonths() + " months" : "";
        String days = untilNextBirthday.getDays() > 0 ? untilNextBirthday.getDays() + " days" : "";

        String nextBirthdayStr = (months.isEmpty() ? "" : months) + (months.isEmpty() || days.isEmpty() ? "" : " and ") + (days.isEmpty() ? "" : days);
        if (nextBirthdayStr.isEmpty()) {
            nextBirthdayStr = "Today is your birthday!";
        }
        tvNextBirthday.setText(nextBirthdayStr);
    }
}
