plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mcwilliams.streak"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    namespace = "com.mcwilliams.streak"
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    debugImplementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.workmanager)
    implementation(libs.androidx.glance)
    implementation(libs.android.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.hilt.android)

    implementation(libs.compose.hilt.navigation)
    implementation(libs.compose.navigation)

    implementation(libs.accompanist.webview)
    implementation(libs.accompanist.swiperefresh)

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)

    testImplementation(libs.junit)

//    implementation("net.openid:appauth:0.11.1")

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit)
    androidTestImplementation(libs.hilt.android.testing)

    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.square.leakcanary)

    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    kapt(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)

    annotationProcessor(libs.androidx.room.compiler)
}