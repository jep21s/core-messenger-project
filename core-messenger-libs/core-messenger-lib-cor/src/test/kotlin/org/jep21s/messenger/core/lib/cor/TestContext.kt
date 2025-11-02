package org.jep21s.messenger.core.lib.cor

data class TestContext(
    val status: CorStatuses = CorStatuses.NONE,
    val some: Int = 0,
    val history: String = "",
) {
    enum class CorStatuses {
        NONE,
        RUNNING,
        FAILING,
        ERROR
    }
}

