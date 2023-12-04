plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.financify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.financify"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("io.finnhub:kotlin-client:2.0.20")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.code.gson:gson:2.8.5")
    implementation ("com.github.travijuu:numberpicker:1.0.7")
    implementation("androidx.transition:transition-ktx:1.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.github.polygon-io:client-jvm:v5.1.0")

    implementation ("androidx.compose.ui:ui:1.2.0-alpha04")
    implementation ("androidx.compose.foundation:foundation:1.2.0-alpha04")
    implementation ("androidx.compose.material:material:1.2.0-alpha04")
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    // For AppWidgets support
    implementation ("androidx.glance:glance-appwidget:1.0.0")
    // For interop APIs with Material 2
    implementation ("androidx.glance:glance-material:1.0.0")

    // For interop APIs with Material 3
    implementation ("androidx.glance:glance-material3:1.0.0")


    // Room components
    val room_version = "2.6.0"
    val lifecycle_version = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx: $lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")


}