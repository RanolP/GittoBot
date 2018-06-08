package io.github.ranolp.kommandee

import io.github.ranolp.common.ContextDsl
import io.github.ranolp.kommandee.withdata.ConstantArgument
import io.github.ranolp.kommandee.withdata.EitherArgument
import io.github.ranolp.kommandee.withdata.TypedArgument
import io.github.ranolp.kommandee.withdata.WithData
import org.funktionale.either.Either

class Kommandee(val name: String, private val delegate: KommandeeBody) : KommandeeBody by delegate {
    companion object {
        private val facades = mutableMapOf<Class<*>, TypeAdapter<*>>()

        operator fun plusAssign(typeAdapter: TypeAdapter<*>): Unit = addTypeFacade(typeAdapter, false)

        init {
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toByte())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toShort())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toInt())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toLong())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toFloat())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toDouble())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toBigInteger())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) {
                try {
                    Either.left(it.toBigDecimal())
                } catch (e: NumberFormatException) {
                    Either.right(e.message!!)
                }
            }
            this += TypeAdapter.of(1) { Either.left(it.toBoolean()) }
            this += TypeAdapter.of(1) { Either.left(it) }
        }

        @JvmStatic
        fun addTypeFacade(typeAdapter: TypeAdapter<*>, overrides: Boolean = false) {
            if (!overrides && typeAdapter.type in facades) {
                throw IllegalStateException("You cannot override already registered types!")
            }
            facades[typeAdapter.type] = typeAdapter
        }

        inline fun <reified T> facadeOf(): TypeAdapter<T> = facadeOf(T::class.java)

        @Suppress("UNCHECKED_CAST")
        fun <T> facadeOf(clazz: Class<T>): TypeAdapter<T> = facades[clazz] as TypeAdapter<T>

        @JvmStatic
        fun of(name: String): Kommandee0 = Kommandee0(name)
    }

    abstract class Builder(val name: String) {
        @ContextDsl
        abstract infix fun <T> with(withData: WithData<T>): Builder
    }

    class Kommandee0(
        name: String
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.() -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return process(executor)
        }

        override fun <T> with(withData: WithData<T>): Kommandee1<T> {
            return Kommandee1(name, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.() -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee1<T> = with(TypedArgument(T::class.java))

        inline fun <reified L, reified R> either(): Kommandee1<Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee1<A>(
        name: String,
        private val arg1: WithData<A>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { _, first ->
                process(executor, first)
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee2<A, T> {
            return Kommandee2(name, arg1, withData)
        }

        @ContextDsl
        infix fun build(process: KommandeeExecutor. (A) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee2<A, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee2<A, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee2<A, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee2<A, B>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { _, second ->
                    process(executor, first, second)
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee3<A, B, T> {
            return Kommandee3(name, arg1, arg2, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee3<A, B, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee3<A, B, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee3<A, B, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee3<A, B, C>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { _, third ->
                        process(executor, first, second, third)
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee4<A, B, C, T> {
            return Kommandee4(name, arg1, arg2, arg3, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee4<A, B, C, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee4<A, B, C, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee4<A, B, C, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee4<A, B, C, D>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { _, fourth ->
                            process(executor, first, second, third, fourth)
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee5<A, B, C, D, T> {
            return Kommandee5(name, arg1, arg2, arg3, arg4, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee5<A, B, C, D, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee5<A, B, C, D, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee5<A, B, C, D, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee5<A, B, C, D, E>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>,
        private val arg5: WithData<E>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D, E) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { eat4, fourth ->
                            arg5.expand(eat1 + eat2 + eat3 + eat4, args) { _, fifth ->
                                process(executor, first, second, third, fourth, fifth)
                            }
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee6<A, B, C, D, E, T> {
            return Kommandee6(name, arg1, arg2, arg3, arg4, arg5, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D, E) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee6<A, B, C, D, E, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee6<A, B, C, D, E, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee6<A, B, C, D, E, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee6<A, B, C, D, E, F>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>,
        private val arg5: WithData<E>,
        private val arg6: WithData<F>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D, E, F) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { eat4, fourth ->
                            arg5.expand(eat1 + eat2 + eat3 + eat4, args) { eat5, fifth ->
                                arg6.expand(eat1 + eat2 + eat3 + eat4 + eat5, args) { _, sixth ->
                                    process(executor, first, second, third, fourth, fifth, sixth)
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee7<A, B, C, D, E, F, T> {
            return Kommandee7(name, arg1, arg2, arg3, arg4, arg5, arg6, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D, E, F) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee7<A, B, C, D, E, F, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee7<A, B, C, D, E, F, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee7<A, B, C, D, E, F, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee7<A, B, C, D, E, F, G>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>,
        private val arg5: WithData<E>,
        private val arg6: WithData<F>,
        private val arg7: WithData<G>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D, E, F, G) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { eat4, fourth ->
                            arg5.expand(eat1 + eat2 + eat3 + eat4, args) { eat5, fifth ->
                                arg6.expand(eat1 + eat2 + eat3 + eat4 + eat5, args) { eat6, sixth ->
                                    arg7.expand(eat1 + eat2 + eat3 + eat4 + eat5 + eat6, args) { _, seventh ->
                                        process(executor, first, second, third, fourth, fifth, sixth, seventh)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee8<A, B, C, D, E, F, G, T> {
            return Kommandee8(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D, E, F, G) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee8<A, B, C, D, E, F, G, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee8<A, B, C, D, E, F, G, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee8<A, B, C, D, E, F, G, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee8<A, B, C, D, E, F, G, H>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>,
        private val arg5: WithData<E>,
        private val arg6: WithData<F>,
        private val arg7: WithData<G>,
        private val arg8: WithData<H>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D, E, F, G, H) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { eat4, fourth ->
                            arg5.expand(eat1 + eat2 + eat3 + eat4, args) { eat5, fifth ->
                                arg6.expand(eat1 + eat2 + eat3 + eat4 + eat5, args) { eat6, sixth ->
                                    arg7.expand(eat1 + eat2 + eat3 + eat4 + eat5 + eat6, args) { eat7, seventh ->
                                        arg8.expand(eat1 + eat2 + eat3 + eat4 + eat5 + eat6 + eat7, args) { _, eighth ->
                                            process(
                                                executor,
                                                first,
                                                second,
                                                third,
                                                fourth,
                                                fifth,
                                                sixth,
                                                seventh,
                                                eighth
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Kommandee9<A, B, C, D, E, F, G, H, T> {
            return Kommandee9(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, withData)
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D, E, F, G, H) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }

        inline fun <reified T> typed(): Kommandee9<A, B, C, D, E, F, G, H, T> = with(TypedArgument(T::class.java))

        fun constant(data: String): Kommandee9<A, B, C, D, E, F, G, H, Unit> = with(ConstantArgument(data))

        inline fun <reified L, reified R> either(): Kommandee9<A, B, C, D, E, F, G, H, Either<L, R>> =
            with(EitherArgument(L::class.java, R::class.java))
    }

    class Kommandee9<A, B, C, D, E, F, G, H, I>(
        name: String,
        private val arg1: WithData<A>,
        private val arg2: WithData<B>,
        private val arg3: WithData<C>,
        private val arg4: WithData<D>,
        private val arg5: WithData<E>,
        private val arg6: WithData<F>,
        private val arg7: WithData<G>,
        private val arg8: WithData<H>,
        private val arg9: WithData<I>
    ) : Builder(name), KommandeeBody {
        private lateinit var process: KommandeeExecutor.(A, B, C, D, E, F, G, H, I) -> KommandeeResult
        override fun execute(executor: KommandeeExecutor, args: List<String>): KommandeeResult {
            return arg1.expand(0, args) { eat1, first ->
                arg2.expand(eat1, args) { eat2, second ->
                    arg3.expand(eat1 + eat2, args) { eat3, third ->
                        arg4.expand(eat1 + eat2 + eat3, args) { eat4, fourth ->
                            arg5.expand(eat1 + eat2 + eat3 + eat4, args) { eat5, fifth ->
                                arg6.expand(eat1 + eat2 + eat3 + eat4 + eat5, args) { eat6, sixth ->
                                    arg7.expand(eat1 + eat2 + eat3 + eat4 + eat5 + eat6, args) { eat7, seventh ->
                                        arg8.expand(
                                            eat1 + eat2 + eat3 + eat4 + eat5 + eat6 + eat7,
                                            args
                                        ) { eat8, eighth ->
                                            arg9.expand(
                                                eat1 + eat2 + eat3 + eat4 + eat5 + eat6 + eat7 + eat8,
                                                args
                                            ) { _, ninth ->
                                                process(
                                                    executor,
                                                    first,
                                                    second,
                                                    third,
                                                    fourth,
                                                    fifth,
                                                    sixth,
                                                    seventh,
                                                    eighth,
                                                    ninth
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun <T> with(withData: WithData<T>): Builder {
            throw IllegalStateException("No more utility class defined")
        }


        @ContextDsl
        infix fun build(process: KommandeeExecutor.(A, B, C, D, E, F, G, H, I) -> KommandeeResult): Kommandee {
            this.process = process
            return Kommandee(name, this)
        }
    }
}
