package com.example.EventLanka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private List<Integer> imageResIds; // list of drawable resource IDs, or URLs if loading from network

    public ImageSliderAdapter(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a simple layout containing an ImageView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_image, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // Bind the image resource
        holder.bind(imageResIds.get(position));
    }

    @Override
    public int getItemCount() {
        return imageResIds.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImageView);
        }

        void bind(int resId) {
            imageView.setImageResource(resId);
        }
    }
}

