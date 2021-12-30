package com.example.jetcaster.buildsrc

object Versions {
    const val ktlint = "0.43.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.3"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.1.5"

    object Accompanist {
        const val version = "0.22.0-rc"
        const val insets = "com.google.accompanist:accompanist-insets:"
        const val pager = "com.google.accompanist:accompanist-pager:"
    }

    object Kotlin {
        private const val version = "1.6.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:"
    }

    object Coroutines {
        private const val version = "1.5.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:"
    }

    object OkHttp {
        private const val version = "4.9.1"
        const val okhttp = "com.squareup.okhttp3:okhttp:"
        const val logging = "com.squareup.okhttp3:logging-interceptor:"
    }

    object JUnit {
        private const val version = "4.13"
        const val junit = "junit:junit:"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.4.0"
        const val palette = "androidx.palette:palette:1.0.0"

        const val coreKtx = "androidx.core:core-ktx:1.7.0"

        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.4.0"
        }

        object Constraint {
            const val constraintLayoutCompose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-rc02"
        }

        object Compose {
            const val version = "1.1.0-rc01"
            const val foundation = "androidx.compose.foundation:foundation:"
            const val layout = "androidx.compose.foundation:foundation-layout:"

            const val ui = "androidx.compose.ui:ui:"
            const val material = "androidx.compose.material:material:"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:"

            const val tooling = "androidx.compose.ui:ui-tooling:"

            const val uiTest = "androidx.compose.ui:ui-test-junit4:"
            const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:"
        }

        object Lifecycle {
            private const val version = "2.4.0"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:"
        }

        object Material3 {
            const val version = "1.0.0-alpha02"

            const val material3 = "androidx.compose.material3:material3:"
        }

        object Navigation {
            const val navigation = "androidx.navigation:navigation-compose:2.4.0-rc01"
        }

        object Test {
            private const val version = "1.4.0"
            const val core = "androidx.test:core:"
            const val rules = "androidx.test:rules:"

            object Ext {
                private const val version = "1.1.2"
                const val junit = "androidx.test.ext:junit-ktx:"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        object Room {
            private const val version = "2.4.0"
            const val runtime = "androidx.room:room-runtime:"
            const val ktx = "androidx.room:room-ktx:"
            const val compiler = "androidx.room:room-compiler:"
        }

        object Window {
            const val window = "androidx.window:window:1.0.0-rc01"
        }
    }

    object Rome {
        private const val version = "1.14.1"
        const val rome = "com.rometools:rome:"
        const val modules = "com.rometools:rome-modules:"
    }

    object Coil {
        const val coilCompose = "io.coil-kt:coil-compose:1.4.0"
    }
}
