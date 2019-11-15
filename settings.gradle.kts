val kotlinVersion = "1.3.50"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.multiplatform" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                "org.jetbrains.kotlin.plugin.serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
            }
        }
    }

    repositories {
        jcenter()
        google()
//        maven(url = "https://dl.bintray.com/salomonbrys/gradle-plugins")
//        maven(url = "https://plugins.gradle.org/m2/")
//        mavenLocal()
    }
}

enableFeaturePreview("GRADLE_METADATA")