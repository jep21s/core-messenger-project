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
  implementation(libs.coroutines.core)

  implementation(projects.coreServiceCommon)

  testImplementation(libs.bundles.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.kotlinx.coroutines.test)
}

tasks.test {
  useJUnitPlatform()
}