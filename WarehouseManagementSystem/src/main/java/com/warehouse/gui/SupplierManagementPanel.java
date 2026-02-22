package com.warehouse.gui;

import com.warehouse.dao.SupplierDAO;
import com.warehouse.model.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierManagementPanel extends JPanel {
    private JTable tableSuppliers;
    private DefaultTableModel tableModel;
    
    public SupplierManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("MANAJEMEN DATA SUPPLIER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("➕ Tambah Supplier");
        JButton btnEdit = new JButton("✏️ Edit");
        JButton btnDelete = new JButton("🗑️ Hapus");
        JButton btnRefresh = new JButton("🔄 Refresh");
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnRefresh.addActionListener(e -> refreshData());
        
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        toolbarPanel.add(btnRefresh);
        
        add(toolbarPanel, BorderLayout.PAGE_START);
        
        // Table
        String[] columns = {"ID", "Kode", "Nama Perusahaan", "Kontak", "Telepon", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSuppliers = new JTable(tableModel);
        tableSuppliers.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableSuppliers);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshData();
    }
    
    public void refreshData() {
        try {
            SupplierDAO dao = new SupplierDAO();
            var suppliers = dao.findAll();
            
            tableModel.setRowCount(0);
            for (Supplier sup : suppliers) {
                Object[] row = {
                    sup.getId(),
                    sup.getCode(),
                    sup.getCompanyName(),
                    sup.getContactName(),
                    sup.getPhone(),
                    sup.getEmail(),
                    sup.isActive() ? "Aktif" : "Non-Aktif"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        SupplierDialog dialog = new SupplierDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = tableSuppliers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier yang akan diedit!");
            return;
        }
        
        int supId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            SupplierDAO dao = new SupplierDAO();
            Supplier sup = dao.findById(supId);
            
            if (sup != null) {
                SupplierDialog dialog = new SupplierDialog((JFrame) SwingUtilities.getWindowAncestor(this), sup);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteSupplier() {
        int selectedRow = tableSuppliers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier yang akan dihapus!");
            return;
        }
        
        int supId = (int) tableModel.getValueAt(selectedRow, 0);
        String supName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin hapus supplier '" + supName + "'?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                SupplierDAO dao = new SupplierDAO();
                if (dao.delete(supId)) {
                    JOptionPane.showMessageDialog(this, "Supplier berhasil dihapus!");
                    refreshData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private class SupplierDialog extends JDialog {
        private JTextField txtCode, txtCompanyName, txtContactName, txtPhone, txtEmail;
        private JTextArea txtAddress;
        private JCheckBox chkActive;
        private boolean saved = false;
        private Supplier supplier;
        
        public SupplierDialog(JFrame parent, Supplier supplier) {
            super(parent, supplier == null ? "Tambah Supplier" : "Edit Supplier", true);
            this.supplier = supplier;
            
            setSize(500, 450);
            setLocationRelativeTo(parent);
            
            initComponents();
            
            if (supplier != null) {
                loadSupplierData();
            }
        }
        
        private void initComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            addField(panel, gbc, 0, "Kode:*", txtCode = new JTextField(15));
            addField(panel, gbc, 1, "Nama Perusahaan:*", txtCompanyName = new JTextField(20));
            addField(panel, gbc, 2, "Nama Kontak:", txtContactName = new JTextField(20));
            addField(panel, gbc, 3, "Telepon:", txtPhone = new JTextField(15));
            addField(panel, gbc, 4, "Email:", txtEmail = new JTextField(20));
            
            gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Alamat:"), gbc);
            
            txtAddress = new JTextArea(3, 20);
            txtAddress.setLineWrap(true);
            JScrollPane scroll = new JScrollPane(txtAddress);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0;
            panel.add(scroll, gbc);
            
            gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            panel.add(new JLabel("Status:"), gbc);
            
            chkActive = new JCheckBox("Aktif");
            chkActive.setSelected(true);
            gbc.gridx = 1;
            panel.add(chkActive, gbc);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSave = new JButton("Simpan");
            JButton btnCancel = new JButton("Batal");
            
            btnSave.setBackground(new Color(40, 167, 69));
            btnSave.setForeground(Color.WHITE);
            
            btnSave.addActionListener(e -> saveSupplier());
            btnCancel.addActionListener(e -> dispose());
            
            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
            panel.add(buttonPanel, gbc);
            
            add(panel);
            getRootPane().setDefaultButton(btnSave);
        }
        
        private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            panel.add(new JLabel(label), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(field, gbc);
        }
        
        private void loadSupplierData() {
            txtCode.setText(supplier.getCode());
            txtCompanyName.setText(supplier.getCompanyName());
            txtContactName.setText(supplier.getContactName());
            txtPhone.setText(supplier.getPhone());
            txtEmail.setText(supplier.getEmail());
            txtAddress.setText(supplier.getAddress());
            chkActive.setSelected(supplier.isActive());
        }
        
        private void saveSupplier() {
            String code = txtCode.getText().trim();
            String companyName = txtCompanyName.getText().trim();
            
            if (code.isEmpty() || companyName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode dan Nama Perusahaan wajib diisi!");
                return;
            }
            
            try {
                SupplierDAO dao = new SupplierDAO();
                Supplier saveSup = (supplier != null) ? supplier : new Supplier();
                saveSup.setCode(code);
                saveSup.setCompanyName(companyName);
                saveSup.setContactName(txtContactName.getText().trim());
                saveSup.setPhone(txtPhone.getText().trim());
                saveSup.setEmail(txtEmail.getText().trim());
                saveSup.setAddress(txtAddress.getText().trim());
                saveSup.setActive(chkActive.isSelected());
                
                if (dao.save(saveSup)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Supplier berhasil disimpan!");
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