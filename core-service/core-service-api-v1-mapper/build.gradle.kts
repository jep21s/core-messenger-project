plugins {
  id("build-jvm")
  id("com.google.devtools.ksp") version "2.1.21-2.0.2"
  id("konvert")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation(projects.coreServiceApiV1)
  implementation(projects.coreServiceCommon)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}
