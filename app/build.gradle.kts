plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlinx.kover)
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
        debug {
            enableAndroidTestCoverage = true
        }

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
    implementation(libs.coil.compose)
    implementation(libs.coil.network.client)
    implementation(libs.bundles.moko.fields)
    implementation(libs.bundles.markwon)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
}

/*
 * Kover configs
 */

dependencies {
    kover(project(":data"))
}

kover {
    reports {
        filters.excludes.androidGeneratedClasses()

        variant("release") {
            verify.rule {
                minBound(50)
            }

            filters.excludes {
                androidGeneratedClasses()
                classes(
                    // excludes debug classes
                    "*.DebugUtil"
                )
            }
        }

        total {
            additionalBinaryReports.addAll(
                providers.provider {
                    fileTree(rootProject.projectDir) {
                        include("**/outputs/code_coverage/**/*.ec")
                    }.files
                }
            )

            // 2. Настраиваем генерацию конкретных форматов
            xml { onCheck = true }
            html { title = "Отчет покрытия кода на всех уровнях" }
        }
    }
}