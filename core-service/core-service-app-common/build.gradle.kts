plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))

  api("org.jep21s.messenger.core.libs:core-messenger-lib-logging-common")
  implementation(projects.coreServiceApiV1)
  implementation(projects.coreServiceApiV1Mapper)
  implementation(projects.coreServiceCommon)
  implementation(projects.coreServiceBiz)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}