package com.example.taskmanager;

public class Task {

    public static final int REGULAR_ITEM_TYPE = 1;
    public static final int HEADER_ITEM_TYPE = 2;

    private long id;
    private String taskName;
    private String description;
    private String category;
    private String priority;
    private String dueDate;
    private int itemType; // Item type (regular or header)
    private boolean isHeader;
    private boolean areRecordsVisible;

    public static final String COLUMN_DATE_COMPLETED = "date_completed";
    public static final String COLUMN_TIME_COMPLETED = "time_completed";

    private String dateCompleted; // Date of completion
    private String timeCompleted; // Time of completion

    // Constructor
    public Task(int id, String taskName, String date, String time) {
        this.id = id;
        this.taskName = taskName;
        this.dateCompleted = date;
        this.timeCompleted = time;
        this.isHeader = false; // Set the value of isHeader
        this.areRecordsVisible = true; // Records are initially visible
    }

    public Task() {
        this.itemType = REGULAR_ITEM_TYPE;
    }

    // Getters and setters for each field

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(String timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean areRecordsVisible() {
        return areRecordsVisible;
    }

    public void setRecordsVisible(boolean visible) {
        this.areRecordsVisible = visible;
    }

    public Task(String date) {
        this.dateCompleted = date;
        this.isHeader = true;
        this.areRecordsVisible = true; // Records are initially visible
    }

}
