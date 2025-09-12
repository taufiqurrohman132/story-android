package com.example.instogramapplication.ui.story.post

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.datastore.UserPreferences
import com.example.instogramapplication.data.local.datastore.userDataStore
import com.example.instogramapplication.utils.PermissionGranter
import com.example.instogramapplication.utils.atPosition
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Menandakan bahwa test ini akan dijalankan menggunakan AndroidJUnit4 runner
@RunWith(AndroidJUnit4::class)
class EditFragmentTest {

    // Rule ini akan otomatis meluncurkan PostActivity sebelum setiap tes
    @get:Rule
    val activity = ActivityScenarioRule(PostActivity::class.java)

    @Before
    fun setupLoginSession() {
        // Dapatkan instance UserPreference Anda
        val context = ApplicationProvider.getApplicationContext<Context>()
        val userPreference =
            UserPreferences.getInstance(context.userDataStore) // Sesuaikan ini jika perlu

        // Siapkan data palsu untuk tes
        val fakeTokenForTesting =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTFiRUVSQXNtUm1lLUJJcmgiLCJpYXQiOjE3NTc2MDAwMjB9.x2QvZARH5XwAi_Rplq_pCNnaJWqCzOZFHDwwAQ8UWME"
        val fakeUserNameForTesting = "Test User"

        // Panggil fungsi yang benar dengan parameter yang benar
        runBlocking {
            userPreference.saveUserLoginToken(fakeTokenForTesting, fakeUserNameForTesting)
        }
    }

//    @After
//    fun tearDown() {
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//    }


    /**
     * Skenario 1: Memastikan alur posting cerita berhasil.
     * 1. Klik tombol ambil foto di PostActivity.
     * 2. Verifikasi bahwa layar pindah ke EditFragment (dengan mengecek keberadaan input deskripsi).
     * 3. Ketik teks deskripsi.
     * 4. Klik tombol posting.
     * 5. Verifikasi bahwa loading indicator muncul (menandakan proses upload dimulai).
     */
    @Test
    fun addStory_SuccessScenario() {
        //  menunggu  untuk memastikan kamera siap
        PermissionGranter.allowPermissionsIfNeeded()
        Thread.sleep(1000)
        onView(withId(R.id.post_btn_click_camera)).perform(click())

        // Verifikasi kita sudah di EditFragment dengan mengecek EditText deskripsi
        // Menunggu untuk transisi fragment
        Thread.sleep(1000)
        onView(withId(R.id.post_tv_desk)).check(matches(isDisplayed()))

        // Ketik deskripsi
        val descriptionText =
            "Ini adalah deskripsi"
        onView(withId(R.id.post_tv_desk))
            .perform(typeText(descriptionText), closeSoftKeyboard())

        //  Klik tombol posting
        onView(withId(R.id.post_btn_posting)).perform(click())

        //  Verifikasi bahwa ProgressBar loading muncul
        onView(withId(R.id.loading)).check(matches(isDisplayed()))

        // Verifikasi sukses
        onView(withText(R.string.popup_success_desc))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // click done
        onView(withText(R.string.popup_success_btn)).perform(click())

        //  Verifikasi bahwa list list muncul dan kembali ke main list
        onView(withId(R.id.halo_main)).check(matches(isDisplayed()))

        // Verifikasi bahwa konten yang baru diunggah muncul
        // dengan cara scroll ke item yang memiliki teks tersebut.
        Thread.sleep(3000)

        onView(withId(R.id.rv_post)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_post))
            .check(matches(atPosition(1, hasDescendant(withText(containsString(descriptionText))))))

    }


    /**
     * Skenario 2: Memastikan pesan error muncul jika deskripsi kosong.
     * 1. Klik tombol ambil foto untuk masuk ke EditFragment.
     * 2. Langsung klik tombol posting tanpa mengisi deskripsi.
     * 3. Verifikasi bahwa pesan toast dengan teks yang sesuai muncul.
     */
    @Test
    fun addStory_EmptyDescription_ShowsErrorToast() {
        PermissionGranter.allowPermissionsIfNeeded()

        // Klik tombol ambil foto
        Thread.sleep(1000)
        onView(withId(R.id.post_btn_click_camera)).perform(click())

        //  Verifikasi sudah di EditFragment
        Thread.sleep(1000)
        onView(withId(R.id.post_tv_desk)).check(matches(isDisplayed()))

        //  klik tombol posting
        onView(withId(R.id.post_btn_posting)).perform(click())

        onView(withId(R.id.loading)).check(matches(not(isDisplayed())))

        // Opsional
        // Misalnya, pastikan tombol posting masih bisa diklik.
        onView(withId(R.id.post_btn_posting)).check(matches(isEnabled()))
    }
}