package com.example.instogramapplication.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.Description
import org.hamcrest.Matcher


fun selectBottomNavItem(@IdRes itemId: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = isDisplayed()
        override fun getDescription() = "Click on bottom navigation item"
        override fun perform(uiController: UiController?, view: View?) {
            val bottomNav = view as BottomNavigationView
            bottomNav.selectedItemId = itemId
        }
    }
}


fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView?): Boolean {
            val viewHolder = view?.findViewHolderForAdapterPosition(position)
            return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
        }
    }
}

