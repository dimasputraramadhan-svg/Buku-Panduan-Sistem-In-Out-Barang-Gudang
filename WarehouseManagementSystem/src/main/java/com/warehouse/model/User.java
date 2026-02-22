package com.warehouse.model;

// Hapus dulu semua import, nanti re-import setelah Maven reload
import org.mindrot.jbcrypt.BCrypt;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role;
    private boolean active;

    public User() {}

    // Method untuk cek password
    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, passwordHash);
    }

    // Static method untuk hash password (untuk registrasi/ubah password)
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}