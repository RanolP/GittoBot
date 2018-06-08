package io.github.ranolp.kommandee

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArgumentSplitterTest {
    @Test
    fun testSimple() {
        assertEquals(
            ArgumentSplitter.Simple.split("a b c d \"e f\"").left().get(),
            listOf("a", "b", "c", "d", "\"e", "f\"")
        )
    }

    @Test
    fun testDefault() {
        assertEquals(
            ArgumentSplitter.Default.split("a b c d \"e f\"").left().get(),
            listOf("a", "b", "c", "d", "e f")
        )
        assertEquals(
            ArgumentSplitter.Default.split("a b \"c d e\" f").left().get(),
            listOf("a", "b", "c d e", "f")
        )
        assertTrue(ArgumentSplitter.Default.split("a b c d \"e f").isRight())
    }
}
