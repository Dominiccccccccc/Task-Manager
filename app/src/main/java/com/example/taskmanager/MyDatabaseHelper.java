package com.example.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DUE_DATE = "due_date";

    private static final String TABLE_SUBTASKS = "subtasks";
    private static final String COLUMN_SUBTASK_ID = "_id";
    private static final String COLUMN_MAIN_TASK_ID = "main_task_id";
    private static final String COLUMN_SUBTASK_NAME = "subtask_name";

    public static final String TABLE_COMPLETED_TASKS = "completed_tasks";
    public static final String COLUMN_DATE_COMPLETED = "date_completed";
    public static final String COLUMN_TIME_COMPLETED = "time_completed";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Task table
        String createTableQuery = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT)";
        db.execSQL(createTableQuery);

        // Sub-task table
        String createSubtasksTableQuery = "CREATE TABLE " + TABLE_SUBTASKS + " (" +
                COLUMN_SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MAIN_TASK_ID + " INTEGER, " +
                COLUMN_SUBTASK_NAME + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_MAIN_TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + COLUMN_ID + "))";

        db.execSQL(createSubtasksTableQuery);

        // Completed task table
        String createCompletedTasksTableQuery = "CREATE TABLE " + TABLE_COMPLETED_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT, " +
                COLUMN_DATE_COMPLETED + " TEXT, " +
                COLUMN_TIME_COMPLETED + " TEXT)";
        db.execSQL(createCompletedTasksTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    long getTaskId() {
        SQLiteDatabase db = this.getReadableDatabase();
        long taskId = -1;
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_ID}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_ID);
                if (columnIndex != -1) {
                    taskId = cursor.getLong(columnIndex);
                }
            }
            cursor.close();
        }
        return taskId;
    }

    public String getTaskName() {
        SQLiteDatabase db = this.getReadableDatabase();
        String taskName = "";
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_TASK_NAME}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_TASK_NAME);
                if (columnIndex != -1) {
                    taskName = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        return taskName;
    }

    public String getDescription() {
        SQLiteDatabase db = this.getReadableDatabase();
        String descriptionHint = "";
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_DESCRIPTION}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                if (columnIndex != -1) {
                    descriptionHint = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        return descriptionHint;
    }

    public String getTaskCategory(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String category = "";

        String[] columns = {COLUMN_CATEGORY};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                if (columnIndex != -1) {
                    category = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return category;
    }

    public String getTaskPriority(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String priority = "";

        String[] columns = {COLUMN_PRIORITY};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
                if (columnIndex != -1) {
                    priority = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return priority;
    }

    public String getTaskDueDate(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dueDate = "";

        String[] columns = {COLUMN_DUE_DATE};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
                if (columnIndex != -1) {
                    dueDate = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return dueDate;
    }

    public long insertTask(String taskName, String description, String category, String priority, String dueDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PRIORITY, priority);
        values.put(COLUMN_DUE_DATE, dueDate);
        return db.insert(TABLE_TASKS, null, values);
    }

    public long insertSubtask(long mainTaskId, String subtaskName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAIN_TASK_ID, mainTaskId);
        values.put(COLUMN_SUBTASK_NAME, subtaskName);
        long insertedRowId = db.insert(TABLE_SUBTASKS, null, values);
        Log.d("DatabaseInsertion", "Inserted row ID: " + insertedRowId);
        return insertedRowId;
    }

    public List<String> getAllTaskNames() {
        List<String> taskNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_TASKS,
                new String[] {COLUMN_TASK_NAME},
                null,
                null,
                null,
                null,
                null
        );

        // After retrieving tasks from the database
        if (cursor != null && cursor.moveToFirst()) {
            int taskNameIndex = cursor.getColumnIndex(COLUMN_TASK_NAME);
            if (taskNameIndex != -1) {
                do {
                    String taskName = cursor.getString(taskNameIndex);
                    taskNames.add(taskName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return taskNames;
    }

    public Task getTaskByName(String taskName) {
        Task task = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_TASKS,
                null,
                COLUMN_TASK_NAME + " = ?",
                new String[]{taskName},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            task = new Task();

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex != -1) {
                task.setId(cursor.getLong(idIndex));
            }

            int taskNameIndex = cursor.getColumnIndex(COLUMN_TASK_NAME);
            if (taskNameIndex != -1) {
                task.setTaskName(cursor.getString(taskNameIndex));
            }

            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            if (descriptionIndex != -1) {
                task.setDescription(cursor.getString(descriptionIndex));
            }

            int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
            if (categoryIndex != -1) {
                task.setCategory(cursor.getString(categoryIndex));
            }

            int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
            if (priorityIndex != -1) {
                task.setPriority(cursor.getString(priorityIndex));
            }

            int dueDateIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
            if (dueDateIndex != -1) {
                task.setDueDate(cursor.getString(dueDateIndex));
            }

            cursor.close();
        }

        return task;
    }

    public List<String> getSubTaskNamesForTask(long taskId) {
        List<String> subTaskNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_SUBTASKS,
                new String[]{COLUMN_SUBTASK_NAME},
                COLUMN_MAIN_TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int subTaskNameIndex = cursor.getColumnIndex(COLUMN_SUBTASK_NAME);
            if (subTaskNameIndex != -1) {
                do {
                    String subTaskName = cursor.getString(subTaskNameIndex);
                    subTaskNames.add(subTaskName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return subTaskNames;
    }

    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        return rowsAffected > 0;
    }

    public long insertCompletedTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, task.getTaskName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_DUE_DATE, task.getDueDate());

        // Get the current date and time
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HHmm", Locale.getDefault()); // Change format here
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        values.put(COLUMN_DATE_COMPLETED, currentDate);
        values.put(COLUMN_TIME_COMPLETED, currentTime);

        long insertedRowId = db.insert(TABLE_COMPLETED_TASKS, null, values);
        db.close();

        return insertedRowId;
    }

    public int getCompletedTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_COMPLETED_TASKS;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public List<Task> searchTasks(String query) {
        List<Task> searchResults = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_TASK_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = db.query(
                TABLE_TASKS,
                null,            // Retrieve all columns
                selection,       // WHERE clause
                selectionArgs,   // Selection arguments
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                task.setTaskName(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                task.setPriority(cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY)));
                task.setDueDate(cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE)));

                searchResults.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return searchResults;
    }


}
