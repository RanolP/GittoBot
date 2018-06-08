package io.github.ranolp.kommandee.withdata

import io.github.ranolp.kommandee.Kommandee
import io.github.ranolp.kommandee.KommandeeResult

class TypedArgument<T>(val type: Class<T>) : WithData<T> {
    override fun expand(
        start: Int,
        args: List<String>,
        success: (Int, T) -> KommandeeResult
    ): KommandeeResult {
        return adapt(Kommandee.facadeOf(type), start, args).fold({
            success(it.first, it.second)
        }, {
            KommandeeResult.Fail(it)
        })
    }
}
