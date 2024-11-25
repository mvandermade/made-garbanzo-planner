plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register<Copy>("copyPreCommitHook") {
    description = "Copy pre-commit git hook from the scripts to the .git/hooks folder."
    group = "githooks"
    outputs.upToDateWhen { false }
    filePermissions {
        user {
            read = true
            execute = true
        }
        other.execute = false
    }
    from("$rootDir/scripts/pre-commit")
    into("$rootDir/.git/hooks/")
}
