package com.warehouse.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockOut {
    private int id;
    private String outNumber;
    private String destination;
    private String department;
    private Date date;
    private int userId;
    private String userName;
    private String notes;
    private List<StockOutDetail> details = new ArrayList<>();
    
    public StockOut() {}
    
    public int getTotalItems() {
        return details.stream().mapToInt(StockOutDetail::getQuantity).sum();
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getOutNumber() { return outNumber; }
    public void setOutNumber(String outNumber) { this.outNumber = outNumber; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<StockOutDetail> getDetails() { return details; }
    public void setDetails(List<StockOutDetail> details) { this.details = details; }
    
    public void addDetail(StockOutDetail detail) {
        this.details.add(detail);
    }
}