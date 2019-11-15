plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
}

repositories {
    jcenter()
    google()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
}

group = "com.mybusiness"
version = "1.0-SNAPSHOT"

kotlin {
    jvm("android") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js { browser() }

    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "AddressBookCommon"
            }
        }
    }

    sourceSets {
        // Versions
        val coroutinesVersion = "1.3.1"
        val ktorVersion = "1.2.4"
        val serializationVersion = "0.13.0"
        val mockkVersion = "1.9.3"

        // Shortcuts
        fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
        fun coroutines(module: String = "") = kotlinx("coroutines-core$module", coroutinesVersion)
        fun serialization(module: String = "") = kotlinx("serialization-runtime$module", serializationVersion)
        fun ktorClient(module: String, version: String = ktorVersion) = "io.ktor:ktor-client-$module:$version"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation(coroutines("-common"))
                implementation(serialization("-common"))

                implementation(ktorClient("core"))
                implementation(ktorClient("json"))
                implementation(ktorClient("serialization"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation(coroutines())
                implementation(serialization())

                implementation(ktorClient("core-jvm"))
                implementation(ktorClient("json-jvm"))
                implementation(ktorClient("serialization-jvm"))
                implementation(ktorClient("apache"))
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(coroutines("-native"))
                implementation(serialization("-native"))

                implementation(ktorClient("core-native"))
                implementation(ktorClient("ios"))
                implementation(ktorClient("json-native"))
                implementation(ktorClient("serialization-native"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(coroutines("-js"))
                implementation(serialization("-js"))

                implementation(ktorClient("core-js"))
                implementation(ktorClient("js"))
                implementation(ktorClient("json-js"))
                implementation(ktorClient("serialization-js"))
            }
        }
    }
}

val iosTest: Task by tasks.creating {
    val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
    val testExecutable = kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("ios").binaries.getTest("DEBUG")
    dependsOn(testExecutable.linkTaskName)
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs tests for target 'ios' on an iOS simulator"

    doLast {
        exec {
            println(testExecutable.outputFile.absolutePath)
            commandLine("xcrun", "simctl", "spawn", "--standalone", device, testExecutable.outputFile.absolutePath)
        }
    }
}

tasks.getByName("allTests").dependsOn(iosTest)

val packForXcode by tasks.creating(Sync::class) {
    /// selecting the right configuration for the iOS
    /// framework depending on the environment
    /// variables set by Xcode build
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
        .getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("ios")
        .binaries.getFramework(mode)
    inputs.property("mode", mode)

    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)