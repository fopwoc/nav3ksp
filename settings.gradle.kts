rootProject.name = "nav3ksp-lib"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(
    ":lib",
    ":ksp:annotation",
    ":ksp:processor",
    ":example:composeApp"
)

project(":lib").name = "nav3ksp"
project(":ksp:annotation").name = "nav3ksp-annotation"
project(":ksp:processor").name = "nav3ksp-processor"
