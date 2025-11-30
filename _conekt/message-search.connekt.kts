val host = "http://localhost:8080"

POST("$host/v1/message/search") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "requestType": "SEARCH_MESSAGE",
            "chatFilter": {
              "id": "98d4eab9-e2aa-42bd-9bab-6a280b56689d",
              "communicationType": "TG"
            },
            "messageFilter": {
              "sentDate": {
                "direction": "LESS",
                "value": 1765537908495
              }
            },
            "order": "DESC"
        }
        """.trimIndent()
    )
}