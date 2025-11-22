plugins {
  id("build-jvm")
  id("com.google.devtools.ksp") version "2.1.21-2.0.2"
  id("konvert")
  id("kotlin-kapt")
  id("idea-plugin")
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.jdk9)
  implementation(libs.bundles.cassandra)
  kapt(libs.db.cassandra.kapt)

  implementation(libs.liquibase.core)
  implementation(libs.liquibase.cassandra)
  implementation("com.ing.data:cassandra-jdbc-wrapper:4.16.1")

  implementation("org.jep21s.messenger.core.libs:core-messenger-lib-logging-logback")
  implementation(libs.bundles.jackson)

  implementation(projects.coreServiceCommon)
  api(projects.coreServiceRepoCommon)

  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.testcontainers)
  testImplementation(libs.testcontainers.cassandra)
  testImplementation(libs.mockk)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(testFixtures(projects.coreServiceRepoCommon))
}

tasks.test {
  jvmArgs = listOf(
    "--add-opens=java.base/java.time=ALL-UNNAMED"
  )
  useJUnitPlatform()
}