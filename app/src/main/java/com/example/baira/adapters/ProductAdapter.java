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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private final OnAddToCartClickListener onAddToCartClick;

    public interface OnAddToCartClickListener {
        void onAddToCart(Product product);
    }

    public ProductAdapter(List<Product> products, OnAddToCartClickListener onAddToCartClick) {
        this.products = products;
        this.onAddToCartClick = onAddToCartClick;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), onAddToCartClick);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
            btnAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }

        public void bind(Product product, OnAddToCartClickListener onAddToCartClick) {
            productName.setText(product.getName());
            productPrice.setText(String.format("% ,.2f ₽", product.getPrice()));
            String imageUrl = product.getImageUrl();
            if (imageUrl.startsWith("http")) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_product)
                        .into(productImage);
            } else {
                int resId = itemView.getContext().getResources().getIdentifier(imageUrl, "drawable", itemView.getContext().getPackageName());
                if (resId != 0) {
                    productImage.setImageResource(resId);
                } else {
                    productImage.setImageResource(R.drawable.placeholder_product);
                }
            }
            btnAddToCart.setOnClickListener(v -> onAddToCartClick.onAddToCart(product));
        }
    }
} 