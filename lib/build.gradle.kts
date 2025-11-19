import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.multiplatformLibrary)
    alias(libs.plugins.compose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "nav3ksp", version.toString())
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    androidLibrary {
        namespace = "$group.nav3ksp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Nav3Ksp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.navigation3)
            api(libs.serialization)

            api(projects.ksp.annotation)
        }
    }
}
