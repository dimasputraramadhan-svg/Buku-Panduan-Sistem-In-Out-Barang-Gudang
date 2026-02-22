## 📘 Deskripsi Proyek

Aplikasi **Warehouse Management System (WMS)** ini merupakan sistem informasi **In Out Barang Gudang** berbasis **Java Desktop** yang dikembangkan sebagai **studi kasus pembelajaran dan implementasi konsep Pemrograman Berorientasi Objek (Object-Oriented Programming / OOP)**.

Proyek ini dibuat untuk mendukung **kebutuhan akademik (skripsi/tugas akhir)** dengan penerapan arsitektur perangkat lunak yang terstruktur, mencakup pemodelan UML, perancangan basis data, serta implementasi aplikasi nyata menggunakan **Java Swing tanpa GUI Builder (Matisse)** dan database **MariaDB**.

---

## 🎯 Tujuan Pengembangan

- Mengimplementasikan konsep **OOP** dalam aplikasi nyata
- Menerapkan **layered architecture** (Model, DAO, Service, GUI)
- Mengembangkan sistem manajemen gudang yang terstruktur
- Menjadi **referensi akademik** untuk mahasiswa TI/SI
- Meningkatkan pemahaman Java Desktop & JDBC

---

## 📌 Ruang Lingkup Sistem

- Pengelolaan data barang dan stok
- Pencatatan barang masuk dan keluar
- Monitoring stok secara real-time
- Penyusunan laporan persediaan
- Pengaturan hak akses pengguna

---

## 👤 Aktor Sistem

| Aktor | Deskripsi |
|------|----------|
| **Administrator** | Mengelola master data, pengguna, dan laporan |
| **Warehouse Staff** | Mengelola transaksi barang masuk dan keluar |
| **Manager** | Monitoring laporan dan dashboard (read-only) |

---

## ⚙️ Fitur Utama

- CRUD Data Barang
- Manajemen Kategori dan Supplier
- Transaksi Barang Masuk (Stock In)
- Transaksi Barang Keluar (Stock Out)
- Validasi Stok Otomatis
- Alert Stok Minimum
- Pencarian & Tracking Barang
- Dashboard & Laporan
- Cetak Bukti Transaksi

---

## 🏗️ Arsitektur Sistem

Aplikasi ini menerapkan **Layered Architecture**:

1. **Model Layer** – Representasi entitas (POJO)
2. **DAO Layer** – Akses database (JDBC)
3. **Service Layer** – Logika bisnis
4. **GUI Layer** – Antarmuka Java Swing
5. **Utils Layer** – Helper & utilities

---

## 📁 Struktur Proyek


src/
└── main/java/com/warehouse/
├── config/ # Konfigurasi database
├── model/ # Entity / POJO
├── dao/ # Data Access Object
├── service/ # Business logic
├── gui/ # Java Swing UI
├── utils/ # Helper
└── Main.java # Entry point

lib/
└── mariadb-java-client.jar


---

## 🗄️ Basis Data

- **DBMS**: MariaDB
- **Normalisasi**: Third Normal Form (3NF)
- **Entitas Utama**:
  - Categories
  - Suppliers
  - Items
  - Users
  - Stock In
  - Stock Out
  - Transaction Details

Sistem menggunakan **database transaction (commit & rollback)** untuk menjaga integritas data.

---

## 🔐 Keamanan Sistem

- Enkripsi password menggunakan **BCrypt**
- Role-Based Access Control (RBAC)
- Validasi stok sebelum transaksi keluar
- Soft delete untuk data penting

---

## 🖥️ Teknologi yang Digunakan

- Java SE
- Java Swing (Manual UI)
- JDBC
- MariaDB
- NetBeans IDE
- BCrypt

---

## 🚀 Cara Menjalankan Aplikasi

1. Clone repository:
   ```bash
   git clone https://github.com/dimasputraramadhan-svg/Buku-Panduan-Sistem-In-Out-Barang-Gudang.git

Import project ke NetBeans
Buat database warehouse_db
Import struktur tabel sesuai ERD
Konfigurasi database di DatabaseConfig.java
Jalankan Main.java

📈 Pengembangan Selanjutnya

Multi-warehouse
Barcode / QR Code
Stock opname
REST API (Spring Boot)
Migrasi JavaFX
Docker & CI/CD
Two-Factor Authentication

🎓 Konteks Akademik

Proyek ini disusun sebagai:

📌 Tugas Akhir Semester
📌 Referensi pembelajaran OOP Java
📌 Studi kasus Sistem Informasi Gudang

Dapat digunakan dan dikembangkan kembali untuk kebutuhan akademik dengan mencantumkan sumber.

🔗 Penyusun
Nama : Dimas Putra Ramadhan
NIM :24131310032
Universitas : UNTARA

📦 Link Ebook:
https://ebook.webiot.id/ebooks/buku-panduan-sistem-in-out-gudang
