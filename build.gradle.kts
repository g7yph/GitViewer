plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.secrets)
    alias(libs.plugins.sqldelight) apply false
    jacoco
}

/*
 * JaCoCo configs
 */

configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
}

val jacocoCoveredModulePaths = listOf(
    ":app",
    ":data"
)

tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    group = "verification"
    description = "Aggregate JaCoCo coverage from `connectedAndroidDeviceTest` " +
            "(KMP-library modules) and `connectedDebugAndroidTest` (:app) `.ec` files. " +
            "Independent of Kover (which covers host tests at build/reports/kover/). " +
            "Run after the connected test tasks finish."

    val coveredProjects = jacocoCoveredModulePaths.mapNotNull { rootProject.findProject(it) }

    executionData.setFrom(
        files(coveredProjects.map { p ->
            p.fileTree(p.layout.buildDirectory.dir("outputs/code_coverage")) {
                include("**/*.ec")
            }
        })
    )

    classDirectories.setFrom(
        files(coveredProjects.map { p ->
            p.fileTree(p.layout.buildDirectory) {
                // JaCoCo refuses already-instrumented bytecode. AGP-9 keeps *instrumented* copies at `intermediates/classes/debug/jacocoDebug/dirs/`
                // (for `:app`) and `.transforms/.../transformed/instrumented_classes/` (for KMP-library). The original bytecode the report needs lives
                // at the compiler outputs below.
                include(
                    // :app (com.android.application) — Kotlin compile output, pre-instrumentation.
                    "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes/**/*.class",
                    // :app — Java compile output, pre-instrumentation.
                    "intermediates/javac/debug/compileDebugJavaWithJavac/classes/**/*.class"
                )
                exclude(
                    listOf(
                        "**/di/**",
                        "**/generated/resources/**",
                        "**/*BuildConfig*",
                        "**/*ComposableSingletons*",
                        "**/*\$\$serializer*",
                        "**/R.class",
                        "**/R\$*.class",
                        "**/Manifest*.*",
                    )
                )
            }
        })
    )

    sourceDirectories.setFrom(
        files(coveredProjects.flatMap { p ->
            listOf(
                p.file("src/main/java"),
                p.file("src/main/kotlin")
            )
        })
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/androidTest/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/androidTest/report.xml"))
    }
}