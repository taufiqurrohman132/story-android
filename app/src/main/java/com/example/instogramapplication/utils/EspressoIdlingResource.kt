package com.example.instogramapplication.utils

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = androidx.test.espresso.idling.CountingIdlingResource(RESOURCE)

    fun increment() = countingIdlingResource.increment()

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment()
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement()
    }
}

