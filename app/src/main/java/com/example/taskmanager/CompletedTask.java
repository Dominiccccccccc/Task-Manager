package com.example.taskmanager;

public class CompletedTask {
    private String taskName;
    private String dateCompleted;
    private String timeCompleted;
    private String category;
    private boolean isSelected;

    public CompletedTask(String taskName, String dateCompleted, String timeCompleted, String category) {
        this.taskName = taskName;
        this.dateCompleted = dateCompleted;
        this.timeCompleted = timeCompleted;
        this.category = category;
        this.isSelected = false;
    }

    // Getters and setters
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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
}