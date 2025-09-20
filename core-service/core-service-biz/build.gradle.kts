plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(libs.kotlin.reflect)
  implementation(libs.bundles.kotlinx.coroutines)

  implementation(projects.coreServiceCommon)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}