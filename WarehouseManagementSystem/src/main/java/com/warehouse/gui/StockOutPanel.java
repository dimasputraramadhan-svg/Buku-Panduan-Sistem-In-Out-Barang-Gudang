package com.warehouse.gui;

import com.warehouse.dao.ItemDAO;
import com.warehouse.dao.StockOutDAO;
import com.warehouse.model.Item;
import com.warehouse.model.StockOut;
import com.warehouse.model.StockOutDetail;
import com.warehouse.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class StockOutPanel extends JPanel {
    private JTextField txtDestination, txtDepartment, txtItemCode, txtItemName, txtQuantity;
    private JTable tableItems;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnSave, btnClear;
    
    private User currentUser;
    private StockOut currentTransaction;
    private Item currentItem;
    
    public StockOutPanel(User user) {
        this.currentUser = user;
        this.currentTransaction = new StockOut();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 53, 69)); // Merah untuk Barang Keluar
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("TRANSAKSI BARANG KELUAR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(280);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        splitPane.setTopComponent(formPanel);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        splitPane.setBottomComponent(tablePanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Data"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tujuan
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tujuan:*"), gbc);
        
        txtDestination = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtDestination, gbc);
        
        // Departemen
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Departemen:"), gbc);
        
        txtDepartment = new JTextField(15);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtDepartment, gbc);
        
        // Tanggal
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tanggal:"), gbc);
        
        JTextField txtDate = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtDate.setEditable(false);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtDate, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);
        
        // Kode Barang
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Kode Barang:*"), gbc);
        
        txtItemCode = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtItemCode, gbc);
        
        JButton btnCheck = new JButton("Cek Stok");
        btnCheck.addActionListener(e -> checkStock());
        gbc.gridx = 2;
        panel.add(btnCheck, gbc);
        
        // Nama Barang
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Nama Barang:"), gbc);
        
        txtItemName = new JTextField(20);
        txtItemName.setEditable(false);
        txtItemName.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtItemName, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Jumlah:*"), gbc);
        
        txtQuantity = new JTextField(10);
        gbc.gridx = 1;
        panel.add(txtQuantity, gbc);
        
        // Button Tambah
        btnAdd = new JButton("TAMBAH KE LIST");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(btnAdd, gbc);
        
        txtItemCode.addActionListener(e -> checkStock());
        btnAdd.addActionListener(e -> addToList());
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Barang Keluar"));
        
        String[] columns = {"No", "Kode", "Nama Barang", "Qty", "Satuan", "Stok Tersedia", "Keterangan", "Hapus"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        tableItems = new JTable(tableModel);
        tableItems.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableItems);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnClear = new JButton("BERSIHKAN");
        btnClear.setBackground(new Color(108, 117, 125));
        btnClear.setForeground(Color.WHITE);
        
        btnSave = new JButton("SIMPAN TRANSAKSI");
        btnSave.setBackground(new Color(220, 53, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnClear.addActionListener(e -> resetForm());
        btnSave.addActionListener(e -> saveTransaction());
        
        panel.add(btnClear);
        panel.add(btnSave);
        
        return panel;
    }
    
    private void checkStock() {
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) return;
        
        try {
            ItemDAO dao = new ItemDAO();
            Item item = dao.findByCode(code);
            
            if (item != null) {
                currentItem = item;
                txtItemName.setText(item.getName() + " (Stok: " + item.getStock() + ")");
                
                if (item.getStock() <= 0) {
                    JOptionPane.showMessageDialog(this, "Stok barang HABIS!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    currentItem = null;
                } else {
                    txtQuantity.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!");
                txtItemCode.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void addToList() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Cek barang terlebih dahulu!");
            return;
        }
        
        String qtyStr = txtQuantity.getText().trim();
        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah!");
            return;
        }
        
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus > 0!");
                return;
            }
            
            if (qty > currentItem.getStock()) {
                JOptionPane.showMessageDialog(this, 
                    "Stok tidak cukup!\nTersedia: " + currentItem.getStock() + "\nDiminta: " + qty,
                    "Stok Tidak Cukup", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StockOutDetail detail = new StockOutDetail();
            detail.setItemId(currentItem.getId());
            detail.setItemCode(currentItem.getCode());
            detail.setItemName(currentItem.getName());
            detail.setUnit(currentItem.getUnit());
            detail.setQuantity(qty);
            
            currentTransaction.addDetail(detail);
            
            Vector<Object> row = new Vector<>();
            row.add(tableModel.getRowCount() + 1);
            row.add(currentItem.getCode());
            row.add(currentItem.getName());
            row.add(qty);
            row.add(currentItem.getUnit());
            row.add(currentItem.getStock());
            row.add("");
            row.add("Hapus");
            
            tableModel.addRow(row);
            
            txtItemCode.setText("");
            txtItemName.setText("");
            txtQuantity.setText("");
            currentItem = null;
            txtItemCode.requestFocus();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus angka!");
        }
    }
    
    private void saveTransaction() {
        if (currentTransaction.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada barang yang ditambahkan!");
            return;
        }
        
        String destination = txtDestination.getText().trim();
        if (destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tujuan pengeluaran wajib diisi!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Simpan transaksi barang keluar ini?\nStok akan berkurang secara otomatis.",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            currentTransaction.setDestination(destination);
            currentTransaction.setDepartment(txtDepartment.getText().trim());
            currentTransaction.setDate(new Date());
            currentTransaction.setUserId(currentUser.getId());
            
            StockOutDAO dao = new StockOutDAO();
            boolean success = dao.createStockOut(currentTransaction);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Transaksi berhasil disimpan!\nNo: " + currentTransaction.getOutNumber());
                resetForm();
            }
            
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public void resetForm() {
        currentTransaction = new StockOut();
        currentItem = null;
        tableModel.setRowCount(0);
        txtDestination.setText("");
        txtDepartment.setText("");
        txtItemCode.setText("");
        txtItemName.setText("");
        txtQuantity.setText("");
        txtDestination.requestFocus();
    }
}