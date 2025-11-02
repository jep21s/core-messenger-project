plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib-common"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(libs.coroutines.core)

  testImplementation(libs.bundles.junit)
  implementation(libs.coroutines.test)
}

tasks.test {
  useJUnitPlatform()
}
