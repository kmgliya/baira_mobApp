package com.example.baira.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.baira.R;
import com.example.baira.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> categories;
    private final OnCategoryClickListener onCategoryClick;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener onCategoryClick) {
        this.categories = categories;
        this.onCategoryClick = onCategoryClick;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        Glide.with(holder.itemView.getContext())
            .load(category.getImageUrl())
            .placeholder(R.drawable.placeholder_category)
            .error(R.drawable.error_category)
            .centerCrop()
            .into(holder.imageView);
        holder.nameText.setText(category.getName());
        holder.itemView.setOnClickListener(v -> onCategoryClick.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public final TextView nameText;
        public CategoryViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.categoryImage);
            nameText = view.findViewById(R.id.categoryName);
        }
    }
} 