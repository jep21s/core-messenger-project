plugins {
  id("build-jvm")
  id("idea-plugin")
  id("build-docker")
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
  implementation(projects.coreServiceRepoCassandra)

  testImplementation(libs.bundles.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.ktor.client.negotiation)

  testImplementation(testFixtures("org.jep21s.messenger.core.libs:core-messenger-lib-test-common"))

}

tasks {
  shadowJar {
    isZip64 = true
    // Явно указываем, что нужно включать все зависимости
    configurations = listOf(project.configurations.runtimeClasspath.get())
    mergeServiceFiles()

    manifest {
      attributes["Main-Class"] = "org.jep21s.messenger.core.service.app.web.ApplicationKt"
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }


  // Если ошибка: "Entry application.yaml is a duplicate but no duplicate handling strategy has been set."
  withType(ProcessResources::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }

  dockerBuild {
    doFirst {
      copy {
        from("Dockerfile") //.rename { "Dockerfile" }
        from("build/libs/core-service-app-web-all.jar")
        println("BUILD CONTEXT: ${buildContext.get()}")
        into(buildContext)
      }
    }
  }
}

docker {
  buildContext = project.layout.buildDirectory.dir("docker-x64").get().toString()
  imageName = "${project.name}-x64"
  dockerFile = "Dockerfile"
//  imageTag = "${project.version}"
  imageTag = "0.0.5"
}


tasks.test {
  jvmArgs = listOf(
    "--add-opens=java.base/java.time=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.math=ALL-UNNAMED"
  )
  useJUnitPlatform()
}