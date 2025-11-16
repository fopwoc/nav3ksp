plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    sourceSets {
        jvmMain {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")

            dependencies {
                implementation(projects.lib)
                implementation(libs.navigation3)
                implementation(libs.compose.lifecycle.viewmodel)

                implementation(libs.ksp)
                implementation(libs.kotlinpoet)
                implementation(libs.kotlinpoet.ksp)
            }
        }
    }
}
