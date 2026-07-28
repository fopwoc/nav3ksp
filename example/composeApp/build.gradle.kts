import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    //alias(libs.plugins.androidApplication)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    //androidTarget()

//    android {
//        namespace = "$group.nav3ksp.example"
//        compileSdk = libs.versions.android.compileSdk.get().toInt()
//        minSdk = libs.versions.android.minSdk.get().toInt()
//
//        withJava()
//
//        compilerOptions {
//            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
//        }
//    }



    js {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.lifecycle.runtime)
                implementation(libs.compose.lifecycle.viewmodel)
                implementation(libs.compose.lifecycle.viewmodel.navigation3)
                implementation(libs.navigation3)
                implementation(libs.navigationevent)
                implementation(libs.serialization)

                implementation(projects.nav3ksp)
                implementation(projects.ksp.nav3kspAnnotation)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "$group.nav3ksp.example.MainKt"
    }
}

dependencies {
    add("kspCommonMainMetadata", projects.ksp.nav3kspProcessor)
}

ksp {
    arg("logLevel", "info")
}

tasks.withType<KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.named("compileKotlinJs") {
    dependsOn("kspCommonMainKotlinMetadata")
}

tasks.named("compileKotlinWasmJs") {
    dependsOn("kspCommonMainKotlinMetadata")
}
