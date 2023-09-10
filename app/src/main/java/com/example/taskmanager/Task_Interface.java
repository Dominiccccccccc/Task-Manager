package com.example.taskmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Task_Interface extends AppCompatActivity {

    private MyDatabaseHelper databaseHelper;

    private Task task;
    private TextView taskNameTextView;
    private TextView dueDateTextView;
    private TextView descriptionTextView;
    private TextView categoryTextView;
    private TextView priorityTextView;
    private ImageView imageView;
    private LinearLayout subTaskLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setTitle("Task Viewing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new MyDatabaseHelper(this);

        taskNameTextView = findViewById(R.id.taskNameTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        priorityTextView = findViewById(R.id.priorityTextView);
        imageView = findViewById(R.id.imageView);
        subTaskLayout = findViewById(R.id.subTaskContainer); // LinearLayout for sub-task checkboxes

        String selectedTaskName = getIntent().getStringExtra("selectedTaskName");

        // Get task details from the database
        task = databaseHelper.getTaskByName(selectedTaskName);
        Task task = databaseHelper.getTaskByName(selectedTaskName);

        // Set task details to the TextViews
        taskNameTextView.setText(task.getTaskName());
        dueDateTextView.setText("Due Date: " + task.getDueDate());
        descriptionTextView.setText(task.getDescription());
        categoryTextView.setText(task.getCategory());
        priorityTextView.setText("Priority: " + task.getPriority());
        imageView.setImageResource(R.drawable.task_history);

        // Get the sub-task names
        List<String> subTaskNames = databaseHelper.getSubTaskNamesForTask(task.getId());

        for (String subTaskName : subTaskNames) {
            CheckBox subTaskCheckBox = new CheckBox(this);
            subTaskCheckBox.setText(subTaskName);
            subTaskCheckBox.setChecked(false);
            subTaskLayout.addView(subTaskCheckBox);
        }

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //
            }
        });

        Button completedButton = findViewById(R.id.completed_button);
        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Task_Interface.this);
                alertDialogBuilder.setTitle("Task Completion");
                alertDialogBuilder.setMessage("Have you completed this task?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Insert the task into the completed tasks table
                        long insertedRowId = databaseHelper.insertCompletedTask(task);

                        if (insertedRowId != -1) {
                            // delete the task from the main tasks table
                            boolean taskDeleted = databaseHelper.deleteTask(task.getId());

                            if (taskDeleted) {
                                Toast.makeText(Task_Interface.this, "Task completed and moved to completed tasks", Toast.LENGTH_SHORT).show();
                                finish(); // Go back to the main menu
                            } else {
                                Toast.makeText(Task_Interface.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Task_Interface.this, "Failed to complete task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
            additionalIcon.setImageResource(R.drawable.ic_edit);

            additionalIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Task_Interface.this, EditTaskActivity.class);
                    startActivity(intent);
                }
            });

            iconLayout.addView(additionalIcon);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;

            actionBar.setCustomView(iconLayout, layoutParams);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_task) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Delete Task");
            alertDialogBuilder.setMessage("Are you sure you want to delete this task?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete the task from the database
                    boolean taskDeleted = databaseHelper.deleteTask(task.getId());

                    if (taskDeleted) {
                        Toast.makeText(Task_Interface.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to main menu
                    } else {
                        Toast.makeText(Task_Interface.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();

        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        long taskId = myDB.getTaskId();

        // Update the UI with the latest data
        TextView taskNameTextView =findViewById(R.id.taskNameTextView);
        TextView descriptionTextView =findViewById(R.id.descriptionTextView);
        TextView categoryTextView =findViewById(R.id.categoryTextView);
        TextView priorityTextView =findViewById(R.id.priorityTextView);
        TextView dueDateTextView = findViewById(R.id.dueDateTextView);
        String taskName = myDB.getTaskName();
        String description = myDB.getDescription();
        String due_date = myDB.getTaskDueDate(taskId);
        String priority = myDB.getTaskPriority(taskId);
        String taskCategory = myDB.getTaskCategory(taskId);

        // Set task details to the TextViews
        taskNameTextView.setText(taskName);
        dueDateTextView.setText("Due Date: " + due_date);
        descriptionTextView.setText(description);
        categoryTextView.setText(taskCategory);
        priorityTextView.setText("Priority: " + priority);
    }
}
