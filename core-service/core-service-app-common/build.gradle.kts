plugins {
  id("build-jvm")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-common")
  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-logback")
  implementation(projects.coreServiceApiV1)
  implementation(projects.coreServiceApiV1Mapper)
  implementation(projects.coreServiceCommon)
  implementation(projects.coreServiceBiz)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}