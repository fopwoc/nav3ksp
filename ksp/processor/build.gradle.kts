plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "nav3ksp-processor", version.toString())
}

kotlin {
    jvm()
    jvmToolchain(libs.versions.java.get().toInt())

    sourceSets {
        jvmMain {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")

            dependencies {
                implementation(projects.nav3ksp)
                implementation(projects.ksp.nav3kspAnnotation)

                implementation(libs.navigation3)
                implementation(libs.compose.lifecycle.viewmodel)

                implementation(libs.ksp)
                implementation(libs.kotlinpoet)
                implementation(libs.kotlinpoet.ksp)
            }
        }
    }
}
