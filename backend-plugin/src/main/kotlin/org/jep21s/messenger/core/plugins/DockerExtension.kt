package org.jep21s.messenger.core.plugins

open class DockerExtension {
  var dockerFile = "Dockerfile"
  var imageName = ""
  var imageTag = "latest"
  var buildContext = "./"
  var buildArgs: Map<String, String> = emptyMap()
  var noCache = false
  var removeIntermediateContainers = false
}