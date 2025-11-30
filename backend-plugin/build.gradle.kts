plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("build-jvm") {
            id = "build-jvm"
            implementationClass = "org.jep21s.messenger.core.plugins.BuildPluginJvm"
        }
        register("build-kmp") {
            id = "build-kmp"
            implementationClass = "org.jep21s.messenger.core.plugins.BuildPluginMultiplatform"
        }
        register("konvert") {
            id = "konvert"
            implementationClass = "org.jep21s.messenger.core.plugins.KonvertPlugin"
        }
        register("idea-plugin") {
            id = "idea-plugin"
            implementationClass = "org.jep21s.messenger.core.plugins.IdeaPlugin"
        }
        register("build-docker") {
            id = "build-docker"
            implementationClass = "org.jep21s.messenger.core.plugins.DockerPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.binaryCompatibilityValidator)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
