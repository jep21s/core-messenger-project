pluginManagement {
    includeBuild("../backend-plugin")
    plugins {
        id("build-jvm") apply false
        id("build-kmp") apply false
        id("konvert") apply false
        id("idea-plugin") apply false
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

// Включает вот такую конструкцию
//implementation(projects.m2l5Gradle.sub1.ssub1)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "core-service"
include("core-service-api-v1")
include("core-service-common")
include("core-service-api-v1-mapper")
include("core-service-app-web")
include("core-service-biz")
include("core-service-app-kafka")
include("core-service-app-common")
include("core-service-repo-common")
include("core-service-repo-inmemory")
include("core-service-repo-cassandra")