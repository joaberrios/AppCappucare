plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.appcappucare"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.appcappucare"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

apply(plugin = "jacoco")

tasks.register<org.gradle.testing.jacoco.tasks.JacocoReport>("reporteCobertura") {
    dependsOn("testDebugUnitTest")

    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("jacoco/testDebugUnitTest.exec")
        }
    )

    sourceDirectories.setFrom(
        files("src/main/java")
    )

    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
            include("com/example/appcappucare/model/**")
            include("com/example/appcappucare/utils/ValidadorProducto*.class")
            include("com/example/appcappucare/repository/**")
            include("com/example/appcappucare/viewmodel/**")
            exclude("**/R.class", "**/R$*.class")
        }
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
