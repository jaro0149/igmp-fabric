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
import java.math.BigDecimal.valueOf
import sk.jt.igmp.fabric.types.TypeUtils.Companion.divideBy10
import sk.jt.igmp.fabric.types.TypeUtils.Companion.multiplyWith10

/**
 * IGMP response time used by IGMPv2.s It specifies the maximum allowed time before sending a
 * responding report in units of 1/10 second.
 *
 * @property value byte value of response time used in the IGMP packet
 * @constructor creation of [IgmpResponseTime]
 */
class IgmpResponseTime private constructor(val value: UByte) {

    companion object {
        private val MIN_RESPONSE_TIME_SEC = ZERO
        private val MAX_RESPONSE_TIME_SEC = valueOf(25.5)

        /**
         * Constant [IgmpResponseTime] with time set to 0.
         */
        val ZERO_RESPONSE_TIME = IgmpResponseTime(0u)

        /**
         * Creation of response time from byte field in the IGMP packet.
         *
         * @return created [IgmpResponseTime]
         */
        fun createResponseTime(time: UByte): IgmpResponseTime {
            return IgmpResponseTime(time)
        }

        /**
         * Creation of response time from provided seconds.
         *
         * @param seconds number of seconds
         * @return created [IgmpResponseTime]
         * @throws IllegalArgumentException scale of the input [BigDecimal] is higher than 1 or provided value
         * does not fit <[MIN_RESPONSE_TIME_SEC], [MAX_RESPONSE_TIME_SEC]> interval
         */
        fun createResponseTime(seconds: BigDecimal): IgmpResponseTime {
            require(seconds.scale() <= 1) { "IGMP Response Time scale must be from the interval <0, 1> " }
            require(seconds >= MIN_RESPONSE_TIME_SEC && seconds <= MAX_RESPONSE_TIME_SEC) {
                "IGMP Response Time must be from the interval <$MIN_RESPONSE_TIME_SEC, $MAX_RESPONSE_TIME_SEC>"
            }
            val value = seconds.multiplyWith10().toLong().toUByte()
            return IgmpResponseTime(value)
        }
    }

    /**
     * Convert byte value of IGMP response time into seconds.
     *
     * @return [BigDecimal]
     */
    fun seconds() = valueOf(value.toLong()).divideBy10()

    override fun toString() = "IgmpResponseTime(" +
            "value=$value, " +
            "seconds=${seconds()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as IgmpResponseTime
        if (value != other.value) {
            return false
        }
        return true
    }

    override fun hashCode() = value.hashCode()
}