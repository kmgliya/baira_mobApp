package com.example.baira.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.baira.R;
import com.example.baira.models.Product;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Pair<Product, Integer>> cartItems;
    private final OnRemoveClickListener onRemoveClick;

    public interface OnRemoveClickListener {
        void onRemove(Product product);
    }

    public CartAdapter(List<Pair<Product, Integer>> cartItems, OnRemoveClickListener onRemoveClick) {
        this.cartItems = cartItems;
        this.onRemoveClick = onRemoveClick;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position), onRemoveClick);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCart(List<Pair<Product, Integer>> newCart) {
        this.cartItems = newCart;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productCount;
        private final Button btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewCartProduct);
            productName = itemView.findViewById(R.id.textViewCartProductName);
            productPrice = itemView.findViewById(R.id.textViewCartProductPrice);
            productCount = itemView.findViewById(R.id.textViewCartProductCount);
            btnRemove = itemView.findViewById(R.id.buttonRemoveFromCart);
        }

        public void bind(Pair<Product, Integer> item, OnRemoveClickListener onRemoveClick) {
            Product product = item.first;
            int count = item.second;
            productName.setText(product.getName());
            productPrice.setText(String.format("% ,.2f ₽", product.getPrice()));
            productCount.setText("x" + count);
            Glide.with(itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_product)
                .into(productImage);
            btnRemove.setOnClickListener(v -> onRemoveClick.onRemove(product));
        }
    }

    // Simple Pair class for Java (since Kotlin's Pair is not available)
    public static class Pair<F, S> {
        public F first;
        public S second;
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
} 