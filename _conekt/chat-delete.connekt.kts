val host = "http://localhost:8080"

POST("$host/v1/chat/delete") {
  header("Content-Type", "application/json")
  body(
    """
        {
          "requestType": "DELETE_CHAT",
          "id": "e599718a-bfe4-4f8c-bf28-e2822ff3d381",
          "communicationType": "TG",
          "debug": {
            "mode": "test"
          }
}
        """.trimIndent()
  )
}