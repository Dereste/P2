plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mapcalc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mapcalc"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation (libs.com.appolica.interactive.info.window.android)
    implementation(libs.volley)
    implementation (libs.apollographql.apollo.api)
    implementation (libs.apollographql.apollo.android.support)
    implementation (libs.apollographql.apollo.runtime)
    implementation(libs.okhttp)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation (libs.android.maps.utils)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}