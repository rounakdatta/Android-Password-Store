/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Plugins.agp)
        classpath(Plugins.kotlin)
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.30.0"
}

subprojects {
    repositories {
        google()
        jcenter()
        maven {
          setUrl("https://jitpack.io")
        }
    }
    if (name == "app") {
        apply(plugin = "com.android.application")
    } else {
        apply(plugin = "com.android.library")
    }
    configure<BaseExtension> {
        compileSdkVersion(29)
        defaultConfig {
            minSdkVersion(23)
            targetSdkVersion(29)
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        tasks.withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:unchecked")
            options.isDeprecation = true
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xallow-result-return-type")
            languageVersion = "1.4"
        }
    }
}

tasks.wrapper {
    gradleVersion = "6.6.1"
    distributionType = Wrapper.DistributionType.ALL
    distributionSha256Sum = "11657af6356b7587bfb37287b5992e94a9686d5c8a0a1b60b87b9928a2decde5"
}
