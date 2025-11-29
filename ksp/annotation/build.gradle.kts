import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.multiplatformLibrary)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "nav3ksp-annotation", version.toString())
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    androidLibrary {
        namespace = "$group.annotation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
            }
        }
    }

    js {
        outputModuleName = project.name
        binaries.library()
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = project.name
        binaries.library()
        browser()
        nodejs()
        d8()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Nav3KspAnnotaion"
            isStatic = true
        }
    }
}
