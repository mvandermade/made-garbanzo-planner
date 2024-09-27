import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.apache.pdfbox:pdfbox:3.0.3")
            implementation("org.dmfs:lib-recur:0.17.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("io.mockk:mockk:1.13.12")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        desktopTest.dependencies {
            implementation("org.jetbrains.compose.ui:ui-test-junit4:1.6.11")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                iconFile.set(project.file("icon/icon.icns"))
            }
            windows {
                iconFile.set(project.file("icon/icon.ico"))
            }
            linux {
                iconFile.set(project.file("icon/icon.png"))
            }
        }

        buildTypes.release.proguard {
            // false because of "can't find referenced class"
            isEnabled.set(false)
            obfuscate.set(false)
            // Add this line
            configurationFiles.from(project.file("compose-desktop.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "made-gp"
            packageVersion = "1.0.4"
        }
    }
}
