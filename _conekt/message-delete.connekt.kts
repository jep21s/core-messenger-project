val host = "http://localhost:8080"

POST("$host/v1/message/delete") {
  header("Content-Type", "application/json")
  body(
    """
        {
          "requestType": "DELETE_MESSAGE",
          "ids": ["2d8e61ca-37a6-427a-b9b9-f89dc47d9147"],
          "chatId": "7a35d60b-c796-4c4a-96b0-77be74f3449a",
          "communicationType": "TG",
          "sentDate": 1764537908495,
          "debug": {
            "mode": "test"
          }
}
        """.trimIndent()
  )
}