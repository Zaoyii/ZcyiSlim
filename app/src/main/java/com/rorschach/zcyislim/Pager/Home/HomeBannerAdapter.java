package com.rorschach.zcyislim.Pager.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.android.material.carousel.MaskableFrameLayout;
import com.rorschach.zcyislim.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeBannerAdapter extends RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder> {
    ArrayList<String> list;
    Context context;
    ItemClickListen itemClickListen;

    public HomeBannerAdapter(ArrayList<String> list, Context context, ItemClickListen itemClickListen) {
        this.list = list;
        this.context = context;
        this.itemClickListen = itemClickListen;
    }

    @NonNull
    @Override
    public HomeBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel, parent, false);
        context = parent.getContext();
        return new HomeBannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBannerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position)).centerCrop().into(holder.imageView);
        holder.maskableFrameLayout.setOnClickListener(view -> itemClickListen.itemClickListen(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HomeBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaskableFrameLayout maskableFrameLayout;

        public HomeBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_view);
            maskableFrameLayout = itemView.findViewById(R.id.carousel_item_container);
        }
    }

    public interface ItemClickListen {
        void itemClickListen(int position);
    }
}
