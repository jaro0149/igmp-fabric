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

/**
 * Time code represented by 'mant' (base) and 'exp' (exponent).
 *
 * @property code byte representation of code
 * @constructor creation of code
 */
abstract class IgmpMantExpCode(val code: UByte) {

    companion object {
        internal const val MAX_UNSCALED_CODE = 127u
        internal const val MAX_MANT = 15u
        internal const val MAX_EXP = 7u

        /**
         * Create IGMP time code using values of 'mant' and 'exp'.
         *
         * @param mant base part of the code
         * @param exp exponent part of the code
         * @throws IllegalArgumentException value of [mant] does not fit <0, [MAX_MANT]> interval or
         * value of [exp] does not fit <0, [MAX_EXP]> interval
         */
        fun createCode(mant: UByte, exp: UByte): UByte {
            require(mant <= MAX_MANT) { "IGMP 'mant' must be from the interval <0, $MAX_MANT>" }
            require(exp <= MAX_EXP) { "IGMP 'exp' must be from the interval <0, $MAX_EXP>" }
            return ((mant or 0x10u).toUInt() shr (exp + 3u).toInt()).toUByte()
        }
    }

    /**
     * Get unscaled time (without unit) representation of this code.
     *
     * @return [BigDecimal] time
     */
    fun time(): BigDecimal {
        if (code <= MAX_UNSCALED_CODE) {
            return BigDecimal.valueOf(code.toLong())
        }
        val mant = (code and 0x70u).toUInt()
        val exp = (code and 0x0Fu).toInt() shr 4
        val maxResponseTime = (mant or 0x10u) shl (exp + 3)
        return BigDecimal.valueOf(maxResponseTime.toLong())
    }

    /**
     * Get value of this code in seconds.
     *
     * @return seconds expressed by [BigDecimal]
     */
    abstract fun seconds(): BigDecimal
}