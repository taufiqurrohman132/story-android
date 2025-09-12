package com.example.instogramapplication.utils
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

/**
 * Utilitas kelas untuk memberikan (grant) izin runtime secara otomatis
 * selama tes instrumentasi (UI Test) menggunakan UI Automator.
 *
 * Didesain untuk bekerja di berbagai versi API Android (sekitar 28 hingga 35+)
 * dengan menangani berbagai macam dialog izin yang mungkin muncul.
 */
object PermissionGranter {

    private const val TAG = "PermissionGranter"
    private const val DEFAULT_TIMEOUT = 5000L // Total waktu tunggu dalam milidetik
    private const val CHECK_INTERVAL = 1000L // Jeda antar pengecekan

    private val device: UiDevice by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    // Daftar selector untuk menemukan tombol "Allow" atau "Izinkan".
    // Urutan prioritas: dari yang paling spesifik ke yang paling umum.
    private val allowSelectors = listOf(
        // Berdasarkan Resource ID (paling andal jika tidak berubah)
        By.res("com.android.permissioncontroller:id/permission_allow_all_button"), // Tombol "Allow all" di Photo Picker
        By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
        By.res("com.android.permissioncontroller:id/permission_allow_always_button"),
        By.res("com.android.permissioncontroller:id/permission_allow_one_time_button"),
        By.res("com.android.permissioncontroller:id/permission_allow_button"),
        By.res("android:id/button1"),

        // Berdasarkan Teks (case-insensitive)
        // Teks Eksak (Berbagai variasi huruf)
        By.text("Allow all"),
        By.text("ALLOW ALL"),
        By.text("Izinkan semua"),
        By.text("While using the app"),
        By.text("Saat aplikasi digunakan"),
        By.text("Only this time"),
        By.text("Hanya kali ini"),
        By.text("Allow"),
        By.text("ALLOW"),
        By.text("Izinkan"),

        // Fallback ke textContains jika teks eksak tidak ditemukan
        By.textContains("Allow"),
        By.textContains("Izinkan")
    )

    /**
     * Fungsi utama yang akan dipanggil dari dalam tes.
     * Akan terus mencari dan mengklik tombol izin yang muncul hingga timeout.
     */
    fun allowPermissionsIfNeeded() {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < DEFAULT_TIMEOUT) {
            if (findAndClickButton()) {
                // Jika sebuah tombol berhasil diklik, beri sedikit jeda agar UI stabil
                Thread.sleep(CHECK_INTERVAL)
            } else {
                // Jika tidak ada tombol izin yang ditemukan, kita anggap selesai.
                Log.i(TAG, "Tidak ada dialog izin yang ditemukan. Melanjutkan tes.")
                return
            }
        }
    }

    private fun findAndClickButton(): Boolean {
        for (selector in allowSelectors) {
            val button = device.findObject(selector)
            if (button != null && button.isEnabled) {
                Log.i(TAG, "Dialog izin ditemukan dengan selector '$selector'. Mengklik tombol...")
                button.click()
                return true
            }
        }
        return false
    }
}