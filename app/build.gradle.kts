plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ca3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ca3"
        minSdk = 31
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


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}



dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // HttpsURLConnection/API
    implementation(libs.volley)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.cronet.embedded)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // Firebase
    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)
    implementation (libs.firebase.storage.ktx)
    implementation (libs.firebase.messaging.ktx)

    // Google Maps and Places
    implementation (libs.gms.play.services.maps.v1810)
    implementation (libs.play.services.location.v2101)
    implementation (libs.places)
    implementation (libs.google.android.maps.utils)

    // CameraX
    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.camera.view)

    //Compress
    implementation ("id.zelory:compressor:2.1.1")
    implementation ("io.reactivex.rxjava2:rxandroid:2.0.1")

    // Glide for Image Loading
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    // Room for Offline Caching
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    // Hilt for Dependency Injection
    implementation (libs.hilt.android)

    // MPAndroidChart for Data Visualization
    implementation (libs.mpandroidchart)


    // Coroutines for Asynchronous Operations
    implementation (libs.kotlinx.coroutines.android)
    implementation(libs.firebase.firestore)

    //Recycler View
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}