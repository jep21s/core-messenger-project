plugins {
  id("build-jvm")
  alias(libs.plugins.openapi.generator)
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(libs.jackson.kotlin)
  implementation(libs.jackson.datatype)

  testImplementation(libs.bundles.junit)
}

tasks.test {
  useJUnitPlatform()
}

sourceSets {
  main {
    java.srcDir(layout.buildDirectory.dir("generate-resources/main/src/main/kotlin"))
  }
}

/**
 * Настраиваем генерацию здесь
 */
openApiGenerate {
  val openapiGroup = "${rootProject.group}.api.v1"
  generatorName.set("kotlin")
  packageName.set(openapiGroup)
  apiPackage.set("$openapiGroup.api")
  modelPackage.set("$openapiGroup.models")
  invokerPackage.set("$openapiGroup.invoker")
  inputSpec.set(rootProject.ext["spec-cs-v1"] as String) // <-

  globalProperties.apply {
    put("models", "")
    put("modelDocs", "false")
  }

//  templateDir.set("${projectDir}/src/main/resources/templates")

  configOptions.set(
    mapOf(
      "dateLibrary" to "string",
      "enumPropertyNaming" to "UPPERCASE",
      "serializationLibrary" to "jackson",
      "collectionType" to "list",
      "nullableFields" to "true",
    )
  )
}

tasks {
  compileKotlin {
    dependsOn(openApiGenerate)
  }
}
