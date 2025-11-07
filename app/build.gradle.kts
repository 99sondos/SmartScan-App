import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

// Helper function to safely get a property from local.properties
fun getApiKey(property: String): String {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    } else {
        throw GradleException("local.properties not found. Please add it and sync Gradle.")
    }
    return localProperties.getProperty(property)?.trim('"') ?: throw GradleException("$property not found in local.properties. Please add it and sync Gradle.")
}

android {
    namespace = "com.app.smartscan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.smartscan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Get the key using the new, safe helper function
        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"" + getApiKey("OPENAI_API_KEY") + "\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" 
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "USE_EMULATORS", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "USE_EMULATORS", "false")
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Firebase Dependencies
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("com.google.firebase:firebase-analytics-ktx:22.5.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.google.firebase:firebase-functions-ktx:21.2.1")
    
    // Core Android, Compose, and Coroutines from backend and AI team branches (using latest versions)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.compose.ui:ui:1.7.0-beta01")
    implementation("androidx.compose.material3:material3:1.3.0-beta01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0-beta01")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0-beta01")
    implementation("androidx.compose.material:material-icons-core:1.7.0-beta01")
    implementation("androidx.compose.material:material-icons-extended:1.7.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.8.0-beta03")

    // CameraX and ML Kit dependencies from the main branch
    val camerax_version = "1.3.4"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("androidx.camera:camera-video:$camerax_version")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Networking libraries from the main branch
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Unit testing libraries from BOTH branches
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // Instrumented testing libraries from backend and AI branches
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.0-beta01")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0-beta01")
}
