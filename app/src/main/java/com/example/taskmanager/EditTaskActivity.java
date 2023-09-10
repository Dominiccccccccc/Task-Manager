package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private Button dueDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        setTitle("Edit Task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        long taskId = myDB.getTaskId();

        EditText taskEditText = findViewById(R.id.task_EditText);
        EditText descriptionEditText = findViewById(R.id.description_editText);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        dueDateButton = findViewById(R.id.due_date_button);

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        String taskName = myDB.getTaskName();
        String descriptionHint = myDB.getDescription();

        taskEditText.setHint(taskName);
        descriptionEditText.setHint(descriptionHint);

        String taskCategory = myDB.getTaskCategory(taskId);
        String[] categoryValues = getResources().getStringArray(R.array.category);

        int categoryPosition = -1;
        for (int i = 0; i < categoryValues.length; i++) {
            if (taskCategory.equals(categoryValues[i])) {
                categoryPosition = i;
                break;
            }
        }
        if (categoryPosition != -1) {
            categorySpinner.setSelection(categoryPosition);
        }

        String priority = myDB.getTaskPriority(taskId);
        String[] priorityLevel = getResources().getStringArray(R.array.priority);

        int priorityPosition = -1;
        for (int i = 0; i < priorityLevel.length; i++) {
            if (priorityLevel[i].equals(priority)) {
                priorityPosition = i;
                break;
            }
        }
        if (priorityPosition != -1) {
            prioritySpinner.setSelection(priorityPosition);
        }

        String taskDueDate = myDB.getTaskDueDate(taskId);
        dueDateButton.setText(taskDueDate);

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle("Confirm Update");
                builder.setMessage("Are you sure you want to update your task?");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = taskEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        String category = categorySpinner.getSelectedItem().toString();
                        String priority = prioritySpinner.getSelectedItem().toString();
                        String dueDate = dueDateButton.getText().toString();

                        ContentValues values = new ContentValues();
                        values.put(MyDatabaseHelper.COLUMN_TASK_NAME, taskName);
                        values.put(MyDatabaseHelper.COLUMN_DESCRIPTION, description);
                        values.put(MyDatabaseHelper.COLUMN_CATEGORY, category);
                        values.put(MyDatabaseHelper.COLUMN_PRIORITY, priority);
                        values.put(MyDatabaseHelper.COLUMN_DUE_DATE, dueDate);

                        SQLiteDatabase db = myDB.getWritableDatabase();
                        long currentTaskId = myDB.getTaskId(); // Retrieve the current task ID

                        // Update the task using the task ID
                        int rowsUpdated = db.update(MyDatabaseHelper.TABLE_TASKS, values, "_id = ?", new String[]{String.valueOf(currentTaskId)});
                        db.close();

                        if (rowsUpdated > 0) {
                            Toast.makeText(EditTaskActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(EditTaskActivity.this, "Error updating task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the button text with the selected date
                        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        dueDateButton.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth
        );

        // Set minimum date to prevent selecting dates before the current system date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}