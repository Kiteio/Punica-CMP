import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.appdirs)
            implementation(libs.cryptography.provider.jdk)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.otp)
            implementation(libs.startup.runtine)
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.alertKmp)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.composeIcon.css)
            implementation(libs.composeIcon.simple)
            implementation(libs.composeIcon.tabler)
            implementation(libs.composeSettings.ui)
            implementation(libs.cryptography.core)
            implementation(libs.datastore)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ksoup)
            implementation(libs.ktor.client.content.negotitation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.material3.windowSizeClass)
            implementation(libs.materialkolor)
            implementation(libs.navigation.compose)
            implementation(libs.paging.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.appdirs)
            implementation(libs.cryptography.provider.jdk)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.otp)
        }
        iosMain.dependencies {
            implementation(libs.cryptography.provider.apple)
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "org.kiteio.punica"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.kiteio.punica"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.punica.versionCode.get().toInt()
        versionName = libs.versions.punica.versionName.get()
    }
    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    applicationVariants.all {
        buildOutputs.all {
            if (this is ApkVariantOutputImpl) {
                // punica-[Platform]-[VersionName].apk
                outputFileName =
                    "punica-android-${defaultConfig.versionName}.apk"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.kiteio.punica.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.kiteio.punica"
            packageVersion = libs.versions.punica.versionName.get()
        }
    }
}


buildkonfig {
    packageName = "org.kiteio.punica"
    objectName = "Build"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "appName", "Punica")
        buildConfigField(FieldSpec.Type.STRING, "versionName", libs.versions.punica.versionName.get())
        buildConfigField(FieldSpec.Type.STRING, "organization", "Kiteio")
    }
}