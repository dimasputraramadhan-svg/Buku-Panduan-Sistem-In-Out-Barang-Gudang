package com.warehouse.dao;

import com.warehouse.config.DatabaseConfig;
import com.warehouse.model.Item;
import com.warehouse.model.StockOut;
import com.warehouse.model.StockOutDetail;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockOutDAO {
    private final ItemDAO itemDAO = new ItemDAO();
    private final StockOutDetailDAO detailDAO = new StockOutDetailDAO();
    
    public boolean createStockOut(StockOut stockOut) throws SQLException, IllegalStateException {
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Validasi stok terlebih dahulu
            for (StockOutDetail detail : stockOut.getDetails()) {
                Item item = itemDAO.findById(detail.getItemId());
                if (item == null) {
                    throw new IllegalStateException("Barang tidak ditemukan: " + detail.getItemCode());
                }
                if (item.getStock() < detail.getQuantity()) {
                    throw new IllegalStateException(
                        "Stok tidak cukup untuk " + item.getName() + 
                        ". Tersedia: " + item.getStock() + ", Diminta: " + detail.getQuantity()
                    );
                }
            }
            
            // Generate nomor transaksi
            String outNumber = generateOutNumber(conn);
            stockOut.setOutNumber(outNumber);
            
            // Insert header
            String sql = "INSERT INTO stock_out (out_number, destination, department, date, user_id, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            int stockOutId;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, stockOut.getOutNumber());
                stmt.setString(2, stockOut.getDestination());
                stmt.setString(3, stockOut.getDepartment());
                stmt.setDate(4, new java.sql.Date(stockOut.getDate().getTime()));
                stmt.setInt(5, stockOut.getUserId());
                stmt.setString(6, stockOut.getNotes());
                
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    stockOutId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get generated key");
                }
            }
            
            // Insert details & update stock
            for (StockOutDetail detail : stockOut.getDetails()) {
                detail.setStockOutId(stockOutId);
                detailDAO.insert(detail, conn);
                
                // Update stock (kurangi)
                boolean updated = itemDAO.updateStock(detail.getItemId(), -detail.getQuantity(), conn);
                if (!updated) {
                    throw new SQLException("Failed to update stock for item: " + detail.getItemId());
                }
            }
            
            conn.commit();
            stockOut.setId(stockOutId);
            return true;
            
        } catch (SQLException | IllegalStateException e) {
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
    
    private String generateOutNumber(Connection conn) throws SQLException {
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "OUT-" + dateStr + "-";
        
        String sql = "SELECT COUNT(*) as count FROM stock_out WHERE DATE(date) = CURDATE()";
        
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