package com.rorschach.zcyislim.Pager.Exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.R;

import java.util.ArrayList;
import java.util.Locale;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.RunningExerciseViewHolder> {
    ArrayList<Exercise> list;
    Context context;
    ItemClickListen itemClickListen;

    public ExerciseAdapter(ArrayList<Exercise> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }

    @NonNull
    @Override
    public RunningExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        context = parent.getContext();
        return new RunningExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunningExerciseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.box.setOnClickListener(view -> itemClickListen.itemClickListen(position));
        String type;
        switch (list.get(position).getExerciseType()) {
            case 0:
                type = "跑步";
                break;
            case 1:
                type = "散步";
                break;
            case 2:
                type = "骑行";
                break;
            default:
                type = "未知";
                break;
        }

        String showDistance = String.format(Locale.CHINA, "%.2f", list.get(position).getExerciseDistance() * 0.001) + "公里";
        String showCalories = String.format(Locale.CHINA, "%.2f", list.get(position).getExerciseCalories()) + "卡";
        String startTime = list.get(position).getStartTime();
        String[] s = startTime.split(" ");

        holder.exercise_type.setText(type);
        holder.exercise_distance.setText(showDistance);
        holder.exercise_calories.setText(showCalories);
        holder.exercise_date.setText(s[0].substring(5));
        holder.exercise_time.setText(s[1]);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<Exercise> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class RunningExerciseViewHolder extends RecyclerView.ViewHolder {
        LinearLayout box;
        TextView exercise_date;
        TextView exercise_time;
        TextView exercise_type;
        TextView exercise_distance;
        TextView exercise_calories;

        public RunningExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.exercise_box);
            exercise_date = itemView.findViewById(R.id.exercise_date);
            exercise_time = itemView.findViewById(R.id.exercise_time);
            exercise_distance = itemView.findViewById(R.id.exercise_distance);
            exercise_type = itemView.findViewById(R.id.exercise_type);
            exercise_calories = itemView.findViewById(R.id.exercise_calories);
        }
    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }
}
