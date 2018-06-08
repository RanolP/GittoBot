package io.github.ranolp.kommandee

interface KommandeeExecutor {
    val isOperator: Boolean
    val name: String

    fun sendMessage(message: Message)

    fun sendMessage(message: String)

    fun reply(message: Message)

    fun reply(message: String)

    fun hasPermission(permission: Permission): Boolean
}
