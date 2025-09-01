plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

group = "org.jep21s.messenger.core.service"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}

ext {
    val specDir = layout.projectDirectory.dir("../specs")
    set("spec-cs-v1", specDir.file("specs-core-service-v1.yaml").toString())
}
