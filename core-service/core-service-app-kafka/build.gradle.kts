plugins {
  application
  id("build-jvm")
}

application {
  mainClass.set("org.jep21s.messenger.core.service.app.kafka.MainKt")
}


dependencies {
  implementation(libs.kafka.client)
  implementation(libs.bundles.kotlinx.coroutines)
  implementation(libs.kotlinx.atomicfu)
  implementation(libs.bundles.jackson)

  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-common")
  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-logback")

  implementation(projects.coreServiceApiV1)
  implementation(projects.coreServiceApiV1Mapper)
  implementation(projects.coreServiceCommon)
  implementation(projects.coreServiceBiz)
  implementation(projects.coreServiceAppCommon)

  testImplementation(libs.bundles.junit)
}
