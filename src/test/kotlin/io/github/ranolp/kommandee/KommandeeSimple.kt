package io.github.ranolp.kommandee

import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import org.funktionale.collections.tail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KommandeeSimple {
    @Test
    fun test1() {
        val kommandee = Kommandee.of("help").either<Int, String>() build {
            it.fold({
                sendMessage("$it 페이지를 원하는걸까요.")
            }, {
                sendMessage("$it 명령어를 원하는걸까요.")
            })

            KommandeeResult.Ok
        }

        val handler = object : KommandeeHandler("Test", ArgumentSplitter.Default, setOf(kommandee)) {
            override fun preProcess(command: String): Pair<String, String>? =
                command.takeUnless { it[0] != '/' }?.split(' ')?.let { it.first().substring(1) to it.tail().joinToString(" ") }
        }

        val executor = mockkClass(KommandeeExecutor::class)

        every { executor.hasPermission(any()) } returns true
        every { executor.sendMessage(any<String>()) } returns Unit


        assertEquals(handler.execute(executor, "/help 8"), KommandeeResult.Ok)
        assertEquals(handler.execute(executor, "/help str"), KommandeeResult.Ok)
        assertTrue(handler.execute(executor, "/help") is KommandeeResult.Fail)
        assertTrue(handler.execute(executor, "/asdf") is KommandeeResult.Fail)
        assertEquals(handler.execute(executor, "!asdf"), KommandeeResult.NOP)

        verify(exactly = 1) { executor.sendMessage("8 페이지를 원하는걸까요.") }
        verify(exactly = 1) { executor.sendMessage("str 명령어를 원하는걸까요.") }
    }
}
