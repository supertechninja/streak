pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Streak"

include(":app")
