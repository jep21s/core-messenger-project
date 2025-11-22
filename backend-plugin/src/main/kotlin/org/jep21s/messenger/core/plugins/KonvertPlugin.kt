package org.jep21s.messenger.core.plugins

import java.io.File
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

@Suppress("unused")
class KonvertPlugin : Plugin<Project> {
  private val logger = Logging.getLogger(this::class.java)

  override fun apply(project: Project) {
//    applyRequiredPlugins(project)
    configureDependencies(project)

    project.afterEvaluate {
      tasks.withType(KotlinCompile::class.java)
        .configureEach {
          doLast { processKonvertFiles(project) }
        }
    }
  }

//  private fun applyRequiredPlugins(project: Project) {
//    project.pluginManager.apply("com.google.devtools.ksp")
//  }

  private fun configureDependencies(project: Project) {
    val libs = project.the<LibrariesForLibs>()
//    project.dependencies {
//      add("implementation", "io.mcarle:konvert-api:$konvertVersion")
//      add("ksp", "io.mcarle:konvert:$konvertVersion")
//    }
    project.dependencies {
      add("implementation", libs.konvert.api)
      add("ksp", libs.konvert.lib)
      add("implementation" ,"org.jep21s.messenger.core.libs:core-messenger-lib-konvert")
    }

    logger.lifecycle("Konvert plugin: added dependencies with version ${libs.konvert.lib.get()}")
  }

  private fun processKonvertFiles(project: Project) = with(project) {
    val generatedDir = file("build/generated/ksp")
    if (!generatedDir.exists()) return

    val customImport = "import org.jep21s.messenger.core.lib.konvert.requireNotNull"

    generatedDir.walk()
      .filter { it.isFile && it.extension == "kt" }
      .forEach { file: File ->
        var content = file.readText()

        // Пропускаем файлы без !!
        if (!content.contains("!!")) return@forEach

        // Добавляем импорт если нужно
        if (!content.contains(customImport)) {
          content = addImportSafely(content, customImport)
        }

        // Заменяем !! на requireNotNull
        val pattern = """([\w\[\]().]+?(?:\.\w+)*)!!""".toRegex()
        content = pattern.replace(content) { match ->
          val expr = match.groupValues[1]
//          val fieldName = expr.substringAfterLast('.').takeIf { it.isNotEmpty() } ?: expr
          "$expr.requireNotNull(fieldName = \"$expr\")"
        }

        // Заменяем окончания функций на requireNotNull
        val patternFunction = """}!!""".toRegex()
        content = patternFunction.replace(content) { match ->
          "}.requireNotNull()"
        }

        file.writeText(content)
      }
  }

  fun addImportSafely(content: String, importToAdd: String): String {
    val lines = content.lines().toMutableList()

    // Ищем существующие импорты
    val importLines = lines.filter { it.startsWith("import ") }

    // Если такой импорт уже есть - удаляем
    val existingSimilarImport = importLines.find {
      it == importToAdd
    }

    if (existingSimilarImport != null) {
      lines.remove(existingSimilarImport)
    }

    // Добавляем новый импорт в правильное место
    val lastImportIndex = lines.indexOfLast { it.startsWith("import ") }
    if (lastImportIndex != -1) {
      lines.add(lastImportIndex + 1, importToAdd)
    } else {
      // Ищем package declaration
      val packageIndex = lines.indexOfFirst { it.startsWith("package ") }
      if (packageIndex != -1) {
        lines.add(packageIndex + 1, importToAdd)
      } else {
        lines.add(0, importToAdd)
      }
    }

    return lines.joinToString("\n")
  }
}