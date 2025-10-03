plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //   custom plugins
    // super core for kotlin serialization
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    //hilt
    alias(libs.plugins.hilt)
}

android {
    namespace = "app.vercel.danfelogarporfolios.ktworkmanagertasks"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.vercel.danfelogarporfolios.ktworkmanagertasks"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Custom dependencies
    //work manager
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.gcm)
    implementation(libs.work.multiprocess)
    //dagger-hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.navigation)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    //retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)
    //accompanist-permissions
    implementation(libs.accompanist.permissions)
    //lifecycle and viewmodel
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.savedstate)
    ksp(libs.lifecycle.compiler)
    //runtime
    implementation(libs.runtime)
    implementation(libs.runtime.livedata)
    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    //room-db
    implementation(libs.androidx.room.db)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}