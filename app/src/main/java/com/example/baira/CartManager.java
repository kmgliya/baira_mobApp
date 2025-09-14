package com.example.baira;

import android.content.Context;
import com.example.baira.models.Product;
import com.example.baira.adapters.CartAdapter;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<CartAdapter.Pair<Product, Integer>> cartItems = new ArrayList<>();

    public static void addToCart(Product product) {
        for (CartAdapter.Pair<Product, Integer> pair : cartItems) {
            if (pair.first.getId().equals(product.getId())) {
                pair.second++;
                return;
            }
        }
        cartItems.add(new CartAdapter.Pair<>(product, 1));
    }

    public static void removeFromCart(Product product) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartAdapter.Pair<Product, Integer> pair = cartItems.get(i);
            if (pair.first.getId().equals(product.getId())) {
                if (pair.second > 1) {
                    pair.second--;
                } else {
                    cartItems.remove(i);
                }
                return;
            }
        }
    }

    public static List<CartAdapter.Pair<Product, Integer>> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public static void saveCart(Context context) {
        // Можно реализовать сохранение в SharedPreferences, если нужно
    }

    public static void restoreCart(Context context) {
        // Можно реализовать восстановление из SharedPreferences, если нужно
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static double getTotalPrice() {
        double total = 0.0;
        for (CartAdapter.Pair<Product, Integer> pair : cartItems) {
            total += pair.first.getPrice() * pair.second;
        }
        return total;
    }
}