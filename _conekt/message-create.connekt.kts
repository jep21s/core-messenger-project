val host = "http://localhost:8080"

POST("$host/v1/message/create") {
    header("Content-Type", "application/json")
    body(
        """
        {
          "requestType": "CREATE_MESSAGE",
          "chatId": "7a35d60b-c796-4c4a-96b0-77be74f3449a",
          "communicationType": "TG",
          "messageType": "simple",
          "senderId": "1",
          "senderType": "EMPLOYEE",
          "sentDate": 1764537908495,
          "body": "some text",
          "debug": {
            "mode": "test"
          }
}
        """.trimIndent()
    )
}