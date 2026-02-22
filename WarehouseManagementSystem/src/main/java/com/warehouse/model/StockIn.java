package com.warehouse.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockIn {
    private int id;
    private String inNumber;
    private int supplierId;
    private String supplierName;
    private Date date;
    private int userId;
    private String userName;
    private String notes;
    private List<StockInDetail> details = new ArrayList<>();
    
    public StockIn() {}
    
    public int getTotalItems() {
        return details.stream().mapToInt(StockInDetail::getQuantity).sum();
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getInNumber() { return inNumber; }
    public void setInNumber(String inNumber) { this.inNumber = inNumber; }
    
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<StockInDetail> getDetails() { return details; }
    public void setDetails(List<StockInDetail> details) { this.details = details; }
    
    public void addDetail(StockInDetail detail) {
        this.details.add(detail);
    }
}