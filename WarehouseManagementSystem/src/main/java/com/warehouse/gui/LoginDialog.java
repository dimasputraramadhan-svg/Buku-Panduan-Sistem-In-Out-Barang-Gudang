package com.warehouse.gui;

import com.warehouse.dao.UserDAO;
import com.warehouse.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSkip; // TOMBOL BYPASS
    private boolean authenticated = false;
    
    public LoginDialog() {
        super((Frame) null, "Login - Sistem Gudang", true);
        setSize(400, 300); // Perbesar sedikit
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel lblTitle = new JLabel("SISTEM IN OUT BARANG GUDANG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Login Button
        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        panel.add(btnLogin, gbc);
        
        // ==================== TOMBOL BYPASS ====================
        btnSkip = new JButton("SKIP LOGIN (Testing)");
        btnSkip.setBackground(new Color(255, 193, 7)); // Kuning
        btnSkip.setForeground(Color.BLACK);
        btnSkip.setFocusPainted(false);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(btnSkip, gbc);
        
        // Label info
        JLabel lblInfo = new JLabel("Default: admin / password", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        gbc.gridy = 5;
        panel.add(lblInfo, gbc);
        // ======================================================
        
        // Action Listeners
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        
        // BYPASS ACTION
        btnSkip.addActionListener(e -> doBypass());
        
        add(panel);
        getRootPane().setDefaultButton(btnLogin);
    }
    
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username dan password harus diisi!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                authenticated = true;
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Username atau password salah!", 
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==================== METHOD BYPASS ====================
    private void doBypass() {
        User dummyUser = new User();
        dummyUser.setId(1);
        dummyUser.setUsername("admin");
        dummyUser.setFullName("Administrator (Bypass)");
        dummyUser.setRole("admin");
        dummyUser.setActive(true);
        
        authenticated = true;
        dispose();
        new MainFrame(dummyUser).setVisible(true);
    }
    // ======================================================
    
    public boolean isAuthenticated() {
        return authenticated;
    }
}