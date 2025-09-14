package com.example.baira;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baira.adapters.MessageAdapter;
import com.example.baira.models.Message;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private MaterialButton sendButton;
    private MaterialButton attachButton;
    private MessageAdapter messageAdapter;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private ListenerRegistration messagesListenerReg;
    
    // Идентификатор консультанта
    private static final String CONSULTANT_ID = "consultant";
    private static final String CONSULTANT_NAME = "Консультант BAIRA";
    
    // Время задержки автоответа (в миллисекундах)
    private static final long AUTO_REPLY_DELAY = 1500;
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.contact;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Чат с консультантом");
            }
            initFirebase();
            initUI();
            loadMessages();
            if (savedInstanceState == null) {
                sendWelcomeMessage();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при запуске ChatActivity", e);
            Toast.makeText(this, "Ошибка при запуске чата: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initFirebase() {
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();
            firestore = FirebaseFirestore.getInstance();
            if (currentUser == null) {
                firebaseAuth.signInAnonymously()
                        .addOnSuccessListener(authResult -> {
                            currentUser = authResult.getUser();
                            Log.d(TAG, "Анонимная авторизация успешна");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Ошибка анонимной авторизации", e);
                            Toast.makeText(ChatActivity.this, "Ошибка подключения к чату: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации Firebase", e);
            Toast.makeText(this, "Ошибка инициализации Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initUI() {
        // Инициализация компонентов UI
        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.btnSend);
        attachButton = findViewById(R.id.btnAttach);
        
        // Настройка поля ввода для поддержки русского языка
        messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        
        // Настройка RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        
        // Создание адаптера
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);
        
        // Отправка сообщений
        sendButton.setOnClickListener(v -> sendMessage());
        
        // Прикрепление файлов
        attachButton.setOnClickListener(v -> {
            Toast.makeText(this, "Функция отправки файлов в разработке", Toast.LENGTH_SHORT).show();
        });
        
        // Скрываем нижнюю навигацию в чате
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }
    
    private void loadMessages() {
        try {
            if (messagesListenerReg != null) {
                messagesListenerReg.remove();
            }
            messagesListenerReg = firestore.collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limitToLast(50)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "Ошибка загрузки сообщений: " + e.getMessage());
                                Toast.makeText(ChatActivity.this, "Ошибка при загрузке сообщений: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                try {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        Message message = dc.getDocument().toObject(Message.class);
                                        message.setMessageId(dc.getDocument().getId());
                                        messageAdapter.addMessage(message);
                                        recyclerView.post(() ->
                                                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "Ошибка при обработке сообщения", ex);
                                    Toast.makeText(ChatActivity.this, "Ошибка при обработке сообщения: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при подписке на сообщения", e);
            Toast.makeText(this, "Ошибка при подписке на сообщения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }
        Message message = new Message(
                messageText,
                currentUser != null ? currentUser.getUid() : "guest",
                currentUser != null ? (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Пользователь") : "Пользователь"
        );
        messageEditText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        }
        firestore.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    message.setMessageId(documentReference.getId());
                    Log.d(TAG, "Сообщение отправлено");
                    sendConsultantReply(messageText);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка при отправке сообщения", e);
                    Toast.makeText(ChatActivity.this, "Ошибка при отправке сообщения", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void sendWelcomeMessage() {
        firestore.collection("messages")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        Message welcomeMessage = new Message(
                                "Hello! I am BAIRA consultant. How can I help you?",
                                CONSULTANT_ID,
                                CONSULTANT_NAME
                        );
                        firestore.collection("messages")
                                .add(welcomeMessage)
                                .addOnSuccessListener(documentReference ->
                                        Log.d(TAG, "Welcome message sent"))
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error sending welcome message", e));
                    }
                });
    }
    
    private void sendConsultantReply(String userMessage) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String replyText = generateReply(userMessage);
            Message replyMessage = new Message(
                    replyText,
                    CONSULTANT_ID,
                    CONSULTANT_NAME
            );
            firestore.collection("messages")
                    .add(replyMessage)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "Ответ консультанта отправлен"))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Ошибка при отправке ответа", e));
        }, AUTO_REPLY_DELAY);
    }
    
    private String generateReply(String userMessage) {
        String lowercaseMessage = userMessage.toLowerCase();

        if (lowercaseMessage.contains("hello") || lowercaseMessage.contains("hi") || lowercaseMessage.contains("привет")) {
            return "Здравствуйте! Чем я могу вам помочь?";
        } else if (lowercaseMessage.contains("order") || lowercaseMessage.contains("заказ")) {
            return "Чтобы оформить заказ, добавьте товары в корзину и перейдите в раздел 'Корзина'.";
        } else if (lowercaseMessage.contains("cart") || lowercaseMessage.contains("корзин")) {
            return "Ваша корзина находится в нижнем меню. Там вы можете просмотреть и оформить заказ.";
        } else if (lowercaseMessage.contains("pay") || lowercaseMessage.contains("оплат")) {
            return "Оплатить заказ можно при получении или онлайн картой. Выберите способ оплаты при оформлении заказа.";
        } else if (lowercaseMessage.contains("delivery") || lowercaseMessage.contains("доставк")) {
            return "Мы осуществляем доставку по всему городу. Доставка бесплатная при заказе от 3000 рублей.";
        } else if (lowercaseMessage.contains("discount") || lowercaseMessage.contains("скидк") || lowercaseMessage.contains("акци")) {
            return "Сейчас действует скидка 15% на новую коллекцию. Следите за нашими акциями!";
        } else if (lowercaseMessage.contains("address") || lowercaseMessage.contains("адрес") || lowercaseMessage.contains("где")) {
            return "Наш магазин находится по адресу: г. Бишкек, ул. Киевская, 95. Мы работаем ежедневно с 10:00 до 20:00.";
        } else {
            String[] replies = {
                "Спасибо за ваше сообщение! Чем я могу помочь?",
                "Я готов ответить на ваши вопросы о нашей продукции.",
                "Если у вас есть конкретные вопросы, я с радостью на них отвечу.",
                "Могу я уточнить, какая именно информация вас интересует?",
                "Мы стремимся предоставить лучший сервис нашим клиентам. Чем я могу вам помочь сегодня?"
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListenerReg != null) {
            messagesListenerReg.remove();
        }
    }
} 