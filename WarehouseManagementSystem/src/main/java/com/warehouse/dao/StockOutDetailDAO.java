package com.warehouse.dao;

import com.warehouse.model.StockOutDetail;
import java.sql.*;

public class StockOutDetailDAO {
    
    public void insert(StockOutDetail detail, Connection conn) throws SQLException {
        String sql = "INSERT INTO stock_out_detail (stock_out_id, item_id, quantity, notes) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getStockOutId());
            stmt.setInt(2, detail.getItemId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setString(4, detail.getNotes());
            stmt.executeUpdate();
        }
    }
}