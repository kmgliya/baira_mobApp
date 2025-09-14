package com.example.baira;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Base activity class to provide consistent navigation and UI elements
 * across all activities in the app.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigation;
    protected Toolbar toolbar;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        
        setupToolbar();
        setupBottomNavigation();
    }
    
    /**
     * Each subclass must provide its layout ID
     */
    protected abstract int getLayoutId();
    
    /**
     * Each subclass must provide its navigation selection ID
     */
    protected abstract int getNavigationMenuItemId();
    
    /**
     * Set up the toolbar with back button if needed
     */
    protected void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (!(this instanceof HomeActivity)) {
                // Show back button for activities that are not the home screen
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
            }
        }
    }
    
    /**
     * Set up the bottom navigation with proper selection
     */
    protected void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            // Select the appropriate item
            bottomNavigation.setSelectedItemId(getNavigationMenuItemId());
            
            // Set up the navigation handler
            bottomNavigation.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == getNavigationMenuItemId()) {
                    // Already on this screen
                    return true;
                }
                
                // Handle navigation
                Class<?> targetActivity = null;
                
                if (itemId == R.id.catalog) {
                    targetActivity = CatalogActivity.class;
                } else if (itemId == R.id.cart) {
                    targetActivity = CartActivity.class;
                } else if (itemId == R.id.about) {
                    targetActivity = AboutActivity.class;
                } else if (itemId == R.id.contact) {
                    targetActivity = ContactActivity.class;
                } else if (itemId == R.id.home) {
                    targetActivity = HomeActivity.class;
                }
                
                if (targetActivity != null) {
                    navigateToActivity(targetActivity);
                    return true;
                }
                
                return false;
            });
        }
    }
    
    /**
     * Helper method to set up navigation view with proper selection 
     * that can be used by activities with custom layouts
     */
    protected void setupBottomNavigationView(BottomNavigationView navigationView, int selectedItemId) {
        if (navigationView != null) {
            // Select the appropriate item
            navigationView.setSelectedItemId(selectedItemId);
            
            // Set up the navigation handler
            navigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == selectedItemId) {
                    // Already on this screen
                    return true;
                }
                
                // Handle navigation
                Class<?> targetActivity = null;
                
                if (itemId == R.id.catalog) {
                    targetActivity = CatalogActivity.class;
                } else if (itemId == R.id.cart) {
                    targetActivity = CartActivity.class;
                } else if (itemId == R.id.about) {
                    targetActivity = AboutActivity.class;
                } else if (itemId == R.id.contact) {
                    targetActivity = ContactActivity.class;
                } else if (itemId == R.id.home) {
                    targetActivity = HomeActivity.class;
                }
                
                if (targetActivity != null) {
                    navigateToActivity(targetActivity);
                    return true;
                }
                
                return false;
            });
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button in toolbar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Navigate to the specified activity, clearing the stack if going to Home
     */
    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        if (targetActivity == HomeActivity.class) {
            // Clear the stack when going home
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
        if (targetActivity != HomeActivity.class) {
            // Prevent animation when going back to home
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!(this instanceof HomeActivity)) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
} 