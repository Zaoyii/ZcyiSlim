package com.rorschach.zcyislim.Pager.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;

import java.util.ArrayList;

public class HomeFoodAdapter extends RecyclerView.Adapter<HomeFoodAdapter.HomeFoodViewHolder> {
    ArrayList<Food> list;
    Context context;
    ItemClickListen itemClickListen;

    public HomeFoodAdapter(ArrayList<Food> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }

    @NonNull
    @Override
    public HomeFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        context = parent.getContext();
        return new HomeFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFoodViewHolder holder, int position) {
        Glide.with(context).load(Constant.foodUrl + list.get(position).getFoodTypeId() + "/" + list.get(position).getFoodImage()).centerCrop().into(holder.foodImage);
        holder.foodName.setText(list.get(position).getFoodName());
        holder.foodCalories.setText(list.get(position).getFoodCalories());
        holder.box.setOnClickListener(view -> itemClickListen.itemClickListen(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<Food> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public static class HomeFoodViewHolder extends RecyclerView.ViewHolder {
        CardView box;
        ShapeableImageView foodImage;
        TextView foodName;
        TextView foodCalories;

        public HomeFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.item_card);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodCalories = itemView.findViewById(R.id.food_calories);
        }
    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }
}
