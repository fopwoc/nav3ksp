plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.multiplatformLibrary) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.maven) apply false
}

allprojects {
    group = "io.github.fopwoc"
    version = providers.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0", "--always")
    }.standardOutput.asText.get().trim()
}
