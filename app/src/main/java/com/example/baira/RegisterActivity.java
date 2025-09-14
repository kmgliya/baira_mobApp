package com.example.baira;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegisterAction);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Сохраняем имя пользователя в профиль Firebase
                        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.putExtra("USER_NAME", name);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }
                    } else {
                        Toast.makeText(this, "Ошибка регистрации: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }
} 