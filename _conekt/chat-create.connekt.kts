val host = "http://localhost:8080"

POST("$host/v1/chat/create") {
  header("Content-Type", "application/json")
  body(
    """
        {
          "requestType": "CREATE_CHAT",
          "debug": {
            "mode": "prod"
          },
          "communicationType": "TG",
          "chatType": "simple"
}
        """.trimIndent()
  )
}