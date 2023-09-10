package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_RECORD = 1;
    private Context context;
    private ArrayList<Task> completedTasksList;

    public TaskHistoryAdapter(Context context, ArrayList<Task> completedTasksList) {
        this.context = context;
        this.completedTasksList = completedTasksList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_header_layout, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
            return new RecordViewHolder(recordView);
        }
    }

    public void deleteItem(int position) {
        if (position >= 0 && position < completedTasksList.size()) {
            Task deletedItem = completedTasksList.get(position);
            completedTasksList.remove(position);
            notifyItemRemoved(position);

            // Delete the record from the database
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
            dbHelper.deleteTask(deletedItem.getId());

            // Update the positions of remaining items
            notifyItemRangeChanged(position, completedTasksList.size() - position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        RecordViewHolder recordViewHolder = (RecordViewHolder) holder;
        Task item = completedTasksList.get(position);


            recordViewHolder.taskNameTextView.setText(item.getTaskName());
            recordViewHolder.dateTextView.setText(item.getDateCompleted());
            recordViewHolder.timeTextView.setText(item.getTimeCompleted());

            recordViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteButtonClickListener != null) {
                        deleteButtonClickListener.onDeleteButtonClick(position);
                    }
                }
            });
        }

    @Override
    public int getItemCount() {
        return completedTasksList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return completedTasksList.get(position).getItemType(); // Use getItemType
    }

    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener listener) {
        this.deleteButtonClickListener = listener;
    }

    private OnDeleteButtonClickListener deleteButtonClickListener;

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick(int position);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateHeader = itemView.findViewById(R.id.textViewDateHeader);
        }
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        ImageView deleteButton;
        TextView dateTextView;
        TextView timeTextView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.TaskName_tv);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            timeTextView = itemView.findViewById(R.id.textViewTime);
        }
    }
}