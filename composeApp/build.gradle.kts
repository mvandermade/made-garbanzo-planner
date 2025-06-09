import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    id("com.github.ben-manes.versions") version "0.52.0"
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.apache.pdfbox:pdfbox:3.0.5")
            implementation("org.dmfs:lib-recur:0.17.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        desktopTest.dependencies {
            implementation("org.jetbrains.compose.ui:ui-test-junit4:1.8.1")
            implementation("io.mockk:mockk:1.14.2")
            implementation("nl.wykorijnsburger.kminrandom:kminrandom:1.0.4")

            implementation("org.junit.jupiter:junit-jupiter-api:5.13.1")
            implementation("org.junit.jupiter:junit-jupiter-params:5.13.1")
            implementation("org.junit.jupiter:junit-jupiter-engine:5.13.1")
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
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "made-gp"
            packageVersion = "1.0.7"
        }
    }
}
