package com.warehouse.gui;

import com.warehouse.dao.*;
import com.warehouse.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class StockInPanel extends JPanel {
    private JComboBox<Supplier> cmbSupplier;
    private JTextField txtDate;
    private JTextField txtItemCode;
    private JTextField txtItemName;
    private JTextField txtQuantity;
    private JButton btnAdd;
    private JButton btnSave;
    private JButton btnClear;
    private JTable tableItems;
    private DefaultTableModel tableModel;
    
    private User currentUser;
    private StockIn currentTransaction;
    private Item currentItem;
    
    public StockInPanel(User user) {
        this.currentUser = user;
        this.currentTransaction = new StockIn();
        initComponents();
        loadSuppliers();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("TRANSAKSI BARANG MASUK");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        
        // Form Panel (Atas)
        JPanel formPanel = createFormPanel();
        splitPane.setTopComponent(formPanel);
        
        // Table Panel (Bawah)
        JPanel tablePanel = createTablePanel();
        splitPane.setBottomComponent(tablePanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Button Panel (South)
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Data"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Supplier
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Supplier:"), gbc);
        
        cmbSupplier = new JComboBox<>();
        cmbSupplier.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(cmbSupplier, gbc);
        
        // Tanggal
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Tanggal:"), gbc);
        
        txtDate = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtDate.setEditable(false);
        txtDate.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        panel.add(txtDate, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);
        
        // Kode Barang
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Kode Barang:"), gbc);
        
        txtItemCode = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtItemCode, gbc);
        
        // Nama Barang (auto-fill)
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Nama Barang:"), gbc);
        
        txtItemName = new JTextField(20);
        txtItemName.setEditable(false);
        txtItemName.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(txtItemName, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Jumlah:"), gbc);
        
        txtQuantity = new JTextField(10);
        gbc.gridx = 1;
        panel.add(txtQuantity, gbc);
        
        // Button Tambah
        btnAdd = new JButton("TAMBAH KE LIST");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(btnAdd, gbc);
        
        // Event Listeners
        txtItemCode.addActionListener(e -> searchItem());
        btnAdd.addActionListener(e -> addToList());
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Barang"));
        
        String[] columns = {"No", "Kode", "Nama Barang", "Qty", "Satuan", "Keterangan", "Hapus"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Hanya kolom hapus yang editable (untuk button)
            }
        };
        
        tableItems = new JTable(tableModel);
        tableItems.setRowHeight(25);
        tableItems.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableItems.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableItems.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tableItems);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnClear = new JButton("BERSIHKAN");
        btnClear.setBackground(new Color(108, 117, 125));
        btnClear.setForeground(Color.WHITE);
        
        btnSave = new JButton("SIMPAN TRANSAKSI");
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnClear.addActionListener(e -> resetForm());
        btnSave.addActionListener(e -> saveTransaction());
        
        panel.add(btnClear);
        panel.add(btnSave);
        
        return panel;
    }
    
    private void loadSuppliers() {
        try {
            SupplierDAO dao = new SupplierDAO();
            List<Supplier> suppliers = dao.findAllActive();
            cmbSupplier.removeAllItems();
            for (Supplier s : suppliers) {
                cmbSupplier.addItem(s);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error load suppliers: " + e.getMessage());
        }
    }
    
    private void searchItem() {
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) return;
        
        try {
            ItemDAO dao = new ItemDAO();
            Item item = dao.findByCode(code);
            
            if (item != null) {
                currentItem = item;
                txtItemName.setText(item.getName());
                txtQuantity.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!");
                txtItemCode.setText("");
                txtItemCode.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void addToList() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Scan barang terlebih dahulu!");
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
            
            // Buat detail
            StockInDetail detail = new StockInDetail();
            detail.setItemId(currentItem.getId());
            detail.setItemCode(currentItem.getCode());
            detail.setItemName(currentItem.getName());
            detail.setUnit(currentItem.getUnit());
            detail.setQuantity(qty);
            
            currentTransaction.addDetail(detail);
            
            // Add to table
            Vector<Object> row = new Vector<>();
            row.add(tableModel.getRowCount() + 1);
            row.add(currentItem.getCode());
            row.add(currentItem.getName());
            row.add(qty);
            row.add(currentItem.getUnit());
            row.add("");
            row.add("Hapus");
            
            tableModel.addRow(row);
            
            // Reset input
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
        
        if (cmbSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Simpan transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();
            currentTransaction.setSupplierId(supplier.getId());
            currentTransaction.setDate(new Date());
            currentTransaction.setUserId(currentUser.getId());
            
            StockInDAO dao = new StockInDAO();
            boolean success = dao.createStockIn(currentTransaction);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Transaksi berhasil disimpan!\nNo: " + currentTransaction.getInNumber());
                resetForm();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public void resetForm() {
        currentTransaction = new StockIn();
        currentItem = null;
        tableModel.setRowCount(0);
        txtItemCode.setText("");
        txtItemName.setText("");
        txtQuantity.setText("");
        cmbSupplier.setSelectedIndex(0);
        txtItemCode.requestFocus();
    }
}