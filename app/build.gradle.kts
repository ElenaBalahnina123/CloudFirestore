plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.slobozhaninova.cloudfirestore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.slobozhaninova.cloudfirestore"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation (libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation (libs.lifecycle.viewmodel.compose)
    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.material.icons.extended)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.navigation.ui.ktx)
    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.ui.tooling.preview)
    debugImplementation (libs.ui.tooling)
    implementation (libs.androidx.navigation.compose)
    implementation (libs.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation (libs.hilt.android)
    implementation(libs.work.runtime)

    ksp(libs.androidx.hilt.compiler)
    ksp (libs.dagger.hilt.compiler)

}