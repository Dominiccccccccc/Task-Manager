package com.example.taskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Task_Search extends AppCompatActivity {

    private MyDatabaseHelper databaseHelper;
    private EditText searchEditText;
    private Button searchButton;
    private TextView searchResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_task);

        setTitle("Search Tasks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new MyDatabaseHelper(this);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsTextView = findViewById(R.id.searchResultsTextView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTasks();
            }
        });
    }

    private void searchTasks() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            List<Task> searchResults = databaseHelper.searchTasks(query);

            if (searchResults.isEmpty()) {
                searchResultsTextView.setText("No matching tasks found.");
            } else {
                StringBuilder resultText = new StringBuilder("Search results:\n\n");
                for (Task task : searchResults) {
                    resultText.append("Name: ").append(task.getTaskName()).append("\n");
                    resultText.append("ID: ").append(task.getId()).append("\n");
                    resultText.append("Category: ").append(task.getCategory()).append("\n");
                    resultText.append("Description: ").append(task.getDescription()).append("\n");
                    resultText.append("Due Date: ").append(task.getDueDate()).append("\n");
                    resultText.append("Priority: ").append(task.getPriority()).append("\n\n");

                }
                searchResultsTextView.setText(resultText.toString());
            }
        } else {
            searchResultsTextView.setText("Please enter a search query.");
        }
    }
}