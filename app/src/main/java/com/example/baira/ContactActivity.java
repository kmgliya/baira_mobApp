package com.example.baira;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Установка заголовка экрана
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Свяжитесь с нами");
        }

        setupContactCard();   // Настройка карточки с кнопкой "Открыть чат"
        setupContactInfo();   // Телефон и email кликабельные
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.contact;
    }

    private void setupContactInfo() {
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);

        // Обработка нажатия на номер телефона
        phoneTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneTextView.getText().toString().replace(" ", "")));
            startActivity(intent);
        });

        // Обработка нажатия на email
        emailTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + emailTextView.getText().toString()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Запрос информации");
            startActivity(intent);
        });
    }

    private void setupContactCard() {
        CardView contactCard = findViewById(R.id.chatCard); // Было: contactCard
        Button writeMessageBtn = findViewById(R.id.chatButton); // Было: writeMessageBtn
        ImageView messageIcon = findViewById(R.id.chatIcon); // такой id пока не существует


        // Установка иконки для чата
        messageIcon.setImageResource(R.drawable.ic_chat);

        // Настройка кнопки для перехода в чат
        writeMessageBtn.setText("Открыть чат с консультантом");
        writeMessageBtn.setOnClickListener(v -> {
            Intent chatIntent = new Intent(ContactActivity.this, ChatActivity.class);
            startActivity(chatIntent);
        });
    }
}
