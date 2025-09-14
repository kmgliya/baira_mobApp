package com.example.baira.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baira.R;
import com.example.baira.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG = "MessageAdapter";
    
    private Context context;
    private List<Message> messageList;
    private FirebaseUser currentUser;
    
    public MessageAdapter(Context context) {
        this.context = context;
        this.messageList = new ArrayList<>();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "MessageAdapter создан");
    }
    
    public void setMessages(List<Message> messages) {
        this.messageList = messages;
        Log.d(TAG, "Установлено " + messages.size() + " сообщений");
        notifyDataSetChanged();
    }
    
    public void addMessage(Message message) {
        if (message == null) {
            Log.e(TAG, "Попытка добавить null сообщение");
            return;
        }
        
        Log.d(TAG, "Добавляем сообщение: " + message.getText() + " от " + message.getSenderName());
        this.messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    public void clear() {
        messageList.clear();
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        
        if (currentUser != null && message.getSenderId().equals(currentUser.getUid())) {
            // Сообщение отправлено текущим пользователем
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // Сообщение получено от другого пользователя
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                Log.d(TAG, "Отображение отправленного сообщения на позиции " + position);
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                Log.d(TAG, "Отображение полученного сообщения на позиции " + position);
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    /**
     * Holder для отправленных сообщений
     */
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage;
        
        SentMessageHolder(View itemView) {
            super(itemView);
            
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            messageImage = itemView.findViewById(R.id.image_message_body);
        }
        
        void bind(Message message) {
            if (message.getMessageType() == 0) {
                // Текстовое сообщение
                messageText.setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.GONE);
                
                // Обеспечиваем правильную кодировку текста
                String text = message.getText();
                if (text != null) {
                    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                    String utf8Text = new String(bytes, StandardCharsets.UTF_8);
                    messageText.setText(utf8Text);
                    Log.d(TAG, "Установлен текст отправленного сообщения: " + utf8Text);
                } else {
                    messageText.setText("");
                    Log.w(TAG, "Получен пустой текст сообщения");
                }
            } else if (message.getMessageType() == 1 && message.getImageUrl() != null) {
                // Сообщение с изображением
                messageText.setVisibility(View.GONE);
                messageImage.setVisibility(View.VISIBLE);
                
                Glide.with(context)
                        .load(message.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.error_product)
                        .into(messageImage);
            }
            
            // Время сообщения
            timeText.setText(message.getFormattedTime());
        }
    }
    
    /**
     * Holder для полученных сообщений
     */
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView messageImage, profileImage;
        
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            messageImage = itemView.findViewById(R.id.image_message_body);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }
        
        void bind(Message message) {
            // Имя отправителя
            nameText.setText(message.getSenderName());
            
            if (message.getMessageType() == 0) {
                // Текстовое сообщение
                messageText.setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.GONE);
                
                // Обеспечиваем правильную кодировку текста
                String text = message.getText();
                if (text != null) {
                    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                    String utf8Text = new String(bytes, StandardCharsets.UTF_8);
                    messageText.setText(utf8Text);
                    Log.d(TAG, "Установлен текст полученного сообщения: " + utf8Text);
                } else {
                    messageText.setText("");
                    Log.w(TAG, "Получен пустой текст сообщения");
                }
            } else if (message.getMessageType() == 1 && message.getImageUrl() != null) {
                // Сообщение с изображением
                messageText.setVisibility(View.GONE);
                messageImage.setVisibility(View.VISIBLE);
                
                Glide.with(context)
                        .load(message.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.error_product)
                        .into(messageImage);
            }
            
            // Время сообщения
            timeText.setText(message.getFormattedTime());
            
            // Аватар (заглушка)
            profileImage.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }
} 