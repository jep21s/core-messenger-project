plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))
  api("org.jep21s.messenger.core.libs:core-messenger-lib-logging-common")

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}