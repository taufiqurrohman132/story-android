// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    //noinspection GradleDependency
    id("androidx.navigation.safeargs") version "2.7.7" apply false
    //noinspection GradleDependency
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false // tambahkan ini
}