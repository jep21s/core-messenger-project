plugins {
  alias(libs.plugins.kotlin.jvm) apply false
}

group = "org.jep21s.messenger.core.libs"
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