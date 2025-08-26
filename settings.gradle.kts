import java.util.Properties
import java.io.File

pluginManagement {
    val file = file("nexus.properties")
    if (file.exists()) {
        val props = java.util.Properties()
        file.inputStream().use { props.load(it) }
        repositories {
            maven { url = uri(props.getProperty("repository_google")) }
            maven { url = uri(props.getProperty("repository_mavenCentral")) }
            maven { url = uri(props.getProperty("repository_jitpackIo")) }
        }
    } else {
        repositories {
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    val file = file("nexus.properties")
    if (file.exists()) {
        val props = java.util.Properties()
        file.inputStream().use { props.load(it) }
        repositories {
            maven { url = uri(props.getProperty("repository_google")) }
            maven { url = uri(props.getProperty("repository_mavenCentral")) }
            maven { url = uri(props.getProperty("repository_jitpackIo")) }
        }
    } else {
        repositories {
            google()
            mavenCentral()
        }
    }

    repositories {
        maven { url = uri("repo") }
    }
}

rootProject.name = "LiqpaySdkAos"
include(":sample")
