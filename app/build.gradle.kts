plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.icerock.gitviewer"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "dev.icerock.gitviewer"
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.datetime)

    implementation(libs.bundles.android.ui)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.android.compose.ui)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.bundles.moko.fields)
    implementation(libs.bundles.moko.permissions)
    implementation(libs.bundles.markwon)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.bundles.android.ui.test)
}