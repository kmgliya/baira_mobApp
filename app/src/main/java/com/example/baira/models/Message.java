package com.example.baira.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Класс, представляющий сообщение в чате.
 */
@IgnoreExtraProperties
public class Message {
    private String messageId;
    private String text;
    private String senderId;
    private String senderName;
    private long timestamp;
    private boolean isRead;
    private String imageUrl;
    private int messageType; // 0 - текст, 1 - изображение, 2 - системное сообщение и т.д.
    
    // Необходимый пустой конструктор для Firebase
    public Message() {
    }
    
    public Message(String text, String senderId, String senderName) {
        // Обеспечиваем правильную кодировку текста
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        this.text = new String(bytes, StandardCharsets.UTF_8);
        
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = new Date().getTime();
        this.isRead = false;
        this.messageType = 0; // По умолчанию текстовое сообщение
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        // Обеспечиваем правильную кодировку текста при установке
        if (text != null) {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            this.text = new String(bytes, StandardCharsets.UTF_8);
        } else {
            this.text = null;
        }
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getMessageType() {
        return messageType;
    }
    
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    
    @Exclude
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ru"));
        return sdf.format(new Date(timestamp));
    }
    
    @Exclude
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", new Locale("ru"));
        return sdf.format(new Date(timestamp));
    }
    
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("text", text);
        result.put("senderId", senderId);
        result.put("senderName", senderName);
        result.put("timestamp", timestamp);
        result.put("isRead", isRead);
        if (imageUrl != null) result.put("imageUrl", imageUrl);
        result.put("messageType", messageType);
        
        return result;
    }
} 