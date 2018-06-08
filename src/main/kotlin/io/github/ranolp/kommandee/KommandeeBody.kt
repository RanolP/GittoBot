package io.github.ranolp.kommandee

interface KommandeeBody {
    fun execute(executor: KommandeeExecutor, args: Array<out String>): KommandeeResult =
        execute(executor, args.toList())

    fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult
}
