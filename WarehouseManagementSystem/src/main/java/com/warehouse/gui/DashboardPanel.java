package com.warehouse.gui;

import com.warehouse.dao.ItemDAO;
import com.warehouse.model.Item;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardPanel extends JPanel {
    private JLabel lblTotalItems;
    private JLabel lblLowStock;
    private JLabel lblTodayIn;
    private JLabel lblTodayOut;
    
    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel lblTitle = new JLabel("DASHBOARD GUDANG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);
        
        // Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        
        lblTotalItems = createCard("TOTAL BARANG", "0", new Color(0, 123, 255));
        lblLowStock = createCard("STOK MENIPIS", "0", new Color(220, 53, 69));
        lblTodayIn = createCard("BARANG MASUK HARI INI", "0", new Color(40, 167, 69));
        lblTodayOut = createCard("BARANG KELUAR HARI INI", "0", new Color(255, 193, 7));
        
        cardsPanel.add(lblTotalItems);
        cardsPanel.add(lblLowStock);
        cardsPanel.add(lblTodayIn);
        cardsPanel.add(lblTodayOut);
        
        add(cardsPanel, BorderLayout.CENTER);
    }
    
    private JLabel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblValue.setForeground(color);
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        // Wrapper untuk return label value yang bisa diupdate
        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card);
        
        // Simpan reference ke lblValue di properties panel
        card.putClientProperty("valueLabel", lblValue);
        
        return lblValue;
    }
    
    public void refreshData() {
        try {
            ItemDAO dao = new ItemDAO();
            List<Item> items = dao.findAll();
            
            int total = items.size();
            int lowStock = (int) items.stream().filter(Item::isLowStock).count();
            
            lblTotalItems.setText(String.valueOf(total));
            lblLowStock.setText(String.valueOf(lowStock));
            lblTodayIn.setText("0"); // Implementasi query hari ini
            lblTodayOut.setText("0");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}