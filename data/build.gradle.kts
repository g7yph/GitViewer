plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "dev.icerock.gitviewer.data"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 23

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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "secrets.defaults.properties"

    ignoreList.add("sdk.*")
}

sqldelight {
    databases {
        register("GitHubDatabase") {
            packageName.set("dev.icerock.gitviewer.data")
        }
    }
}

dependencies {
    api(libs.kotlinx.coroutines.android)
    api(libs.retrofit.core)
    api(libs.androidx.paging.common)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.sqldelight.driver.android)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.paging)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.sqldelight.driver.jvm)
    androidTestImplementation(libs.bundles.android.test)
}