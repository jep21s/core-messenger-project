plugins {
  id("build-jvm")
  id("java-test-fixtures")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib-common"))
  implementation(kotlin("stdlib-jdk8"))

  testFixturesCompileOnly(libs.bundles.jackson)
}

repositories {
  mavenCentral()
}