package com.example.baira;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.res.Configuration;
import java.util.Locale;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends BaseActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private TextView tvUserGreeting;
    private RecyclerView rvPromotions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        
        // Configure Google Sign In for sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Initialize user greeting
        tvUserGreeting = findViewById(R.id.tvUserGreeting);
        updateUserInfo();
        
        // Set up promotions recycler view
        rvPromotions = findViewById(R.id.rvPromotions);
        setupPromotions();
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.home;
    }
    
    private void setupPromotions() {
        // In a real app, you would load promotions from a data source
        // For now, just set up the layout manager
        rvPromotions.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Here you would set an adapter with promotion items
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in, if not redirect to login
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            updateUserInfo();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            signOut();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void updateUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        String displayName = "Гость";
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                displayName = user.getDisplayName();
            }
        }
        tvUserGreeting.setText(getString(R.string.hello_user, displayName));
    }
    
    private void signOut() {
        // Firebase sign out
        auth.signOut();
        
        // Google sign out if signed in with Google
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(HomeActivity.this, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show();
            }
            // Go to login screen
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
} 