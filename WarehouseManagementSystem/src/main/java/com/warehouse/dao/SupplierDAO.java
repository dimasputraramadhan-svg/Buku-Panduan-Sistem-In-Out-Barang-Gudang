package com.warehouse.dao;

import com.warehouse.config.DatabaseConfig;
import com.warehouse.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    public List<Supplier> findAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY company_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }
    
    public List<Supplier> findAllActive() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE is_active = TRUE ORDER BY company_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }
    
    public Supplier findById(int id) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSupplier(rs);
            }
        }
        return null;
    }
    
    public boolean save(Supplier supplier) throws SQLException {
        String sql = supplier.getId() == 0 ?
            "INSERT INTO suppliers (code, company_name, contact_name, address, phone, email, is_active) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE suppliers SET code=?, company_name=?, contact_name=?, address=?, phone=?, email=?, is_active=? WHERE id=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, supplier.getCode());
            stmt.setString(2, supplier.getCompanyName());
            stmt.setString(3, supplier.getContactName());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getPhone());
            stmt.setString(6, supplier.getEmail());
            stmt.setBoolean(7, supplier.isActive());
            
            if (supplier.getId() != 0) {
                stmt.setInt(8, supplier.getId());
            }
            
            int affected = stmt.executeUpdate();
            
            if (supplier.getId() == 0 && affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    supplier.setId(rs.getInt(1));
                }
            }
            return affected > 0;
        }
    }
    
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getInt("id"));
        supplier.setCode(rs.getString("code"));
        supplier.setCompanyName(rs.getString("company_name"));
        supplier.setContactName(rs.getString("contact_name"));
        supplier.setAddress(rs.getString("address"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setActive(rs.getBoolean("is_active"));
        supplier.setCreatedAt(rs.getTimestamp("created_at"));
        return supplier;
    }
}