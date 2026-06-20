plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.deymervilla.domain"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    //Modules
    implementation(projects.datasource)
    //Kotlin
    implementation(libs.core.ktx)
    //Hilt
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Paging
    implementation(libs.paging.runtime)
    //Test
    testImplementation(libs.junit)
    testImplementation(libs.paging.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //Robolectric
    testImplementation(libs.robolectric.test)
    //Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)
}