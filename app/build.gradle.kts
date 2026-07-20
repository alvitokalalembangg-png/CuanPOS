plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cuanpos"

    // Diubah menjadi versi 37 yang lebih simpel dan didukung library terbaru
    compileSdk = (37)

    defaultConfig {
        applicationId = "com.example.cuanpos"
        minSdk = (24)
        targetSdk = (37) // Disamakan ke 37 agar sinkron
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
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    // Retrofit & JSON Parsing
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for logging API responses in Logcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines for asynchronous network calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}