val host = "http://localhost:8080"

POST("$host/v1/message/delete") {
  header("Content-Type", "application/json")
  body(
    """
        {
          "requestType": "DELETE_MESSAGE",
          "ids": ["ac010544-394f-4ba0-b7aa-622cf01666dc"],
          "chatId": "98d4eab9-e2aa-42bd-9bab-6a280b56689d",
          "communicationType": "TG",
          "sentDate": 1764537908495
}
        """.trimIndent()
  )
}