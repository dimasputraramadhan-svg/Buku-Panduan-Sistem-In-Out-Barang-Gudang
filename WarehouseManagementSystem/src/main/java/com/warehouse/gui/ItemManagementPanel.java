package com.warehouse.gui;

import com.warehouse.dao.CategoryDAO;
import com.warehouse.dao.ItemDAO;
import com.warehouse.model.Category;
import com.warehouse.model.Item;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemManagementPanel extends JPanel {
    private JTable tableItems;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<Item> currentItems;
    
    public ItemManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("MANAJEMEN DATA BARANG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Toolbar Panel
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cari");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Tambah Barang");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        
        btnSearch.addActionListener(e -> searchItems());
        btnRefresh.addActionListener(e -> refreshData());
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteItem());
        
        toolbarPanel.add(new JLabel("Cari:"));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(btnSearch);
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        
        add(toolbarPanel, BorderLayout.PAGE_START);
        
        // Table
        String[] columns = {"ID", "Kode", "Nama Barang", "Kategori", "Stok", "Min Stok", "Status", "Satuan", "Lokasi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableItems = new JTable(tableModel);
        tableItems.setRowHeight(25);
        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Color coding untuk stok menipis
        tableItems.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && row < table.getRowCount()) {
                    Object statusObj = table.getValueAt(row, 6);
                    if (statusObj != null) {
                        String status = statusObj.toString();
                        if ("MENIPIS".equals(status)) {
                            c.setBackground(new Color(255, 243, 205));
                            c.setForeground(new Color(133, 100, 4));
                        } else if ("HABIS".equals(status)) {
                            c.setBackground(new Color(248, 215, 218));
                            c.setForeground(new Color(114, 28, 36));
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableItems);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        refreshData();
    }
    
    public void refreshData() {
        try {
            ItemDAO dao = new ItemDAO();
            currentItems = dao.findAll();
            loadTableData(currentItems);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadTableData(List<Item> items) {
        tableModel.setRowCount(0);
        for (Item item : items) {
            Object[] row = {
                item.getId(),
                item.getCode(),
                item.getName(),
                item.getCategoryName() != null ? item.getCategoryName() : "-",
                item.getStock(),
                item.getMinStock(),
                item.getStockStatus(),
                item.getUnit(),
                item.getLocation()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchItems() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        try {
            ItemDAO dao = new ItemDAO();
            currentItems = dao.search(keyword);
            loadTableData(currentItems);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        ItemDialog dialog = new ItemDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan diedit!");
            return;
        }
        
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            ItemDAO dao = new ItemDAO();
            Item item = dao.findById(itemId);
            
            if (item != null) {
                ItemDialog dialog = new ItemDialog((JFrame) SwingUtilities.getWindowAncestor(this), item);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteItem() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan dihapus!");
            return;
        }
        
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin hapus barang '" + itemName + "'?\nPerhatian: Data yang sudah dihapus tidak bisa dikembalikan!",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ItemDAO dao = new ItemDAO();
                if (dao.delete(itemId)) {
                    JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!");
                    refreshData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // Inner class untuk Dialog Tambah/Edit
    private class ItemDialog extends JDialog {
        private JTextField txtCode, txtName, txtUnit, txtMinStock, txtLocation;
        private JComboBox<Category> cmbCategory;
        private JTextArea txtDescription;
        private boolean saved = false;
        private Item item;
        
        public ItemDialog(JFrame parent, Item item) {
            super(parent, item == null ? "Tambah Barang Baru" : "Edit Barang", true);
            this.item = item;
            
            setSize(500, 450);
            setLocationRelativeTo(parent);
            
            initComponents();
            
            if (item != null) {
                loadItemData();
            }
        }
        
        private void initComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Kode Barang
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Kode Barang:*"), gbc);
            
            txtCode = new JTextField(15);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(txtCode, gbc);
            
            // Nama Barang
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            panel.add(new JLabel("Nama Barang:*"), gbc);
            
            txtName = new JTextField(20);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(txtName, gbc);
            
            // Kategori
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            panel.add(new JLabel("Kategori:"), gbc);
            
            cmbCategory = new JComboBox<>();
            loadCategories();
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cmbCategory, gbc);
            
            // Satuan
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Satuan:*"), gbc);
            
            txtUnit = new JTextField(10);
            txtUnit.setText("pcs");
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtUnit, gbc);
            
            // Stok Minimum
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Stok Minimum:*"), gbc);
            
            txtMinStock = new JTextField(10);
            txtMinStock.setText("10");
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtMinStock, gbc);
            
            // Lokasi
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Lokasi:"), gbc);
            
            txtLocation = new JTextField(15);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtLocation, gbc);
            
            // Deskripsi
            gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Deskripsi:"), gbc);
            
            txtDescription = new JTextArea(3, 20);
            txtDescription.setLineWrap(true);
            JScrollPane scrollDesc = new JScrollPane(txtDescription);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
            panel.add(scrollDesc, gbc);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSave = new JButton("Simpan");
            JButton btnCancel = new JButton("Batal");
            
            btnSave.setBackground(new Color(40, 167, 69));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            
            btnSave.addActionListener(e -> saveItem());
            btnCancel.addActionListener(e -> dispose());
            
            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0;
            panel.add(buttonPanel, gbc);
            
            add(panel);
            getRootPane().setDefaultButton(btnSave);
        }
        
        private void loadCategories() {
            try {
                CategoryDAO dao = new CategoryDAO();
                List<Category> categories = dao.findAll();
                cmbCategory.addItem(null);
                for (Category cat : categories) {
                    cmbCategory.addItem(cat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadItemData() {
            txtCode.setText(item.getCode());
            txtName.setText(item.getName());
            txtUnit.setText(item.getUnit());
            txtMinStock.setText(String.valueOf(item.getMinStock()));
            txtLocation.setText(item.getLocation());
            txtDescription.setText(item.getDescription());
            
            for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                Category cat = cmbCategory.getItemAt(i);
                if (cat != null && cat.getId() == item.getCategoryId()) {
                    cmbCategory.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        private void saveItem() {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            String unit = txtUnit.getText().trim();
            String minStockStr = txtMinStock.getText().trim();
            
            if (code.isEmpty() || name.isEmpty() || unit.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode, Nama, dan Satuan wajib diisi!");
                return;
            }
            
            int minStock;
            try {
                minStock = Integer.parseInt(minStockStr);
                if (minStock < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Stok minimum harus angka positif!");
                return;
            }
            
            try {
                ItemDAO dao = new ItemDAO();
                
                Item saveItem = (item != null) ? item : new Item();
                saveItem.setCode(code);
                saveItem.setName(name);
                saveItem.setUnit(unit);
                saveItem.setMinStock(minStock);
                saveItem.setLocation(txtLocation.getText().trim());
                saveItem.setDescription(txtDescription.getText().trim());
                
                Category selectedCat = (Category) cmbCategory.getSelectedItem();
                if (selectedCat != null) {
                    saveItem.setCategoryId(selectedCat.getId());
                }
                
                if (dao.save(saveItem)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Barang berhasil disimpan!");
                    dispose();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
        
        public boolean isSaved() {
            return saved;
        }
    }
}