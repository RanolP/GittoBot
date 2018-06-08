package io.github.ranolp.kommandee

sealed class KommandeeResult(val message: String) {
    object Ok : KommandeeResult("Ok") {
        override fun toString(): String = "KommandeeResult.Ok"
    }

    object NOP : KommandeeResult("No Operation") {
        override fun toString(): String = "KommandeeResult.NOP"
    }

    class Fail(message: String = "Unknown cause") : KommandeeResult(message) {
        override fun toString(): String = "KommandeeResult.Fail($message)"
    }
}
