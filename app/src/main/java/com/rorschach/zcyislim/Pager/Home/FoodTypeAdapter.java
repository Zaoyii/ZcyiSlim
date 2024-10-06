package com.rorschach.zcyislim.Pager.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.AbsoluteCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.rorschach.zcyislim.Entity.FoodType;
import com.rorschach.zcyislim.R;
import com.rorschach.zcyislim.Util.Constant;

import java.util.ArrayList;

public class FoodTypeAdapter extends RecyclerView.Adapter<FoodTypeAdapter.FoodTypeViewHolder> {

    ArrayList<FoodType> list;
    Context context;
    ItemClickListen itemClickListen;
    private int selectedIndex;

    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    public FoodTypeAdapter(ArrayList<FoodType> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }


    @NonNull
    @Override
    public FoodTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_type, parent, false);
        context = parent.getContext();
        return new FoodTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodTypeViewHolder holder, int position) {
        holder.foodTypeName.setText(list.get(position).getFoodTypeName());
        holder.foodTypeImage.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(new AbsoluteCornerSize(25f)).build());
        Glide.with(context).load(Constant.foodTypeUrl + list.get(position).getFoodTypeId() + "/" + list.get(position).getFoodTypeImg()).centerCrop().into(holder.foodTypeImage);
        holder.box.setOnClickListener(view -> itemClickListen.itemClickListen(position));
        if (selectedIndex == position) {
            holder.foodTypeName.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.foodTypeName.setTextColor(context.getResources().getColor(R.color.food_type_uncheck));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class FoodTypeViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView box;
        ShapeableImageView foodTypeImage;
        TextView foodTypeName;

        public FoodTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.recommend_box);
            foodTypeImage = itemView.findViewById(R.id.food_type_image);
            foodTypeName = itemView.findViewById(R.id.food_type_name);
        }

    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }
}
