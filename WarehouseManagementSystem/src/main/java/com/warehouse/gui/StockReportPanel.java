package com.warehouse.gui;

import com.warehouse.dao.ItemDAO;
import com.warehouse.model.Item;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockReportPanel extends JPanel {
    private JTable tableReport;
    private DefaultTableModel tableModel;
    private JLabel lblTotalItems, lblTotalStock, lblLowStock, lblOutOfStock;
    
    public StockReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(108, 117, 125));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("LAPORAN PERSEDIAAN BARANG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Summary Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Buat card dengan reference ke JLabel value
        JPanel card1 = createCard("Total Jenis Barang", "0", new Color(0, 123, 255));
        lblTotalItems = (JLabel) card1.getClientProperty("valueLabel");
        
        JPanel card2 = createCard("Total Stok", "0", new Color(40, 167, 69));
        lblTotalStock = (JLabel) card2.getClientProperty("valueLabel");
        
        JPanel card3 = createCard("Stok Menipis", "0", new Color(255, 193, 7));
        lblLowStock = (JLabel) card3.getClientProperty("valueLabel");
        
        JPanel card4 = createCard("Stok Habis", "0", new Color(220, 53, 69));
        lblOutOfStock = (JLabel) card4.getClientProperty("valueLabel");
        
        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);
        
        add(cardsPanel, BorderLayout.PAGE_START);
        
        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExport = new JButton("Export Excel");
        
        btnRefresh.addActionListener(e -> refreshData());
        
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(btnExport);
        
        add(toolbarPanel, BorderLayout.PAGE_END);
        
        // Table
        String[] columns = {"No", "Kode", "Nama Barang", "Kategori", "Stok", "Min Stok", "Status", "Lokasi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableReport = new JTable(tableModel);
        tableReport.setRowHeight(25);
        
        // Color renderer
        tableReport.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && row < table.getRowCount()) {
                    Object statusObj = table.getValueAt(row, 6);
                    if (statusObj != null) {
                        String status = statusObj.toString();
                        if ("HABIS".equals(status)) {
                            c.setBackground(new Color(248, 215, 218));
                        } else if ("MENIPIS".equals(status)) {
                            c.setBackground(new Color(255, 243, 205));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableReport);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshData();
    }
    
    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        // Simpan reference ke JLabel
        card.putClientProperty("valueLabel", lblValue);
        
        return card;
    }
    
    public void refreshData() {
        try {
            ItemDAO dao = new ItemDAO();
            List<Item> items = dao.findAll();
            
            // Update summary
            int totalItems = items.size();
            int totalStock = items.stream().mapToInt(Item::getStock).sum();
            int lowStock = (int) items.stream().filter(Item::isLowStock).count();
            int outOfStock = (int) items.stream().filter(i -> i.getStock() == 0).count();
            
            // Update label langsung (tidak perlu method updateCard)
            if (lblTotalItems != null) lblTotalItems.setText(String.valueOf(totalItems));
            if (lblTotalStock != null) lblTotalStock.setText(String.valueOf(totalStock));
            if (lblLowStock != null) lblLowStock.setText(String.valueOf(lowStock));
            if (lblOutOfStock != null) lblOutOfStock.setText(String.valueOf(outOfStock));
            
            // Update table
            tableModel.setRowCount(0);
            int no = 1;
            for (Item item : items) {
                Object[] row = {
                    no++,
                    item.getCode(),
                    item.getName(),
                    item.getCategoryName() != null ? item.getCategoryName() : "-",
                    item.getStock(),
                    item.getMinStock(),
                    item.getStockStatus(),
                    item.getLocation()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}