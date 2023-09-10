package com.example.taskmanager;

public class TaskHistoryItem {
    public static final int TYPE_DATE_HEADER = 0;
    public static final int TYPE_COMPLETED_TASK = 1;

    private int type;
    private String date;
    private CompletedTask task;

    public TaskHistoryItem(int type, String date) {
        this.type = type;
        this.date = date;
    }

    public TaskHistoryItem(int type, CompletedTask task) {
        this.type = type;
        this.task = task;
    }

    public int getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public CompletedTask getTask() {
        return task;
    }
}
