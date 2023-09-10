package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Task_History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CompletedTaskAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private List<TaskHistoryItem> taskHistoryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        setTitle("Task History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new MyDatabaseHelper(this);

        taskHistoryItems = fetchDataFromDatabase();

        adapter = new CompletedTaskAdapter(taskHistoryItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initialize the PieChart
        PieChart pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        setupPieChart(pieChart, taskHistoryItems);

        adapter.setOnDeleteClickListener(new CompletedTaskAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Task_History.this);
                builder.setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the task from the database
                                TaskHistoryItem taskHistoryItem = taskHistoryItems.get(position);
                                if (taskHistoryItem.getType() == TaskHistoryItem.TYPE_COMPLETED_TASK) {
                                    CompletedTask completedTask = taskHistoryItem.getTask();
                                    deleteTaskFromDatabase(completedTask);
                                    taskHistoryItems.remove(position);
                                    adapter.notifyItemRemoved(position);
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showDatePicker(); // To show the date picker
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Convert the selected date to the header format
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                // Find the header position for the selected date
                scrollToHeader(selectedDate);
            }
        }, year, month, day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private int findHeaderPosition(String selectedDate) {
        for (int i = 0; i < taskHistoryItems.size(); i++) {
            TaskHistoryItem item = taskHistoryItems.get(i);
            if (item.getType() == TaskHistoryItem.TYPE_DATE_HEADER && item.getDate().trim().equals(selectedDate.trim())) {
                return i;
            }
        }
        return -1;
    }

    private void scrollToHeader(String selectedDate) {
        int headerPosition = findHeaderPosition(selectedDate);

        if (headerPosition != -1) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            int headerHeight = 10;
            int targetScrollPosition = headerPosition - 1;

            int targetScrollOffset = headerHeight;
            layoutManager.scrollToPositionWithOffset(targetScrollPosition, targetScrollOffset);
        } else {
            Toast.makeText(this, "No records found for selected date", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTaskFromDatabase(CompletedTask task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("completed_tasks", "task_name = ? AND date_completed = ? AND time_completed = ?",
                new String[]{task.getTaskName(), task.getDateCompleted(), task.getTimeCompleted()});
        db.close();
    }

    private List<TaskHistoryItem> fetchDataFromDatabase() {
        List<TaskHistoryItem> taskHistoryItems = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("completed_tasks", null, null, null, null, null, null);

        ArrayList<TaskHistoryItem> tempList = new ArrayList<>();
        String currentDate = null;
        String previousDate = null;

        while (cursor.moveToNext()) {
            int taskNameIndex = cursor.getColumnIndex("task_name");
            int dateCompletedIndex = cursor.getColumnIndex("date_completed");
            int timeCompletedIndex = cursor.getColumnIndex("time_completed");
            int categoryIndex = cursor.getColumnIndex("category");

            if (taskNameIndex >= 0 && dateCompletedIndex >= 0 && timeCompletedIndex >= 0 && categoryIndex >= 0) {
                String taskName = cursor.getString(taskNameIndex);
                String dateCompleted = cursor.getString(dateCompletedIndex);
                String timeCompleted = cursor.getString(timeCompletedIndex);
                String category = cursor.getString(categoryIndex);

                if (!dateCompleted.equals(previousDate)) {
                    taskHistoryItems.add(new TaskHistoryItem(TaskHistoryItem.TYPE_DATE_HEADER, dateCompleted));
                    previousDate = dateCompleted;
                }

                CompletedTask completedTask = new CompletedTask(taskName, dateCompleted, timeCompleted, category);
                taskHistoryItems.add(new TaskHistoryItem(TaskHistoryItem.TYPE_COMPLETED_TASK, completedTask));

                TaskHistoryItem taskHistoryItem = new TaskHistoryItem(TaskHistoryItem.TYPE_COMPLETED_TASK,
                        new CompletedTask(taskName, dateCompleted, timeCompleted, category));

                tempList.add(taskHistoryItem);
            }
        }

        cursor.close();
        db.close();

        Collections.sort(tempList, new Comparator<TaskHistoryItem>() {
            DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            @Override
            public int compare(TaskHistoryItem o1, TaskHistoryItem o2) {
                try {
                    Date dateTime1 = dateTimeFormat.parse(o1.getTask().getDateCompleted() + " " + o1.getTask().getTimeCompleted());
                    Date dateTime2 = dateTimeFormat.parse(o2.getTask().getDateCompleted() + " " + o2.getTask().getTimeCompleted());
                    return dateTime2.compareTo(dateTime1); // arrange the date in descending order
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        taskHistoryItems.clear();

        for (TaskHistoryItem taskHistoryItem : tempList) {
            String dateCompleted = taskHistoryItem.getTask().getDateCompleted();

            if (!dateCompleted.equals(currentDate)) {
                currentDate = dateCompleted;
                taskHistoryItems.add(new TaskHistoryItem(TaskHistoryItem.TYPE_DATE_HEADER, dateCompleted));
            }
            taskHistoryItems.add(taskHistoryItem);
        }
        return taskHistoryItems;
    }

    private void setupPieChart(PieChart pieChart, List<TaskHistoryItem> taskHistoryItems) {
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (TaskHistoryItem item : taskHistoryItems) {
            if (item.getType() == TaskHistoryItem.TYPE_COMPLETED_TASK) {
                CompletedTask completedTask = item.getTask();
                String category = completedTask.getCategory();
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category Name");

        // Customize colors to match your app's blue theme
        int[] pieColors = new int[]{
                Color.parseColor("#66BB6A"),  // Green
                Color.parseColor("#FFD54F"),  // Yellow
                Color.parseColor("#42A5F5"),  // Blue
                Color.parseColor("#E77878"),  // Light Green
                Color.parseColor("#C271DE"),  // Amber
                Color.parseColor("#64B5F6"),  // Light Blue
        };
        dataSet.setColors(pieColors);
        dataSet.setValueTextSize(10f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Task Categories");
        pieChart.invalidate(); // Refresh the chart
    }
}