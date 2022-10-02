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

import sk.jt.igmp.fabric.types.IgmpMantExpCode.Companion.MAX_EXP
import sk.jt.igmp.fabric.types.IgmpMantExpCode.Companion.MAX_MANT

/**
 * The Querier's Query Interval Code field specifies the [Query
 * Interval] used by the querier.  The actual interval, called the
 * Querier's Query Interval (QQI), is represented in units of seconds
 * and is derived from the Querier's Query Interval Code as follows:
 * ```
 * If QQIC < 128, QQI = QQIC
 * If QQIC >= 128, QQIC represents a floating-point value as follows:
 * 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+
 * |1| exp | mant  |
 * +-+-+-+-+-+-+-+-+
 * QQI = (mant | 0x10) << (exp + 3)
 * ```
 * @property code byte representation of code
 * @constructor creation of [IgmpQuerierQueryIntervalCode]
 */
class IgmpQuerierQueryIntervalCode private constructor(code: UByte) : IgmpMantExpCode(code) {

    companion object {
        private val MIN_INTERVAL_SEC = 0u
        private val MAX_INTERVAL_SEC = 127u

        /**
         * Creation of query interval code using byte representation of code.
         *
         * @param responseCode byte representation of code
         * @return created [IgmpQuerierQueryIntervalCode]
         */
        fun createQueryIntervalCode(responseCode: UByte) = IgmpQuerierQueryIntervalCode(responseCode)

        /**
         * Creation of query interval code by composition of 'mant' and 'ext' parts.
         *
         * @param mant base part of the code
         * @param exp exponent part of the code
         * @return created [IgmpQuerierQueryIntervalCode]
         * @throws IllegalArgumentException value of [mant] does not fit <0, [MAX_MANT]> interval or
         * value of [exp] does not fit <0, [MAX_EXP]> interval
         */
        fun createQueryIntervalCode(mant: UByte, exp: UByte) = IgmpQuerierQueryIntervalCode(createCode(mant, exp))

        /**
         * Creation of query interval code without exponential part using provided number of seconds.
         *
         * @param seconds code represented by seconds
         * @throws IllegalArgumentException provided seconds does not fit into interval <[MIN_INTERVAL_SEC],
         * [MAX_INTERVAL_SEC]>
         * @return created [IgmpQuerierQueryIntervalCode]
         */
        fun createQueryIntervalCodeFromSeconds(seconds: UByte): IgmpQuerierQueryIntervalCode {
            require(seconds >= MIN_INTERVAL_SEC && seconds <= MAX_INTERVAL_SEC) {
                "IGMP Response Code time must be from the interval <$MIN_INTERVAL_SEC, $MAX_INTERVAL_SEC>"
            }
            return IgmpQuerierQueryIntervalCode(seconds)
        }
    }

    override fun seconds() = super.time()

    override fun toString() = "IgmpQuerierQueryIntervalCode(" +
            "code=$code, " +
            "seconds=${seconds()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as IgmpMantExpCode
        if (code != other.code) {
            return false
        }
        return true
    }

    override fun hashCode() = code.hashCode()
}