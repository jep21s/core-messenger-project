val host = "http://localhost:8080"

POST("$host/v1/chat/search") {
  header("Content-Type", "application/json")
  body(
    """
        {
            "requestType": "SEARCH_CHAT",
            "filter": {
              "communicationType": "TG"
            },
            "sort": {
                "sortField": "LATEST_MESSAGE_DATE", 
                "order": "DESC"
              }
        }
        """.trimIndent()
  )
}