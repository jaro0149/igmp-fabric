/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 igmp-fabric contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sk.jt.igmp.fabric.types

import kotlin.test.assertContentEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import sk.jt.igmp.fabric.types.IgmpAuxiliaryWord.Companion.createWord
import sk.jt.igmp.fabric.types.IgmpAuxiliaryWord.Companion.createWords

internal class IgmpAuxiliaryWordTest {

    @Test
    fun createWord_invalidLength() {
        assertThrows(IllegalArgumentException::class.java) {
            createWord(listOf(10u, 2u, 3u, 4u, 5u))
        }
    }

    @Test
    fun createWord() {
        val word = createWord(listOf(10u, 2u, 3u, 4u))
        val byteArray: ByteArray = word.toByteArray()
        assertContentEquals(byteArrayOf(10, 2, 3, 4), byteArray)
    }

    @Test
    fun createWords_invalidLength() {
        assertThrows(IllegalArgumentException::class.java) {
            createWords(byteArrayOf(10, 2, 3, 4, 5, 7, 3))
        }
    }

    @Test
    fun createWords_empty() {
        val words = createWords(byteArrayOf())
        assertTrue(words.isEmpty())
    }

    @Test
    fun createWords() {
        val words = createWords(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
        assertEquals(2, words.size)
        assertEquals(listOf<UByte>(1u, 2u, 3u, 4u), words[0].bytes)
        assertEquals(listOf<UByte>(5u, 6u, 7u, 8u), words[1].bytes)
    }
}