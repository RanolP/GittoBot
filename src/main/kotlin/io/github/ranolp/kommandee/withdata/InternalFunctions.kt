package io.github.ranolp.kommandee.withdata

import io.github.ranolp.kommandee.TypeAdapter
import org.funktionale.either.Either

internal fun <T> adapt(adapter: TypeAdapter<T>, start: Int, args: List<String>): Either<Pair<Int, T>, String> {
    val diff = (start + adapter.eat) - args.size
    val result =
        if (diff <= 0) adapter.accept(args.subList(start, start + adapter.eat).joinToString(" "))
        else Either.right("Expected $diff, but ${args.size} received.")
    return result.fold({
        Either.left(Pair(adapter.eat, it))
    }, {
        Either.right(it)
    })
}
