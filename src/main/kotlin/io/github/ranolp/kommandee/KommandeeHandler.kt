package io.github.ranolp.kommandee

abstract class KommandeeHandler(val name: String, private val splitter: ArgumentSplitter, kommandees: Set<Kommandee>) {
    private val kommandeeMap = kommandees.associateBy { it.name }

    abstract fun preProcess(command: String): Pair<String, String>?

    fun execute(executor: KommandeeExecutor, command: String): KommandeeResult {
        return preProcess(command)?.let{ (label, argument) ->
            if (label !in kommandeeMap) {
                KommandeeResult.Fail("Command $label not found")
            } else {
                splitter.split(argument).fold({
                    kommandeeMap[label]!!.execute(executor, it)
                }, {
                    KommandeeResult.Fail(it)
                })
            }
        } ?: KommandeeResult.NOP
    }
}
