package com.warehouse.model;

public class Item {
    private int id;
    private String code;
    private String name;
    private int categoryId;
    private String categoryName; // Untuk display
    private String unit;
    private int stock;
    private int minStock;
    private String location;
    private String description;
    
    public Item() {}
    
    // Constructor lengkap
    public Item(int id, String code, String name, int categoryId, 
                String unit, int stock, int minStock, String location) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.categoryId = categoryId;
        this.unit = unit;
        this.stock = stock;
        this.minStock = minStock;
        this.location = location;
    }
    
    // Business method
    public String getStockStatus() {
        if (stock == 0) return "HABIS";
        if (stock <= minStock) return "MENIPIS";
        return "AMAN";
    }
    
    public boolean isLowStock() {
        return stock <= minStock;
    }
    
    // Getters & Setters (generate semua)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { 
        if (stock < 0) throw new IllegalArgumentException("Stok tidak boleh negatif");
        this.stock = stock; 
    }
    
    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}