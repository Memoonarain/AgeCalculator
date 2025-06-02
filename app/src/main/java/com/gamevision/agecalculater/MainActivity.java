package com.gamevision.agecalculater;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    Spinner categorySpinner;
    private CheckBox checkBoxMale, checkBoxFemale;
    private CardView cardMale, cardFemale;
    private EditText etDateCurrent, etTimeCurrent, etDate, etTime,etName;
    private Button btnReset, btnCalculate, btnShowAllUsers;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    Map<String, List<String>> categoryMap;
    List<String> mainCategories;
    private UserDatabaseHelper userDatabaseHelper;
    ExpandableListView expandableListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkNotificationPermission();
        // Initialize WorkManager and schedule daily facts
        userDatabaseHelper = new UserDatabaseHelper(this);
        String targetCategory = "MySelf";

        // Fetch users filtered by the target category
        try {
            List<UserModel> allUsers = userDatabaseHelper.getAllUsers();
            List<UserModel> userList = userDatabaseHelper.getUsersByCategory(targetCategory);
            Log.e("userList", allUsers.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        categorySpinner = findViewById(R.id.categorySpinner);
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

        // Set up spinner
        List<String> categories = Arrays.asList("Family", "MySelf", "Friends", "Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        drawerLayout = findViewById(R.id.main);

        expandableListView = findViewById(R.id.expandableListView);

// Define categories and sub-categories
        mainCategories = new ArrayList<>();
        categoryMap = new HashMap<>();

        mainCategories.add("Saved Birthdays");

        List<String> birthdayCategories = Arrays.asList("All","Family", "Friends", "MySelf", "Other");
        List<String> staticDrawerItems = Arrays.asList("FAQ'S", "About Us", "Privacy Policy");
        categoryMap.put("Saved Birthdays", birthdayCategories);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ListView staticList = findViewById(R.id.staticDrawerList);
        ArrayAdapter<String> staticAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, staticDrawerItems);
        staticList.setAdapter(staticAdapter);

        staticList.setOnItemClickListener((parent, view, position, id) -> {
            String item = staticDrawerItems.get(position);
            switch (item) {
                case "FAQ'S":
                    // launch FAQ Activity
                    Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                    break;
                case "About Us":
                    Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                    break;
                case "Privacy Policy":
                    Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        });
// Setup adapter
        ExpandableListAdapter adapterr = new SimpleExpandableListAdapter(
                this,
                getGroupData(),            // Group items
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"GROUP_NAME"},
                new int[]{android.R.id.text1},

                getChildData(),            // Child items
                android.R.layout.simple_list_item_1,
                new String[]{"CHILD_NAME"},
                new int[]{android.R.id.text1}
        );
        expandableListView.setAdapter(adapterr);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String group = mainCategories.get(groupPosition);
            String child = categoryMap.get(group).get(childPosition);

            if (group.equals("Saved Birthdays")) {
                switch (child) {
                    case "All":
                        startActivity(new Intent(MainActivity.this, AllUsersActivity.class).putExtra("category", child));
                        break;
                    case "Family":
                        startActivity(new Intent(MainActivity.this, AllUsersActivity.class).putExtra("category", child));
                        break;
                    case "Friends":
                        startActivity(new Intent(MainActivity.this, AllUsersActivity.class).putExtra("category", child));
                        break;
                    case "MySelf":
                        startActivity(new Intent(MainActivity.this, AllUsersActivity.class).putExtra("category", child));
                        break;
                    case "Other":
                        startActivity(new Intent(MainActivity.this, AllUsersActivity.class).putExtra("category", child));
                        break;
                    default:
                        startActivity(new Intent(MainActivity.this, AgeDetailsActivity.class).putExtra("category", "all"));
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

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
    private List<Map<String, String>> getGroupData() {
        List<Map<String, String>> groupList = new ArrayList<>();
        for (String category : mainCategories) {
            Map<String, String> map = new HashMap<>();
            map.put("GROUP_NAME", category);
            groupList.add(map);
        }
        return groupList;
    }

    private List<List<Map<String, String>>> getChildData() {
        List<List<Map<String, String>>> childList = new ArrayList<>();
        for (String category : mainCategories) {
            List<Map<String, String>> childItems = new ArrayList<>();
            List<String> subItems = categoryMap.get(category);
            if (subItems != null) {
                for (String item : subItems) {
                    Map<String, String> map = new HashMap<>();
                    map.put("CHILD_NAME", item);
                    childItems.add(map);
                }
            }
            childList.add(childItems);
        }
        return childList;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void calculateAge() {
        // Get user input
        String category = categorySpinner.getSelectedItem().toString();
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
        if(category.isEmpty()){
            categorySpinner.requestFocus();
            etDate.setError("Please select category");
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
        intent.putExtra("category", category);
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