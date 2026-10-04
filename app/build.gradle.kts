import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// ← Paste it here, at the top level of the file
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.1.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    }
}

android {
    namespace = "com.example.modelviewer"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.modelviewer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { compose = true }
    android {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

//    kotlinOptions {
//        jvmTarget = "17"
//    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:2.4.10")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
 //   implementation(platform("androidx.compose:compose-bom:2025.09.00"))
    implementation(
        enforcedPlatform("androidx.compose:compose-bom:2025.09.00")
    )
    implementation("io.github.sceneview:sceneview:4.51.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")
    implementation("com.google.mlkit:common:18.11.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
