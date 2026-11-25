plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.ratcher"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.ratcher"
        minSdk = 24
        targetSdk = 36
<<<<<<< HEAD
        versionCode = 2
        versionName = "2.0"
=======
        versionCode = 1
        versionName = "1.0"
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

<<<<<<< HEAD
    // Firma del APK en modo release (llave de uso académico incluida en app/ratcher-release.jks)
    signingConfigs {
        create("release") {
            storeFile = file("ratcher-release.jks")
            storePassword = "ratcher2026"
            keyAlias = "ratcher"
            keyPassword = "ratcher2026"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
=======
    buildTypes {
        release {
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.core)
<<<<<<< HEAD
    // Consumo de la API de Xano
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
=======
    testImplementation(libs.junit)
>>>>>>> 07bee8b6740d28cb1a7d463b40455584c0cab57f
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}