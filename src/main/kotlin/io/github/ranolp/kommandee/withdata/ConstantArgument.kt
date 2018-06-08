package io.github.ranolp.kommandee.withdata

import io.github.ranolp.kommandee.Kommandee
import io.github.ranolp.kommandee.KommandeeResult

class ConstantArgument(private val data: String) : WithData<Unit> {
    override fun expand(
        start: Int,
        args: List<String>,
        success: (Int, Unit) -> KommandeeResult
    ): KommandeeResult {
        return adapt(Kommandee.facadeOf<String>(), start, args).fold({
            if (it.second == data) {
                success(it.first, Unit)
            } else {
                KommandeeResult.Fail("${it.second} does not match with $data")
            }
        }, {
            KommandeeResult.Fail(it)
        })
    }
}
