val host = "http://localhost:8080"

POST("$host/v1/message/create") {
    header("Content-Type", "application/json")
    body(
        """
        {
          "requestType": "CREATE_MESSAGE",
          "chatId": "98d4eab9-e2aa-42bd-9bab-6a280b56689d",
          "communicationType": "TG",
          "messageType": "simple",
          "senderId": "1",
          "senderType": "EMPLOYEE",
          "sentDate": 1764537908495,
          "body": "some text"
}
        """.trimIndent()
    )
}