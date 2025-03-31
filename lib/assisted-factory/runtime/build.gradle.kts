import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublish)
}

group = "com.plusmobileapps.kotlin.inject.anvil.extensions.runtime"
version = libs.versions.assistedFactory.get()

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KotlinInjectAnvilDecompose"
            isStatic = true
        }
    }


    jvm("desktop")

    applyDefaultHierarchyTemplate()
}

android {
    namespace = "com.plusmobileapps.kotlin.inject.anvil.runtime"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    commonMainImplementation(libs.kotlin.inject.core.runtime)
    commonMainImplementation(libs.kotlin.inject.anvil.runtime)
    commonMainImplementation(libs.arkivanov.decompose.core)
}

mavenPublishing {
    coordinates("com.plusmobileapps.kotlin-inject-anvil-extensions", "assisted-factory-runtime", libs.versions.assistedFactory.get())

    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    pom {
        name.set("Kotlin Inject Anvil Extensions - Assisted Factory Runtime")
        description.set("Generate bindingds for assisted factory interfaces.")
        inceptionYear.set("2025")
        url.set("https://github.com/plusmobileapps/kotlin-inject-anvil-extensions/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("plusmobileapps")
                name.set("Plus Mobile Apps LLC")
                url.set("https://github.com/plusmobileapps/")
            }
        }
        scm {
            url.set("https://github.com/plusmobileapps/kotlin-inject-anvil-extensions/")
            connection.set("scm:git:git://github.com/plusmobileapps/kotlin-inject-anvil-extensions.git")
            developerConnection.set("scm:git:ssh://git@github.com/plusmobileapps/kotlin-inject-anvil-extensions.git")
        }
    }
}

val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}
