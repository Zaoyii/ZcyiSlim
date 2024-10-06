package com.rorschach.zcyislim.Pager.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.rorschach.zcyislim.Entity.TaskAssignment;
import com.rorschach.zcyislim.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeTaskAdapter extends RecyclerView.Adapter<HomeTaskAdapter.HomeTaskViewHolder> {
    ArrayList<TaskAssignment> list;
    Context context;
    ItemClickListen itemClickListen;

    public HomeTaskAdapter(ArrayList<TaskAssignment> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }

    @NonNull
    @Override
    public HomeTaskAdapter.HomeTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        context = parent.getContext();
        return new HomeTaskViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull HomeTaskAdapter.HomeTaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskAssignment taskAssignment = list.get(position);
        String taskNameTime = context.getString(R.string.task_name_time, taskAssignment.getTaskName(), taskAssignment.getTaskTime() + "");
        holder.taskName.setText(taskNameTime);
        holder.taskTimes.setText(context.getString(R.string.task_times, taskAssignment.getActualCompletionCount() + "", taskAssignment.getExpectedCompletionCount() + ""));
        if (taskAssignment.getActualCompletionCount() != 0) {
            double i = (double) taskAssignment.getActualCompletionCount() / (double) taskAssignment.getExpectedCompletionCount() * 100;
            holder.linearIndicator.setProgress((int) i);
        }
        if (taskAssignment.getTaskExample() != null && taskAssignment.getTaskExample().length() > 0) {
            String s = "如" + taskAssignment.getTaskExample();
            holder.taskExample.setText(s);
            holder.taskExample.setVisibility(View.VISIBLE);
        }
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        String assignmentUpdateTime = taskAssignment.getAssignmentUpdateTime();

        if (!today.equals(assignmentUpdateTime.substring(0, 10)) || taskAssignment.getActualCompletionCount() == 0) {
            holder.done.setVisibility(View.VISIBLE);
            holder.done.setOnClickListener(view -> itemClickListen.itemClickListen(position));
        } else {
            holder.done.setVisibility(View.GONE);
            holder.doneImg.setImageResource(R.drawable.is_done);
        }
    }
    public void updateProgress(ArrayList<TaskAssignment> list, int position) {
        this.list = list;
        notifyItemChanged(position);
    }

    public void updateList(ArrayList<TaskAssignment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class HomeTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskExample;
        TextView taskTimes;
        MaterialCardView done;
        ImageView doneImg;
        LinearProgressIndicator linearIndicator;

        public HomeTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            taskExample = itemView.findViewById(R.id.task_example);
            taskTimes = itemView.findViewById(R.id.task_times);
            done = itemView.findViewById(R.id.done);
            doneImg = itemView.findViewById(R.id.done_img);
            linearIndicator = itemView.findViewById(R.id.linear_indicator);
        }
    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }
}
