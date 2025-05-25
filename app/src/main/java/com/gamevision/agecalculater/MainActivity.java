package com.gamevision.agecalculater;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CheckBox checkBoxMale, checkBoxFemale;
    private CardView cardMale, cardFemale;
    private EditText etDateCurrent, etTimeCurrent, etDate, etTime,etName;
    private Button btnReset, btnCalculate, btnShowAllUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnShowAllUsers = findViewById(R.id.btnShowAllUsers);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);
        etDateCurrent = findViewById(R.id.etDateCurrent);
        etTimeCurrent = findViewById(R.id.etTimeCurrent);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        cardMale = findViewById(R.id.imageView2);
        cardFemale = findViewById(R.id.imageView3);
        checkBoxMale = findViewById(R.id.checkBox3);
        checkBoxFemale = findViewById(R.id.checkBox2);
        etName = findViewById(R.id.editTextText);

btnShowAllUsers.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllUsersActivity.class)));
        int i =1, j= 2;
        etDate.setOnClickListener(v -> showDatePicker(i));
        etTime.setOnClickListener(v -> showTimePicker(i));
        etDateCurrent.setOnClickListener(v -> showDatePicker(j));
        etTimeCurrent.setOnClickListener(v -> showTimePicker(j));
        btnReset.setOnClickListener(v -> resetAll());
        btnCalculate.setOnClickListener(v -> calculateAge());
        setDefaultDateTime();
        setupGenderCheckboxes();
    }

    private void calculateAge() {
        String birthDate = etDate.getText().toString().trim();
        String birthTime = etTime.getText().toString().trim();
        String currentDate = etDateCurrent.getText().toString().trim();
        String currentTime = etTimeCurrent.getText().toString().trim();
        String name = etName.getText().toString().trim();
        boolean isMale = checkBoxMale.isChecked();
        boolean isFemale = checkBoxFemale.isChecked();

        // Validation
        if (!isMale && !isFemale) {
            etDate.requestFocus();
            etDate.setError("Please select gender");
            return;
        }
        if(name.isEmpty()){
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }
        if (birthDate.isEmpty()) {
            etDate.setError("Select birth date");
            etDate.requestFocus();
            return;
        }

        if (birthTime.isEmpty()) {
            etTime.setError("Select birth time");
            etTime.requestFocus();
            return;
        }

        // Gender string
        String gender = isMale ? "Male" : "Female";

        // Send to AgeDetailsActivity
        Intent intent = new Intent(MainActivity.this, AgeDetailsActivity.class);
        intent.putExtra("birthDate", birthDate);
        intent.putExtra("birthTime", birthTime);
        intent.putExtra("currentDate", currentDate);
        intent.putExtra("currentTime", currentTime);
        intent.putExtra("gender", gender);
        intent.putExtra("name", name);
        startActivity(intent);
    }


    private void setupGenderCheckboxes() {
        checkBoxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxFemale.setChecked(false);
                cardMale.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cardFemale.setCardBackgroundColor(getResources().getColor(R.color.white));
            } else {
                cardMale.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        checkBoxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxMale.setChecked(false);
                cardFemale.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cardMale.setCardBackgroundColor(getResources().getColor(R.color.white));
            } else {
                cardFemale.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void showDatePicker(int i) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format: dd/MM/yyyy
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    if (i==1)
                    etDate.setText(selectedDate);
                    else
                        etDateCurrent.setText(selectedDate);

                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(int i) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Format: hh:mm AM/PM
                    Calendar pickedTime = Calendar.getInstance();
                    pickedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    pickedTime.set(Calendar.MINUTE, selectedMinute);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    if (i==1)
                    etTime.setText(sdf.format(pickedTime.getTime()));
                    else
                        etTimeCurrent.setText(sdf.format(pickedTime.getTime()));
                },
                hour, minute, false // false = 12-hour format
        );
        timePickerDialog.show();
    }
    private void setDefaultDateTime() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String currentDate = dateFormat.format(now.getTime());
        String currentTime = timeFormat.format(now.getTime());

        etDateCurrent.setText(currentDate);
        etTimeCurrent.setText(currentTime);
        etDate.setHint("12/02/24");
        etTime.setText("12:00 AM");
    }

    private void resetAll() {
        // Reset gender checkboxes
        checkBoxMale.setChecked(false);
        checkBoxFemale.setChecked(false);
        etName.setText("");
        etName.setHint("UserName");
        // Reset card colors
        cardMale.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardFemale.setCardBackgroundColor(getResources().getColor(R.color.white));

        // Reset date/time to current
        setDefaultDateTime();
    }

}