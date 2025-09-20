# StoryApp – Proyek Submission Aplikasi Story

Aplikasi Android untuk submission kelas **Belajar Pengembangan Aplikasi Android Intermediate** dari [Dicoding](https://www.dicoding.com/).  
Story App memungkinkan pengguna untuk **register, login, melihat daftar cerita, menambah cerita baru dengan foto + deskripsi, melihat detail cerita, menampilkan peta lokasi cerita, serta logout**.  

---

## Cuplikan Layar

<table>
  <tr>
    <td align="center"><strong>Registration</strong></td>
    <td align="center"><strong>Home</strong></td>
    <td align="center"><strong>Detail</strong></td>
    <td align="center"><strong>Upload Story</strong></td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/73fa906e-24c9-45fe-9e5e-42c2c89fa0df" width="250" alt="Registration Page"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4ec9d426-aa2b-4838-8794-949391ab3392" width="250" alt="Home Page"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8d5473df-3207-4723-ac67-87f04b6a2fea" width="250" alt="Detail Page"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/64b9028c-a87c-4e4a-8c49-95f2124b121d" width="250" alt="Upload Story Page"/>
    </td>
  </tr>
</table>

<div align="center">
  <table>
    <tr>
      <td align="center"><strong>Language</strong></td>
      <td align="center"><strong>Stack Widget</strong></td>
    </tr>
    <tr>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/ee2e79e4-f068-4b87-83e5-384bd0984b9e" width="250" alt="Language Page"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/3c471f47-2ada-4e54-873d-bfa80db60618" width="250" alt="Stack Widget"/>
      </td>
    </tr>
  </table>
</div>


---

## ✨ Fitur Utama

| Kategori            | Fitur                                                                 |
|---------------------|----------------------------------------------------------------------|
| **Autentikasi**  | - Register akun (nama, email, password) <br> - Login akun (email, password) <br> - Password tersembunyi <br> - Custom `EditText` dengan validasi (password ≥ 8, email valid) <br> - Session Management dengan DataStore (auto login & logout hapus session/token) |
| **Daftar Cerita** | - Menampilkan list story (nama, foto) <br> - Klik item → detail story (nama, foto, deskripsi) |
| **Tambah Cerita** | - Upload foto (Gallery / Camera) <br> - Input deskripsi <br> - Tombol upload <br> - Setelah sukses, kembali & data baru muncul di atas |
| **Animasi**      | - Property Animation / Motion Animation / Shared Element <br> - Dicatat di Student Note |
| **Maps Integration** | - Halaman peta menampilkan story dengan lokasi (`?location=1`) <br> - Marker berisi nama & deskripsi |
| **Paging 3**      | - Mendukung infinite scroll (`page`, `size`) <br> - RemoteMediator untuk efisiensi data |
| 🧪 **Testing**       | - Unit test ViewModel Paging: <br> ✅ Berhasil load data <br> ✅ Data tidak null <br> ✅ Jumlah sesuai ekspektasi <br> ✅ Data pertama sesuai <br> ✅ Kosong → jumlah nol |

---

## 🛠️ Tech Stack
- Kotlin
- Android Jetpack
  - ViewModel
  - LiveData
  - DataStore
  - Paging 3
- Retrofit + OkHttp
- Google Maps API
- Glide
- Coroutine + Flow
- JUnit, Expresso, Mockito (Testing)

---

## 🚀 Cara Menjalankan Project
1. Clone repository:
   ```bash
   git clone https://github.com/username/story-android.git
   cd story-android

