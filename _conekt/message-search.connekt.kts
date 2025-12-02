val host = "http://localhost:8080"

POST("$host/v1/message/search") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "requestType": "SEARCH_MESSAGE",
            "chatFilter": {
              "id": "7a35d60b-c796-4c4a-96b0-77be74f3449a",
              "communicationType": "TG"
            },
            "messageFilter": {
              "sentDate": {
                "direction": "LESS",
                "value": 1765537908495
              }
            },
            "order": "DESC",
            "debug": {
            "mode": "test"
          }
        }
        """.trimIndent()
    )
}