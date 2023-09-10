package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    private MyDatabaseHelper databaseHelper;
    TextView noTasksTextView;
    TextView myTaskTextView;

    int cardViewCount = 0;
    TextView taskPendingCountTextView;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        databaseHelper = new MyDatabaseHelper(this);

        setContentView(R.layout.activity_main);
        setTitle(" Main Menu");

        taskPendingCountTextView = findViewById(R.id.task_pending_count);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        noTasksTextView = findViewById(R.id.noTasksTextView);
        TextView myTaskTextView = findViewById(R.id.MyTaskTextView);

        CardView createTask_cardView = findViewById(R.id.create_task_card);
        CardView taskSharing_cardView = findViewById(R.id.task_sharing_card);
        CardView focusSession_cardView = findViewById(R.id.focus_session_card);
        CardView history_CardView = findViewById(R.id.history_card);

        updateTaskList();

        createTask_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Task_Create.class);
                startActivity(intent);
            }
        });

        taskSharing_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to another activity
            }
        });

        focusSession_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to another activity
            }
        });

        history_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Task_History.class);
                startActivity(intent);
            }
        });

        // Enable ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            LinearLayout iconLayout = new LinearLayout(this);
            iconLayout.setOrientation(LinearLayout.HORIZONTAL);
            iconLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

            ImageView additionalIcon = new ImageView(this);
            additionalIcon.setImageResource(R.drawable.ic_search);

            additionalIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Search icon clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Task_Search.class);
                    startActivity(intent);
                }
            });

            // Create some space between icons
            View spacerView = new View(this);
            LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                    35, LinearLayout.LayoutParams.MATCH_PARENT);
            spacerView.setLayoutParams(spacerParams);

            ImageView searchIcon = new ImageView(this);
            searchIcon.setImageResource(R.drawable.ic_account);

            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Account icon clicked", Toast.LENGTH_SHORT).show();
                }
            });

            iconLayout.addView(additionalIcon);
            iconLayout.addView(spacerView);
            iconLayout.addView(searchIcon);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;

            actionBar.setCustomView(iconLayout, layoutParams);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.create:
                        Intent intent = new Intent(MainActivity.this, Task_Create.class);
                        startActivity(intent);
                        break;
                    case R.id.share:
                        Toast.makeText(MainActivity.this, "Share Task Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.history:
                        Toast.makeText(MainActivity.this, "Task History Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.pomodoro:
                        Toast.makeText(MainActivity.this, "Focus Session", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.homeWidget:
                        Toast.makeText(MainActivity.this, "Home Widget", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        // retrieve task names from the database and create CardView elements
        List<String> taskNames = databaseHelper.getAllTaskNames();

        LinearLayout taskCardsContainer = findViewById(R.id.my_task_container);

        if (taskCardsContainer == null) {
            Log.e("Error", "taskCardsContainer is null");
        } else {
            Log.d("Debug", "taskCardsContainer found");
        }

        for (String taskName : taskNames) {
            CardView taskCardView = new CardView(this);

            // Set layout parameters for the CardView
            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_top); // Set margin top
            taskCardView.setLayoutParams(cardLayoutParams);

            // Set other CardView attributes like background color and radius

            TextView taskNameTextView = new TextView(this);
            taskNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            taskNameTextView.setPadding(
                    getResources().getDimensionPixelSize(R.dimen.card_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_padding));

            taskNameTextView.setText(taskName);
            taskNameTextView.setTextColor(getResources().getColor(R.color.primary_text));
            taskNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            taskCardView.addView(taskNameTextView);
            taskCardsContainer.addView(taskCardView);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskList(); // Update the CardViews when the MainActivity is resumed
    }

    public void updateTaskList() {
        List<String> taskNames = databaseHelper.getAllTaskNames();

        if (taskNames.isEmpty()) {
            // Display "No tasks currently" message
            noTasksTextView.setVisibility(View.VISIBLE);
            LinearLayout taskContainer = findViewById(R.id.my_task_container);
            taskContainer.removeAllViews();

        } else {
            // Hide the "No tasks currently" message
            noTasksTextView.setVisibility(View.GONE);
            LinearLayout taskContainer = findViewById(R.id.my_task_container);
            taskContainer.removeAllViews();

            int cardViewCount = 0;
            // create and add task CardViews
            for (String taskName : taskNames) {
                CardView taskCard = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.card_height)
                );
                cardParams.setMargins(
                        getResources().getDimensionPixelSize(R.dimen.card_margin_start),
                        getResources().getDimensionPixelSize(R.dimen.card_margin_top),
                        getResources().getDimensionPixelSize(R.dimen.card_margin_end),
                        getResources().getDimensionPixelSize(R.dimen.card_margin_bottom)
                );

                taskCard.setLayoutParams(cardParams);

                taskCard.setCardBackgroundColor(getResources().getColor(R.color.card_background));
                taskCard.setRadius(getResources().getDimensionPixelSize(R.dimen.card_corner_radius));

                LinearLayout taskLayout = new LinearLayout(this);
                taskLayout.setOrientation(LinearLayout.VERTICAL);
                taskCard.addView(taskLayout);

                TextView taskTextView = new TextView(this);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int padding = getResources().getDimensionPixelSize(R.dimen.card_padding);
                taskTextView.setLayoutParams(textParams);
                taskTextView.setPadding(padding, padding, padding, padding);
                taskTextView.setText(taskName);
                taskTextView.setTextColor(getResources().getColor(R.color.primary_text));
                taskTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                taskCard.addView(taskTextView);

                // Add due date
                TextView dueDateTextView = new TextView(this);
                LinearLayout.LayoutParams dueDateParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int dueDatePadding = getResources().getDimensionPixelSize(R.dimen.card_padding);
                dueDateParams.setMargins(0, dueDatePadding, dueDatePadding, dueDatePadding);
                dueDateParams.gravity = Gravity.END; // Align to the right
                dueDateTextView.setLayoutParams(dueDateParams);

                // Retrieve due date for the current task
                String dueDate = getDueDateForTask(taskName);

                // Set text for the due date TextView
                String formattedDueDate = "Due Date: " + dueDate; // Add "Due Date:" prefix
                dueDateTextView.setText(formattedDueDate);
                //dueDateTextView.setTextColor(getResources().getColor(R.color.secondary_text));
                dueDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                taskLayout.addView(dueDateTextView);

                // Set a unique tag to identify the card
                taskCard.setTag(taskName);

                taskCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selectedTaskName = (String) v.getTag(); // Get the task name associated with the clicked card view
                        Intent intent = new Intent(MainActivity.this, Task_Interface.class);
                        intent.putExtra("selectedTaskName", selectedTaskName);
                        startActivity(intent);
                    }
                });

                taskContainer.addView(taskCard);

                cardViewCount++;
            }
            taskPendingCountTextView.setText(String.valueOf(cardViewCount));
        }
    }

    private String getDueDateForTask(String taskName) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {MyDatabaseHelper.COLUMN_DUE_DATE};
        String selection = MyDatabaseHelper.COLUMN_TASK_NAME + " = ?";
        String[] selectionArgs = {taskName};

        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_TASKS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String dueDate = "";

        if (cursor.moveToFirst()) {
            dueDate = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_DUE_DATE));
        }

        cursor.close();
        db.close();

        return dueDate;
    }
}
