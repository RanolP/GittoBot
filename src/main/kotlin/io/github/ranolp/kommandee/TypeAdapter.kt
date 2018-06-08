package io.github.ranolp.kommandee

import org.funktionale.either.Either

interface TypeAdapter<T> {
    companion object {
        inline fun <reified T> of(eat: Int, crossinline body: (String) -> Either<T, String>) = object : TypeAdapter<T> {
            override val type = T::class.java
            override val eat: Int = eat

            override fun accept(data: String): Either<T, String> = body(data)
        }
    }

    val type: Class<T>

    val eat: Int

    /**
     * Transform [data] to specific [type] if [data] is convertible, or else return error message.
     *
     * @param data the data
     *
     * @return Contain converted data or error message
     */
    fun accept(data: String): Either<T, String>
}
