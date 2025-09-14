package com.example.baira;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.baira.adapters.ProductAdapter;
import com.example.baira.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatalogActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FloatingActionButton fabGoToCart;
    private TabLayout categoryTabs;

    private final List<String> categories = Arrays.asList(
            "Все", "Мужское", "Женское", "Обувь", "Чыптама", "Белдемче"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        recyclerView = findViewById(R.id.recyclerViewProducts);
        fabGoToCart = findViewById(R.id.fabGoToCart);
        categoryTabs = findViewById(R.id.categoryTabs);
        
        // Set up category tabs
        for (String category : categories) {
            categoryTabs.addTab(categoryTabs.newTab().setText(category));
        }

        // Set up product adapter
        adapter = new ProductAdapter(new ArrayList<>(), product -> {
            CartManager.addToCart(product);
            showAddToCartFeedback(product.getName());
        });
        
        // Set up recycler view
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Load initial products (all categories)
        showProductsForCategory("Все");

        // Set up tab selection listener
        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getText() != null) {
                    showProductsForCategory(tab.getText().toString());
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Set up cart button
        fabGoToCart.setOnClickListener(v -> {
            navigateToActivity(CartActivity.class);
        });
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_catalog;
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.catalog;
    }

    private void showProductsForCategory(String category) {
        List<Product> filteredProducts;
        if ("Все".equals(category)) {
            // Show all products
            filteredProducts = getAllProducts();
        } else {
            // Filter by category
            filteredProducts = generateMockProducts(category);
        }
        adapter.updateProducts(filteredProducts);
    }
    
    private List<Product> getAllProducts() {
        // In a real app, you would fetch this from a database or API
        List<Product> allProducts = new ArrayList<>();
        for (String category : categories) {
            if (!"Все".equals(category)) {
                allProducts.addAll(generateMockProducts(category));
            }
        }
        return allProducts;
    }

    private List<Product> generateMockProducts(String category) {
        List<Product> products = new ArrayList<>();
        switch (category) {
            case "Женское":
                products.add(new Product("w1", "Женский костюм BAIRA", "https://i.postimg.cc/T2JmdQpy/w1.jpg", 3500.0, "Женское"));
                products.add(new Product("w2", "Платье с орнаментом", "https://i.postimg.cc/3JrGdpvt/w2.jpg", 4200.0, "Женское"));
                products.add(new Product("w3", "Красный костюм BAIRA", "https://i.postimg.cc/Qx6T5tgh/w3.jpg", 3900.0, "Женское"));
                // Старые инста-ссылки
                products.add(new Product("w4", "Инста платье", "https://instagram.com/p/DHNi1C3sYrN/media/?size=l", 4800.0, "Женское"));
                products.add(new Product("w5", "Инста платье", "https://instagram.com/p/DHOwqMDsAca/media/?size=l", 4600.0, "Женское"));
                products.add(new Product("w6", "Универсальное", "https://instagram.com/p/DHWH-RcuvMW/media/?size=l", 5000.0, "Женское"));
                // Новые инста-ссылки (женские)
                products.add(new Product("w7", "Женский пиджак", "https://instagram.com/p/DHgRDljMCHr/media/?size=l", 5100.0, "Женское"));
                products.add(new Product("w8", "Платье", "https://instagram.com/p/DHsm5iossv3/media/?size=l", 4700.0, "Женское"));
                products.add(new Product("w9", "Этно комплект", "https://instagram.com/p/DHtF9spsslT/media/?size=l", 4800.0, "Женское"));
                products.add(new Product("w10", "Женская двойка", "https://instagram.com/p/DHutRBpthca/media/?size=l", 5200.0, "Женское"));
                break;
            case "Мужское":
                products.add(new Product("m1", "Мужской костюм BAIRA", "https://i.postimg.cc/sXFSXNYW/men1.jpg", 3100.0, "Мужское"));
                products.add(new Product("m2", "Чапан мужской", "https://i.postimg.cc/brpt0SFN/men2.jpg", 4700.0, "Мужское"));
                // Старые инста-ссылки
                products.add(new Product("m3", "Мужской этно пиджак", "https://instagram.com/p/DHa4PmIMbAt/media/?size=l", 5200.0, "Мужское"));
                products.add(new Product("m4", "Мужской комплект", "https://instagram.com/p/DHa5Fz4solM/media/?size=l", 5300.0, "Мужское"));
                // Новые инста-ссылки (мужские)
                products.add(new Product("m5", "Мужской BAIRA", "https://instagram.com/p/DHusbXMNQwQ/media/?size=l", 5400.0, "Мужское"));
                products.add(new Product("m6", "Мужской костюм", "https://instagram.com/p/DHuvhVcN384/media/?size=l", 5600.0, "Мужское"));
                products.add(new Product("m7", "БАЙРА пиджак", "https://instagram.com/p/DImOPcwsqAr/media/?size=l", 5800.0, "Мужское"));
                products.add(new Product("m8", "Этно комплект BAIRA", "https://instagram.com/p/DJGr9Pds5qe/media/?size=l", 6000.0, "Мужское"));
                break;
            // Add other categories as needed
            default:
                break;
        }
        return products;
    }


    private void showAddToCartFeedback(String productName) {
        // Show a message that the product was added to cart
        if (productName != null) {
            android.widget.Toast.makeText(
                this, 
                "\"" + productName + "\" добавлен в корзину", 
                android.widget.Toast.LENGTH_SHORT
            ).show();
        }
    }
} 