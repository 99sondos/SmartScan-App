plugins {

    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")

}

repositories {
    google()
    mavenCentral()
}

android {
    namespace = "com.app.smartscan"
    compileSdk = 34   // keep 34 until Android 15 SDK is installed

    defaultConfig {
        applicationId = "com.app.smartscan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    buildFeatures { compose = true }
    // No composeOptions: kotlin 2.0 + compose plugin provides the compiler
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.0.2")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // AndroidX / Compose (via version catalog)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    //  JUnit – vanliga unit tester
    testImplementation(libs.junit)

    //  Robolectric – för att testa Android-klasser (Bitmap, Context) utan emulator
    testImplementation("org.robolectric:robolectric:4.11.1")

    //  Android instrumentation + UI tester
    androidTestImplementation(libs.androidx.junit)               // Android test runner
    androidTestImplementation(libs.androidx.espresso.core)       // Espresso (UI test framework)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose version sync
    androidTestImplementation(libs.androidx.ui.test.junit4)      // Compose UI test rule & matchers

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")


    // Behövs för Compose UI tester i debug builds
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // CameraX
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")

    // Compose Activity integration
    implementation("androidx.activity:activity-compose:1.9.3")

    //  lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // Required to read EXIF orientation metadata for captured images
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Enables access to extended Material icons (Camera, Image, Check, Refresh, etc.)
    //implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-icons-extended")




}
