package io.github.ranolp.kommandee

import org.funktionale.either.Either

interface ArgumentSplitter {
    fun split(data: String): Either<List<String>, String>

    object Simple : ArgumentSplitter {
        override fun split(data: String): Either<List<String>, String> = Either.left(data.split(" ").filter { it.isNotEmpty() })
    }

    object Default : ArgumentSplitter {
        override fun split(data: String): Either<List<String>, String> {
            val result = mutableListOf<String>()

            var opened = false
            val buffer = StringBuilder()

            for (str in data.split(" ").filter { it.isNotEmpty() }) {
                if (opened) {
                    val isClose = str[str.length - 1] == '\"' && str[str.length - 2] != '\\'
                    buffer.append(' ').append(if (isClose) str.substring(0, str.length - 1) else str)

                    if (isClose) {
                        opened = false
                        result += buffer.toString()
                        buffer.setLength(0)
                    }
                } else if (str[0] == '\"') {
                    opened = true
                    buffer.append(str.substring(1))
                } else {
                    result += str
                }
            }

            return if (buffer.isNotEmpty()) {
                Either.right("Incomplete string data $buffer found")
            } else {
                Either.left(result)
            }
        }
    }
}
