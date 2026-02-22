package com.warehouse.dao;

import com.warehouse.config.DatabaseConfig;
import com.warehouse.model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    
    public List<Item> findAll() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM items i " +
                     "LEFT JOIN categories c ON i.category_id = c.id ORDER BY i.name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }
        return items;
    }
    
    public Item findById(int id) throws SQLException {
        String sql = "SELECT i.*, c.name as category_name FROM items i " +
                     "LEFT JOIN categories c ON i.category_id = c.id WHERE i.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        }
        return null;
    }
    
    public Item findByCode(String code) throws SQLException {
        String sql = "SELECT i.*, c.name as category_name FROM items i " +
                     "LEFT JOIN categories c ON i.category_id = c.id WHERE i.code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        }
        return null;
    }
    
    public List<Item> search(String keyword) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM items i " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "WHERE i.code LIKE ? OR i.name LIKE ? ORDER BY i.name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }
        return items;
    }
    
    public boolean save(Item item) throws SQLException {
        String sql = item.getId() == 0 ? 
            "INSERT INTO items (code, name, category_id, unit, stock, min_stock, location, description) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE items SET code=?, name=?, category_id=?, unit=?, min_stock=?, location=?, description=? WHERE id=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getCode());
            stmt.setString(2, item.getName());
            stmt.setObject(3, item.getCategoryId());
            stmt.setString(4, item.getUnit());
            
            if (item.getId() == 0) {
                stmt.setInt(5, item.getStock());
                stmt.setInt(6, item.getMinStock());
                stmt.setString(7, item.getLocation());
                stmt.setString(8, item.getDescription());
            } else {
                stmt.setInt(5, item.getMinStock());
                stmt.setString(6, item.getLocation());
                stmt.setString(7, item.getDescription());
                stmt.setInt(8, item.getId());
            }
            
            int affected = stmt.executeUpdate();
            
            if (item.getId() == 0 && affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            return affected > 0;
        }
    }
    
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateStock(int itemId, int quantityChange, Connection conn) 
            throws SQLException {
        String sql = "UPDATE items SET stock = stock + ? WHERE id = ?";
        
        boolean useExternalConn = (conn != null);
        Connection connection = useExternalConn ? conn : DatabaseConfig.getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;
        } finally {
            if (!useExternalConn && connection != null) {
                connection.close();
            }
        }
    }
    
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setCode(rs.getString("code"));
        item.setName(rs.getString("name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setUnit(rs.getString("unit"));
        item.setStock(rs.getInt("stock"));
        item.setMinStock(rs.getInt("min_stock"));
        item.setLocation(rs.getString("location"));
        item.setDescription(rs.getString("description"));
        return item;
    }
}