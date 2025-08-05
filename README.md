# StoryApp – Proyek Submission Aplikasi Story

StoryApp adalah aplikasi Android sederhana yang saya kembangkan sebagai bagian dari tugas akhir kelas Android Mahir Dicoding melalui program beasiswa IDCamp. Aplikasi ini memiliki fitur login, registrasi, menampilkan daftar cerita dari API, menambah cerita baru, dan melihat detail cerita.

Proyek ini masih dalam proses pengembangan dan akan terus disempurnakan sesuai dengan kriteria dan saran dari reviewer.

---

## Cuplikan Layar

<table align="center">
  <tr>
    <td align="center" valign="top">
      <strong>Home</strong><br>
      <img src="https://github.com/user-attachments/assets/4ec9d426-aa2b-4838-8794-949391ab3392" width="250"/>
    </td>
    <td align="center" valign="top">
      <strong></strong><br>
      <img src="https://github.com/user-attachments/assets/ee2e79e4-f068-4b87-83e5-384bd0984b9e" width="250"/>
    </td>
    <td align="center" valign="top">
      <strong>Favorit</strong><br>
      <img src="https://github.com/user-attachments/assets/3c471f47-2ada-4e54-873d-bfa80db60618" width="250"/>
    </td>
    <td align="center" valign="top">
      <strong>Notification</strong><br>
      <img src="https://github.com/user-attachments/assets/64b9028c-a87c-4e4a-8c49-95f2124b121d" width="250"/>
    </td>
    <td align="center" valign="top">
      <strong>Notification</strong><br>
      <img src="https://github.com/user-attachments/assets/73fa906e-24c9-45fe-9e5e-42c2c89fa0df" width="250"/>
    </td>
  </tr>
</table>

---

## 🎯 Tujuan Pengembangan

Menyelesaikan submission akhir dengan menerapkan fitur-fitur sesuai standar pembelajaran, seperti:

- Halaman login dan register
- Menyimpan sesi login menggunakan DataStore
- Menampilkan daftar cerita dari API
- Menambah cerita dengan foto dan deskripsi
- Melihat detail cerita
- Logout yang menghapus data sesi
- Validasi input dan animasi dasar

---

## ✨Fitur Aplikasi

### Autentikasi
- Login dengan email dan password
- Register dengan nama, email, dan password
- Validasi password minimal 8 karakter langsung di EditText
- Validasi format email langsung di EditText
- Input password disembunyikan

### Manajemen Sesi
- Data token dan login disimpan di `DataStore`
- Jika sudah login, langsung diarahkan ke halaman utama
- Logout akan menghapus data sesi

### Daftar Cerita
- Menampilkan daftar cerita dari endpoint API
- Informasi yang ditampilkan:
  - Nama user (R.id.tv_item_name)
  - Foto (R.id.iv_item_photo)

### Detail Cerita
- Menampilkan informasi detail:
  - Nama (R.id.tv_detail_name)
  - Foto (R.id.iv_detail_photo)
  - Deskripsi (R.id.tv_detail_description)

### Tambah Cerita
- Upload gambar dari galeri
- Tambahkan deskripsi (R.id.ed_add_description)
- Kirim data ke server dengan tombol (R.id.button_add)
- Setelah sukses, kembali ke halaman list dan data muncul di paling atas

### Animasi
- Menggunakan **Shared Element Transition** saat berpindah dari list ke detail

---

## Teknologi yang Digunakan

- Kotlin
- MVVM Architecture
- Retrofit
- Glide
- ViewModel & LiveData
- DataStore
- ViewBinding

---

## Catatan Tambahan

Proyek ini masih terus saya kembangkan. Beberapa hal yang direncanakan:

- Menambahkan fitur upload dari kamera
- Loading dan error handling saat memanggil API
- Localization (multi bahasa)
- Navigasi yang lebih baik (tidak kembali ke login setelah berhasil login)

---

## Status Proyek

🛠️ Dalam pengembangan  
🗓️ Target selesai: Akhir Agustus 2025  
🎓 Tujuan: Submission kelas Mahir Dicoding

