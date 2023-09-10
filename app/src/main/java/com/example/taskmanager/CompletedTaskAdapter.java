package com.example.taskmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TaskHistoryItem> items;

    public CompletedTaskAdapter(List<TaskHistoryItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TaskHistoryItem.TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
            return new CompletedTaskViewHolder(view);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskHistoryItem item = items.get(position);

        if (item.getType() == TaskHistoryItem.TYPE_DATE_HEADER) {
            DateHeaderViewHolder dateHeaderViewHolder = (DateHeaderViewHolder) holder;
            dateHeaderViewHolder.dateTextView.setText(item.getDate());
        } else {
            CompletedTaskViewHolder taskViewHolder = (CompletedTaskViewHolder) holder;
            taskViewHolder.taskNameTextView.setText(item.getTask().getTaskName());
            taskViewHolder.dateCompletedTextView.setText(item.getTask().getDateCompleted());
            taskViewHolder.timeCompletedTextView.setText(item.getTask().getTimeCompleted());

            taskViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateHeaderViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.textViewDateHeader);
        }
    }

    public static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        TextView dateCompletedTextView;
        TextView timeCompletedTextView;
        ImageView deleteButton;

        public CompletedTaskViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            dateCompletedTextView = itemView.findViewById(R.id.dateCompletedTextView);
            timeCompletedTextView = itemView.findViewById(R.id.timeCompletedTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}