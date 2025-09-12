plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.instogramapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.instogramapplication"
        minSdk = 28
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mapOf(
            "clearPackageData" to "true"
        )

        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
//        buildConfigField("boolean", "TEST_MODE", "false")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true // requirement pop up
    }
    buildTypes {
//        debug {
//            buildConfigField("boolean", "TEST_MODE", "true")
//        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            buildConfigField("boolean", "TEST_MODE", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        animationsDisabled = true
    }
}

dependencies {

    // upload to server
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.logging.interceptor)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.lifecycle.livedata.ktx)
    // Lifecycles only (without ViewModel or LiveData)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Data store
    implementation(libs.androidx.datastore.preferences)

    // glide
    implementation(libs.glide)

    // camerax
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)

    // bottom nav
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // langue
    implementation(libs.lingver)

    implementation(libs.photoview)

    implementation(libs.flexbox)

    // paging 3 n room
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.espresso.idling.resource)
//    implementation(libs.androidx.uiautomator)
    testImplementation(libs.junit.jupiter)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // loading
    implementation(libs.igrefreshlayout)
    implementation(libs.shimmer)

    // floating multiple option menu
    implementation("com.github.clans:fab:1.6.4")

    implementation(libs.lottie)

    // loading circular
    implementation(libs.android.nested.progress)

    // pop up
    implementation(libs.popup.dialog)

    // expanable teks
    implementation(libs.expandabletextview)

    // splash android
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.core.testing) //InstantTaskExecutorRule
    androidTestImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1") //TestDispatcher
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("org.mockito:mockito-android:5.12.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
//    androidTestImplementation("androidx.test.espresso:idling-resource:3.6.1")

    testImplementation(libs.androidx.core.testing) // InstantTaskExecutorRule
    testImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
}