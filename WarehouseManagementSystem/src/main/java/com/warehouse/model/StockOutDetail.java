package com.warehouse.model;

public class StockOutDetail {
    private int id;
    private int stockOutId;
    private int itemId;
    private String itemCode;
    private String itemName;
    private String unit;
    private int quantity;
    private String notes;
    
    public StockOutDetail() {}
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getStockOutId() { return stockOutId; }
    public void setStockOutId(int stockOutId) { this.stockOutId = stockOutId; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}