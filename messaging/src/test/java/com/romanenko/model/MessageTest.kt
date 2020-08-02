package com.romanenko.model

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MessageTest {
    @Test
    fun `Verify users are not valid when sender is null`() {
        val areUsersNotValid = Message("id", null, "b", "text").areUsersNotValid()
        assertTrue(areUsersNotValid)
    }

    @Test
    fun `Verify users are not valid when receiver is null`() {
        val areUsersNotValid = Message("id", "a", null, "text").areUsersNotValid()
        assertTrue(areUsersNotValid)
    }

    @Test
    fun `Verify users are not valid when receiver and sender are same`() {
        val areUsersNotValid = Message("id", "a", "a", "text").areUsersNotValid()
        assertTrue(areUsersNotValid)
    }

    @Test
    fun `Verify users are valid on different sender and receiver`() {
        val areUsersNotValid = Message("id", "a", "b", "text").areUsersNotValid()
        assertFalse(areUsersNotValid)
    }


    @Test
    fun `Verify text is not valid when null`() {
        val textNotValid = Message(text = null).isTextNotValid()
        assertTrue(textNotValid)
    }

    @Test
    fun `Verify text is not valid when empty`() {
        val textNotValid = Message(text = "").isTextNotValid()
        assertTrue(textNotValid)
    }

    @Test
    fun `Verify text is not valid when blank`() {
        val textNotValid = Message(text = "\n\n\t   \t").isTextNotValid()
        assertTrue(textNotValid)
    }

    @Test
    fun `Verify text valid`() {
        val textNotValid = Message(text = "text").isTextNotValid()
        assertFalse(textNotValid)
    }
}