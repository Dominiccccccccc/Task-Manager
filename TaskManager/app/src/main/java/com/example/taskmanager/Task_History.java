package com.example.taskmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Comparator;

public class Task_History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskHistoryAdapter adapter;
    private ArrayList<Task> completedTasksList;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        setTitle("Task History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        completedTasksList = new ArrayList<>();
        adapter = new TaskHistoryAdapter(this, completedTasksList);
        recyclerView.setAdapter(adapter);

        loadDataFromDatabase();

        adapter.setOnDeleteButtonClickListener(new TaskHistoryAdapter.OnDeleteButtonClickListener() {
            @Override
            public void onDeleteButtonClick(int position) {
                showConfirmationDialog(position);
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

    private void showConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.deleteItem(position); // Delete task from database and list
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private int findHeaderPosition(String selectedDate) {
        for (int i = 0; i < completedTasksList.size(); i++) {
            Task item = completedTasksList.get(i);
            if (item.isHeader() && item.getDateCompleted().equals(selectedDate)) {
                return i;
            }
        }
        return -1;
    }

    private void loadDataFromDatabase() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        Cursor cursor = dbHelper.readAllData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No records found.", Toast.LENGTH_SHORT).show();
        } else {
            int idIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID);
            int taskNameIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TASK_NAME);
            int dateIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DATE_COMPLETED);
            int timeIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME_COMPLETED);

            ArrayList<Task> tempList = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String taskName = cursor.getString(taskNameIndex);
                String date = cursor.getString(dateIndex);
                String time = cursor.getString(timeIndex);

                Task billRecord = new Task(id, taskName, date, time);
                tempList.add(billRecord);
            }

            completedTasksList.clear();
            String currentDate = null;

            Collections.sort(tempList, new Comparator<Task>() {
                DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                @Override
                public int compare(Task o1, Task o2) {
                    try {
                        Date dateTime1 = dateTimeFormat.parse(o1.getDateCompleted() + " " + o1.getTimeCompleted());
                        Date dateTime2 = dateTimeFormat.parse(o2.getDateCompleted() + " " + o2.getTimeCompleted());
                        return dateTime2.compareTo(dateTime1); // arrange the date in descending order
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            for (Task task : tempList) {
                String date = task.getDateCompleted();

                if (!date.equals(currentDate)) {
                    currentDate = date;
                    Task headerItem = new Task(date);
                    completedTasksList.add(headerItem);
                }

                task.setRecordsVisible(true);
                tempList.add(task);
            }

            //customAdapter.notifyDataSetChanged();
        }

        cursor.close();
    }
}