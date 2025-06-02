package com.gamevision.agecalculater;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserDatabaseHelper userDatabaseHelper;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.emptyView);  // A TextView to show "No users found" message

        userDatabaseHelper = new UserDatabaseHelper(this);

        // 1. Get the category from intent extras
        String category = getIntent().getStringExtra("category");

        // 2. Fetch all users
        List<UserModel> allUsers = userDatabaseHelper.getAllUsers();

        List<UserModel> filteredUsers;

        // 3. Filter users by category, or show all if category is null, empty, or "all"
        if (category == null || category.isEmpty() || category.equalsIgnoreCase("all")) {
            filteredUsers = allUsers;
        } else {
            filteredUsers = new ArrayList<>();
            for (UserModel user : allUsers) {
                if (category.equalsIgnoreCase(user.getCategory())) {
                    filteredUsers.add(user);
                }
            }
        }

        // 4. Setup adapter and handle empty case
        if (filteredUsers.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            if (category == null || category.isEmpty() || category.equalsIgnoreCase("all")) {
                emptyView.setText("No saved birthdays found.");
            } else {
                emptyView.setText("No users found in category: " + category);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            userAdapter = new UserAdapter(filteredUsers);
            recyclerView.setAdapter(userAdapter);
        }
    }
}
