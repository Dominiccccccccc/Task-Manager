package com.example.taskmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Task_Create extends AppCompatActivity {

    private MyDatabaseHelper databaseHelper;
    private Button dueDateButton;
    private List<EditText> subtaskEditTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        setTitle("Create Task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new MyDatabaseHelper(this);

        dueDateButton = findViewById(R.id.due_date_button);
        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        ImageView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
                subtaskEditTexts.clear(); // Clear subtaskEditTexts
                finish(); // Finish this activity to return to MainActivity
            }
        });

        findViewById(R.id.subTask_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubtaskEditText();
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

    private void saveDataToDatabase() {
        // Retrieve user input from UI elements

        String taskName = ((EditText) findViewById(R.id.task_name_edit)).getText().toString();
        String description = ((EditText) findViewById(R.id.description_edit)).getText().toString();
        String category = ((Spinner) findViewById(R.id.categorySpinner)).getSelectedItem().toString();
        String priority = ((Spinner) findViewById(R.id.categorySpinner2)).getSelectedItem().toString();
        String dueDate = ((Button) findViewById(R.id.due_date_button)).getText().toString();

        // Insert main task into the tasks table
        long mainTaskId = databaseHelper.insertTask(taskName, description, category, priority, dueDate);

        // Insert subtasks associated with the main task
        for (EditText subtaskEditText : subtaskEditTexts) {
            String subtask = subtaskEditText.getText().toString().trim();
            Log.d("SubtaskContent", "Subtask: " + subtask);
            if (!subtask.isEmpty()) {
                databaseHelper.insertSubtask(mainTaskId, subtask);
            }
        }

        saveDataToFirebase(mainTaskId, taskName, description, category, priority, dueDate);

        // Perform any additional actions or navigation here
        // show confirmation message
        Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();

    }

    private void saveDataToFirebase(long taskId, String taskName, String description, String category, String priority, String dueDate) {
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");

        // Create a Task object with the retrieved data
        Task task = new Task();
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setCategory(category);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        // Store the task in the Firebase database under a unique key
        tasksRef.child(String.valueOf(taskId)).setValue(task);
    }


    private void addSubtaskEditText() {
        LinearLayout subtaskContainerLayout = findViewById(R.id.subtaskContainerLayout);

        // Create a new CheckBox for subtask
        CheckBox subtaskCheckBox = new CheckBox(this);
        subtaskCheckBox.setText("Sub-Task");
        subtaskContainerLayout.addView(subtaskCheckBox);

        // Create a new EditText for subtask
        EditText subtaskEditText = new EditText(this);
        subtaskEditText.setHint("Input the Sub-task");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.edit_text_width), // Width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height
        );
        subtaskEditText.setLayoutParams(layoutParams);
        subtaskContainerLayout.addView(subtaskEditText);

        // Keep track of the EditText
        subtaskEditTexts.add(subtaskEditText);
    }
}
