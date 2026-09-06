package com.infobip.mobilemessaging.huawei.chat

internal class ChatRuntimeEventBridge(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val emit: (Map<String, String>) -> Unit,
) {
    private val pending = ArrayDeque<Map<String, String>>()
    private var ready = false
    private var disposed = false

    fun publish(event: Map<String, String>) {
        if (disposed) return
        if (ready) {
            emit(event)
            return
        }
        if (pending.size == capacity) pending.removeFirst()
        pending.addLast(event)
    }

    fun publishResult(
        successful: Boolean,
        event: Map<String, String>,
        onFailure: () -> Unit,
    ) {
        if (successful) publish(event) else onFailure()
    }

    fun ready() {
        if (disposed || ready) return
        ready = true
        while (pending.isNotEmpty()) emit(pending.removeFirst())
    }

    fun dispose() {
        disposed = true
        pending.clear()
    }

    companion object {
        const val DEFAULT_CAPACITY = 32
    }
}
