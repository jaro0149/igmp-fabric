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
import sk.jt.igmp.fabric.types.IgmpMantExpCode.Companion.MAX_EXP
import sk.jt.igmp.fabric.types.IgmpMantExpCode.Companion.MAX_MANT
import sk.jt.igmp.fabric.types.TypeUtils.Companion.divideBy10
import sk.jt.igmp.fabric.types.TypeUtils.Companion.multiplyWith10

/**
 * IGMPv3 max. response code. It specifies the maximum time allowed before
 * sending a responding report.  The actual time allowed, called the Max
 * Resp Time, is represented in units of 1/10 second and is derived from
 * the Max Resp Code as follows:
 * ```
 * If Max Resp Code < 128, Max Resp Time = Max Resp Code.
 * If Max Resp Code >= 128, Max Resp Code represents a floating-point
 * value as follows:
 * 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+
 * |1| exp | mant  |
 * +-+-+-+-+-+-+-+-+
 * Max Resp Time = (mant | 0x10) << (exp + 3)
 * ```
 *
 * @property code byte representation of code
 * @constructor creation of [IgmpMaxResponseCode]
 */
class IgmpMaxResponseCode private constructor(code: UByte) : IgmpMantExpCode(code) {

    companion object {
        private val MIN_RESPONSE_TIME_SEC_EXC = ZERO
        private val MAX_RESPONSE_TIME_SEC_INC = valueOf(MAX_UNSCALED_CODE.toLong()).divideBy10()

        /**
         * Creation of response code using byte representation of code.
         *
         * @param responseCode byte representation of code
         * @return created [IgmpMaxResponseCode]
         */
        fun createResponseCode(responseCode: UByte): IgmpMaxResponseCode {
            require(responseCode != 0u.toUByte()) {
                "IGMP Response Code cannot be set to 0. Allowed interval: " +
                        "($MIN_RESPONSE_TIME_SEC_EXC, $MAX_RESPONSE_TIME_SEC_INC>"
            }
            return IgmpMaxResponseCode(responseCode)
        }

        /**
         * Creation of response code by composition of 'mant' and 'ext' parts.
         *
         * @param mant base part of the code
         * @param exp exponent part of the code
         * @return created [IgmpMaxResponseCode]
         * @throws IllegalArgumentException value of [mant] does not fit <0, [MAX_MANT]> interval or
         * value of [exp] does not fit <0, [MAX_EXP]> interval
         */
        fun createResponseCode(mant: UByte, exp: UByte): IgmpMaxResponseCode {
            return IgmpMaxResponseCode(createCode(mant, exp))
        }

        /**
         * Creation of response code without exponential part using provided number of seconds.
         *
         * @param seconds code represented by seconds
         * @return created [IgmpMaxResponseCode]
         * @throws IllegalArgumentException provided seconds does not fit into interval ([MIN_RESPONSE_TIME_SEC_EXC],
         * [MAX_RESPONSE_TIME_SEC_INC]> or scale of [BigDecimal] is higher than 1
         */
        fun createResponseCode(seconds: BigDecimal): IgmpMaxResponseCode {
            require(seconds.scale() <= 1) { "IGMP Response Code time scale must from the interval <0, 1> " }
            require(seconds > MIN_RESPONSE_TIME_SEC_EXC && seconds <= MAX_RESPONSE_TIME_SEC_INC) {
                "IGMP Response Code time must be from the interval " +
                        "($MIN_RESPONSE_TIME_SEC_EXC, $MAX_RESPONSE_TIME_SEC_INC>"
            }
            val value = seconds.multiplyWith10().toLong().toUByte()
            return IgmpMaxResponseCode(value)
        }
    }

    override fun seconds() = super.time().divideBy10()

    override fun toString() = "IgmpMaxResponseCode(" +
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