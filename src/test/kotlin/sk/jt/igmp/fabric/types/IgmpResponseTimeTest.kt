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
import java.math.BigDecimal.ZERO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class IgmpResponseTimeTest {

    @Test
    fun createResponseTime_fromSeconds() {
        val time = BigDecimal("15.5")
        val responseTime = IgmpResponseTime.createResponseTime(time)
        assertEquals(0x9b.toUByte(), responseTime.value)
        assertEquals(time, responseTime.seconds())
    }

    @Test
    fun createResponseTime_fromSeconds_invalidScale() {
        assertThrows(IllegalArgumentException::class.java) {
            IgmpResponseTime.createResponseTime(BigDecimal("10.27"))
        }
    }

    @Test
    fun createResponseTime_fromSeconds_outOfRange() {
        assertThrows(IllegalArgumentException::class.java) {
            IgmpResponseTime.createResponseTime(BigDecimal("26"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            IgmpResponseTime.createResponseTime(BigDecimal("-1"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            IgmpResponseTime.createResponseTime(ZERO)
        }
    }
}