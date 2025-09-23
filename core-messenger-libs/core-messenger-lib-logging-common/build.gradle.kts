plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib-common"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(libs.kotlinx.datetime)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}
