package io.github.ranolp.kommandee.withdata

import io.github.ranolp.kommandee.Kommandee
import io.github.ranolp.kommandee.KommandeeResult
import org.funktionale.either.Either

class EitherArgument<A, B>(val typeA: Class<A>, val typeB: Class<B>) : WithData<Either<A, B>> {
    override fun expand(
        start: Int,
        args: List<String>,
        success: (Int, Either<A, B>) -> KommandeeResult
    ): KommandeeResult {
        // try a
        return adapt(Kommandee.facadeOf(typeA), start, args).fold({
            success(it.first, Either.left(it.second))
        }, {
            adapt(Kommandee.facadeOf(typeB), start, args).fold({
                success(it.first, Either.right(it.second))
            }, {
                KommandeeResult.Fail("Expected ${typeA.simpleName} or ${typeB.simpleName}, but nothing received.")
            })
        })
    }
}
