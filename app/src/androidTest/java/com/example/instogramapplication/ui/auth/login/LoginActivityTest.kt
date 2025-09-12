package com.example.instogramapplication.ui.auth.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.instogramapplication.R
import com.example.instogramapplication.utils.EspressoIdlingResource
import com.example.instogramapplication.utils.selectBottomNavItem
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogout() {
        // input email
        onView(withId(R.id.login_edt_email))
            .perform(typeText("taufiqurrohman132109@gmail.com"), closeSoftKeyboard())

        // input pass
        onView(withId(R.id.login_edt_pass))
            .perform(typeText("12345678"), closeSoftKeyboard())

        // klik login
        onView(withId(R.id.login_btn_login))
            .perform(click())

        // klik tombol OK di dialog sukses
        onView(withText(R.string.popup_success_btn)).perform(click())

        // Verifikasi masuk ke MainActivity (cek tulisan Welcome)
        onView(withId(R.id.halo_main)).check(matches(isDisplayed()))

        // navigate ke settings
        onView(withId(R.id.nav_view)).perform(
            selectBottomNavItem(R.id.navigation_profile)
        )

        // Klik tombol logout
        onView(withId(R.id.setting_btn_logout)).perform(scrollTo(), click())

        onView(withText(R.string.dialog_exit_edit_yes)).perform(click())

        // Pastikan balik ke halaman login
        onView(withId(R.id.login_btn_login)).check(matches(isDisplayed()))

    }

}