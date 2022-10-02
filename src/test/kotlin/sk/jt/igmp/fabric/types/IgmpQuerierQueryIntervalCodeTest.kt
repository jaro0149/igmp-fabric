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
import sk.jt.igmp.fabric.types.IgmpQuerierQueryIntervalCode.Companion.createQueryIntervalCode
import sk.jt.igmp.fabric.types.IgmpQuerierQueryIntervalCode.Companion.createQueryIntervalCodeFromSeconds

internal class IgmpQuerierQueryIntervalCodeTest {

    @Test
    fun createIntervalCode_invalidMantExp() {
        assertThrows(IllegalArgumentException::class.java) {
            createQueryIntervalCode(18u, 2u)
        }
        assertThrows(IllegalArgumentException::class.java) {
            createQueryIntervalCode(10u, 10u)
        }
    }

    @Test
    fun createIntervalCode_usingMantExp() {
        val responseCode = createQueryIntervalCode(5u, 2u)
        assertEquals(0xa5u.toUByte(), responseCode.code)
        assertEquals(BigDecimal("384"), responseCode.seconds())
    }

    @Test
    fun createIntervalCode_invalidRange() {
        assertThrows(IllegalArgumentException::class.java) {
            createQueryIntervalCodeFromSeconds(200u)
        }
    }

    @Test
    fun createIntervalCode_fromSeconds() {
        val code = createQueryIntervalCodeFromSeconds(15u)
        assertEquals(0x0fu.toUByte(), code.code)
        assertEquals(BigDecimal("15"), code.seconds())
    }
}