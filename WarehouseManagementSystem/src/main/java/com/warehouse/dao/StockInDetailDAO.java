package com.warehouse.dao;

import com.warehouse.model.StockInDetail;
import java.sql.*;

public class StockInDetailDAO {
    
    public void insert(StockInDetail detail, Connection conn) throws SQLException {
        String sql = "INSERT INTO stock_in_detail (stock_in_id, item_id, quantity, notes) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getStockInId());
            stmt.setInt(2, detail.getItemId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setString(4, detail.getNotes());
            stmt.executeUpdate();
        }
    }
}
