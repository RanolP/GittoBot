package io.github.ranolp.kommandee.withdata

import io.github.ranolp.kommandee.KommandeeResult

interface WithData<T> {
    fun expand(start: Int, args: List<String>, success: (Int, T) -> KommandeeResult): KommandeeResult
}
