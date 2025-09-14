package com.example.baira;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainPagerAdapter extends RecyclerView.Adapter<MainPagerAdapter.PageViewHolder> {
    private final int pageCount = 5;

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_main, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.textView.setText("Page " + (position + 1));
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public PageViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewPage);
        }
    }
} 