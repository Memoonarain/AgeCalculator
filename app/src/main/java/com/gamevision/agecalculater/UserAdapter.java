package com.gamevision.agecalculater;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserModel> userList;

    public UserAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtFactHeartBeat, txtFactSleep, txtFactMeals, txtFactWater, txtFactSteps,txtZodiac;

        TextView tvCategory, tvNameGender, tvBornWeekday, tvAgeYears, tvAgeMonths, tvAgeWeeks, tvAgeDays, tvAgeHours,
                tvAgeMinutes, tvAgeSeconds, tvNextBirthday, txtBirthMonths, txtBirthWeeks, txtBirthDays,
                txtBirthHours, txtBirthMinutes, txtBirthSeconds;
        ConstraintLayout llBirthdayCountDown;
        ImageButton btnShowBirthdayCountdown;
        Handler handler = new Handler();
        Runnable countdownRunnable;

        public UserViewHolder(View itemView) {
            super(itemView);

            // Bind views from item_age_details.xml
            tvCategory = itemView.findViewById(R.id.txtCategory);
            txtZodiac = itemView.findViewById(R.id.txtZodiacSign);
            txtFactHeartBeat = itemView.findViewById(R.id.txtFactHeartBeat);
            txtFactSleep = itemView.findViewById(R.id.txtFactSleep);
            txtFactMeals = itemView.findViewById(R.id.txtFactMeals);
            txtFactWater = itemView.findViewById(R.id.txtFactWater);
            txtFactSteps = itemView.findViewById(R.id.txtFactSteps);
            btnShowBirthdayCountdown = itemView.findViewById(R.id.btnShowBirthdayCountdown);
            llBirthdayCountDown = itemView.findViewById(R.id.BirthdayCountDownView);
            tvNameGender = itemView.findViewById(R.id.textView2);
            tvBornWeekday = itemView.findViewById(R.id.textView);
            tvAgeYears = itemView.findViewById(R.id.txtAgeYears);
            tvAgeMonths = itemView.findViewById(R.id.txtAgeMonths);
            tvAgeWeeks = itemView.findViewById(R.id.txtAgeWeeks);
            tvAgeDays = itemView.findViewById(R.id.txtAgeDays);
            tvAgeHours = itemView.findViewById(R.id.txtAgeHours);
            tvAgeMinutes = itemView.findViewById(R.id.txtAgeMinutes);
            tvAgeSeconds = itemView.findViewById(R.id.txtAgeSeconds);
            tvNextBirthday = itemView.findViewById(R.id.txtNextBirthday);
            txtBirthMonths = itemView.findViewById(R.id.txtBirthMonths);
            txtBirthWeeks = itemView.findViewById(R.id.txtBirthWeeks);
            txtBirthDays = itemView.findViewById(R.id.txtBirthDays);
            txtBirthHours = itemView.findViewById(R.id.txtBirthHours);
            txtBirthMinutes = itemView.findViewById(R.id.txtBirthMinutes);
            txtBirthSeconds = itemView.findViewById(R.id.txtBirthSeconds);
        }

        public void stopCountdown() {
            if (handler != null && countdownRunnable != null) {
                handler.removeCallbacks(countdownRunnable);
            }
        }

        public void startCountdown(LocalDateTime birthDateTime) {
            stopCountdown();

            countdownRunnable = new Runnable() {
                @Override
                public void run() {
                    LocalDateTime now = LocalDateTime.now();
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

                    txtBirthMonths.setText(String.valueOf(months));
                    txtBirthWeeks.setText(String.valueOf(weeks));
                    txtBirthDays.setText(String.valueOf(days));
                    txtBirthHours.setText(String.valueOf(hours));
                    txtBirthMinutes.setText(String.valueOf(minutes));
                    txtBirthSeconds.setText(String.valueOf(seconds));

                    handler.postDelayed(this, 1000);
                }
            };

            handler.post(countdownRunnable);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.age_details_layout, parent, false);
        return new UserViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        String birthDateStr = user.getBirthdate();     // e.g., "14/01/2010"
        String birthTimeStr = user.getBirthtime();     // e.g., "10:04 PM"
        String currentDateStr = user.getSpecialDate(); // e.g., "24/05/2025"
        String currentTimeStr = user.getSpecialTime(); // e.g., "08:30 AM"
        String name = user.getName();
        String gender = user.getGender();


        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/yyyy'T'hh:mm a")
                .toFormatter(Locale.US);
        try {
            LocalDateTime birthDateTime = LocalDateTime.parse(birthDateStr + "T" + birthTimeStr, formatter);
            LocalDateTime currentDateTime = LocalDateTime.parse(currentDateStr + "T" + currentTimeStr, formatter);
            if (user.getCategory().isEmpty()){
                holder.tvCategory.setText("Unkown");
            }else {

                holder.tvCategory.setText("Category: "+user.getCategory());
            }
            holder.tvNameGender.setText(name + " (" + gender + ")");
            updateAgeDetails(holder, birthDateTime, currentDateTime);
            updateNextBirthday(holder, birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
            holder.startCountdown(birthDateTime);
            String zodiacSign = getZodiacSign(birthDateTime.toLocalDate());
            holder.txtZodiac.setText("Zodiac Sign: "+zodiacSign);

        } catch (Exception e) {
            e.printStackTrace();
            holder.tvNameGender.setText("Invalid date/time");
            holder.tvCategory.setText("Invalid date/time");
            holder.tvBornWeekday.setText("Invalid date/time");
            holder.tvAgeYears.setText("Invalid date/time");
        }
        holder.btnShowBirthdayCountdown.setOnClickListener(v -> {
            if (holder.llBirthdayCountDown.getVisibility() == View.VISIBLE) {
                holder.llBirthdayCountDown.setVisibility(View.GONE);
            } else {
                holder.llBirthdayCountDown.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void updateAgeDetails(UserViewHolder holder, LocalDateTime birthDateTime, LocalDateTime currentDateTime) {
        Period period = Period.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
        long totalDays = ChronoUnit.DAYS.between(birthDateTime.toLocalDate(), currentDateTime.toLocalDate());
        long totalWeeks = totalDays / 7;
        long totalSeconds = ChronoUnit.SECONDS.between(birthDateTime, currentDateTime);
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;

        DayOfWeek dayOfWeek = birthDateTime.getDayOfWeek();
        String weekdayStr = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());

        holder.tvBornWeekday.setText("Born on \"" + weekdayStr + "\"");
        holder.tvAgeYears.setText(String.valueOf(period.getYears()));
        holder.tvAgeMonths.setText(String.valueOf(period.toTotalMonths()));
        holder.tvAgeWeeks.setText(String.valueOf(totalWeeks));
        holder.tvAgeDays.setText(String.valueOf(totalDays));
        holder.tvAgeHours.setText(String.valueOf(totalHours));
        holder.tvAgeMinutes.setText(String.valueOf(totalMinutes));
        holder.tvAgeSeconds.setText(String.valueOf(totalSeconds));
        // 1. Heartbeats (~75 bpm)
        long totalHeartbeats = totalMinutes * 75;
        holder.txtFactHeartBeat.setText("Your heart has beaten approx. " + totalHeartbeats + " times.");

        // 2. Sleep (8 hours/day)
        long sleepHours = totalDays * 8;
        holder.txtFactSleep.setText("You have slept for approx. " + sleepHours + " hours.");

        // 3. Meals (3 meals/day)
        long meals = totalDays * 3;
        holder.txtFactMeals.setText("You have eaten approx. " + meals + " meals.");

        // 4. Water (2.5 liters/day)
        double water = totalDays * 2.5;
        holder.txtFactWater.setText("You have consumed approx. " + water + " liters of water.");

        // 5. Steps (average 6,000/day)
        long steps = totalDays * 6000;
        holder.txtFactSteps.setText("You have walked approx. " + steps + " steps.");
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

    private void updateNextBirthday(UserViewHolder holder, LocalDate birthDate, LocalDate currentDate) {
        LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
        if (!nextBirthday.isAfter(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        Period untilNextBirthday = Period.between(currentDate, nextBirthday);
        String months = untilNextBirthday.getMonths() > 0 ? untilNextBirthday.getMonths() + " months" : "";
        String days = untilNextBirthday.getDays() > 0 ? untilNextBirthday.getDays() + " days" : "";

        String nextBirthdayStr = (months.isEmpty() ? "" : months) + (months.isEmpty() || days.isEmpty() ? "" : " and ") + (days.isEmpty() ? "" : days);
        if (nextBirthdayStr.isEmpty()) {
            nextBirthdayStr = "Today is their birthday!";
        }

        holder.tvNextBirthday.setText(nextBirthdayStr);
    }
}
