plugins {
  id("build-jvm")
  id("idea-plugin")
  alias(libs.plugins.ktor)
}

kotlin {
  jvmToolchain(21)
}

application {
  mainClass.set("org.jep21s.messenger.core.service.app.web.ApplicationKt")
}

dependencies {
  implementation(kotlin("stdlib"))

  //web-server
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.netty)
  //serialize
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.serialization.jackson)
  //exception to http status
  implementation(libs.ktor.server.host.common)
  implementation(libs.ktor.server.status.pages)
  //add headers to response
  implementation(libs.ktor.server.default.headers)
  //double receive request body
  implementation(libs.ktor.server.double.receive)

  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-common")
  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-logback")
  implementation(projects.coreServiceApiV1)
  implementation(projects.coreServiceApiV1Mapper)
  implementation(projects.coreServiceCommon)
  implementation(projects.coreServiceBiz)
  implementation(projects.coreServiceAppCommon)
  implementation(projects.coreServiceRepoInmemory)

  testImplementation(libs.bundles.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.ktor.client.negotiation)

  testImplementation(testFixtures("org.jep21s.messenger.core.libs:core-messenger-lib-test-common"))

}

tasks.test {
  jvmArgs = listOf(
    "--add-opens=java.base/java.time=ALL-UNNAMED"
  )
  useJUnitPlatform()
}