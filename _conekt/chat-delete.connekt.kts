val host = "http://localhost:8080"

POST("$host/v1/chat/delete") {
  header("Content-Type", "application/json")
  body(
    """
        {
          "requestType": "DELETE_CHAT",
          "id": "b8a0b748-579e-48ad-aca5-6b13f37a238a",
          "communicationType": "TG"
}
        """.trimIndent()
  )
}