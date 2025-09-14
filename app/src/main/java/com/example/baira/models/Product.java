package com.example.baira.models;

public class Product {
    private String id;
    private String name;
    private String imageUrl;
    private double price;
    private String category;

    public Product() {
        this("", "", "", 0.0, "");
    }

    public Product(String id, String name, String imageUrl, double price, String category) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
} 