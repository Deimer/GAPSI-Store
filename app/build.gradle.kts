plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.deymervilla.gapsistore"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.deymervilla.gapsistore"
        minSdk = 31
        targetSdk = 36
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Modules
    implementation(projects.domain)
    implementation(projects.ds)
    //Kotlin
    implementation(libs.androidx.core.ktx)
    //Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    //DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Compose
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.constraintlayout)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.icons.core)
    //Navigation
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.navigation.compose)
    //Paging
    implementation(libs.paging.compose)
    //Image
    implementation(libs.coil)
    //Tests Compose
    debugImplementation(libs.compose.test.manifest)
    androidTestImplementation(libs.test.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(platform(libs.compose.bom))
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.archCore.testing)
    //Robolectric
    testImplementation(libs.robolectric.test)
    //Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)
}