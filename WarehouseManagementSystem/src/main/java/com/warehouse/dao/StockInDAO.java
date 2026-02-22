package com.warehouse.dao;

import com.warehouse.config.DatabaseConfig;
import com.warehouse.model.StockIn;
import com.warehouse.model.StockInDetail;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockInDAO {
    private final ItemDAO itemDAO = new ItemDAO();
    private final StockInDetailDAO detailDAO = new StockInDetailDAO();
    
    public boolean createStockIn(StockIn stockIn) throws SQLException {
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Generate nomor transaksi: IN-YYYYMMDD-XXXX
            String inNumber = generateInNumber(conn);
            stockIn.setInNumber(inNumber);
            
            // Insert header
            String sql = "INSERT INTO stock_in (in_number, supplier_id, date, user_id, notes) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            int stockInId;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, stockIn.getInNumber());
                stmt.setInt(2, stockIn.getSupplierId());
                stmt.setDate(3, new java.sql.Date(stockIn.getDate().getTime()));
                stmt.setInt(4, stockIn.getUserId());
                stmt.setString(5, stockIn.getNotes());
                
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    stockInId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get generated key");
                }
            }
            
            // Insert details & update stock
            for (StockInDetail detail : stockIn.getDetails()) {
                detail.setStockInId(stockInId);
                detailDAO.insert(detail, conn);
                
                // Update stock barang (tambah)
                boolean updated = itemDAO.updateStock(detail.getItemId(), detail.getQuantity(), conn);
                if (!updated) {
                    throw new SQLException("Failed to update stock for item: " + detail.getItemId());
                }
            }
            
            conn.commit();
            stockIn.setId(stockInId);
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log error
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }
    
    private String generateInNumber(Connection conn) throws SQLException {
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "IN-" + dateStr + "-";
        
        String sql = "SELECT COUNT(*) as count FROM stock_in WHERE DATE(date) = CURDATE()";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            return String.format("%s%04d", prefix, count + 1);
        }
    }
}