package com.rorschach.zcyislim.Pager.FoodInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.AbsoluteCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;

import java.util.ArrayList;

public class FoodRecommendAdapter extends RecyclerView.Adapter<FoodRecommendAdapter.FoodRecommendViewHolder> {

    ArrayList<Food> list;
    Context context;
    ItemClickListen itemClickListen;

    public FoodRecommendAdapter(ArrayList<Food> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }

    @NonNull
    @Override
    public FoodRecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommend, parent, false);
        context = parent.getContext();
        return new FoodRecommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodRecommendViewHolder holder, int position) {
        holder.foodName.setText(list.get(position).getFoodName());
        holder.foodCalories.setText(context.getResources().getString(R.string.recom_unit_with_str, list.get(position).getFoodCalories()));
        holder.foodImage.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(new AbsoluteCornerSize(25f)).build());
        Glide.with(context).load(Constant.foodUrl + list.get(position).getFoodTypeId() + "/" + list.get(position).getFoodImage()).centerCrop().into(holder.foodImage);
        holder.box.setOnClickListener(view -> itemClickListen.itemClickListen(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearList() {
        this.list.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadMoreData(ArrayList<Food> more) {
        list.addAll(more);
        notifyDataSetChanged();
    }

    public static class FoodRecommendViewHolder extends RecyclerView.ViewHolder {
        LinearLayout box;
        ShapeableImageView foodImage;
        TextView foodName;
        TextView foodCalories;

        public FoodRecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.recommend_box);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodCalories = itemView.findViewById(R.id.food_calories);
        }
    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }

}
