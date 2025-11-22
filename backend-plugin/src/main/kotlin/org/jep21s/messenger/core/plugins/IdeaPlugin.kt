package org.jep21s.messenger.core.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.ide.idea.model.IdeaModel

@Suppress("unused")
internal class IdeaPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    // Применяем стандартный плагин Idea
    pluginManager.apply("idea")

    // Настраиваем модуль Idea после применения плагина
    pluginManager.withPlugin("idea") {
      extensions.configure<IdeaModel>() {
        module {
          isDownloadJavadoc = true
          isDownloadSources = true
        }
      }
    }
  }
}