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

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import sk.jt.igmp.fabric.types.IgmpMaxResponseCode.Companion.createResponseCode

internal class IgmpMaxResponseCodeTest {

    @Test
    fun createResponseCode_invalidMantExp() {
        assertThrows(IllegalArgumentException::class.java) {
            createResponseCode(18u, 2u)
        }
        assertThrows(IllegalArgumentException::class.java) {
            createResponseCode(10u, 10u)
        }
    }

    @Test
    fun createResponseCode_usingMantExp() {
        val responseCode = createResponseCode(5u, 2u)
        assertEquals(0xa5u.toUByte(), responseCode.code)
    }

    @Test
    fun createResponseCode_invalidRange() {
        assertThrows(IllegalArgumentException::class.java) {
            createResponseCode(BigDecimal("1.87"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            createResponseCode(BigDecimal("-5"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            createResponseCode(BigDecimal("12.8"))
        }
    }

    @Test
    fun createResponseCode_usingSeconds() {
        val responseCode = createResponseCode(BigDecimal("9.8"))
        assertEquals(0x62u.toUByte(), responseCode.code)
    }
}