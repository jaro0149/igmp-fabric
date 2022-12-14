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

/**
 * The QRV field contains the [Robustness Variable] value used by the querier, i.e., the sender of the Query.
 * Range of values: <0, 7>.
 *
 * @property value byte representation of robustness variable
 */
class IgmpQuerierRobustnessVariable private constructor(val value: UByte) {

    companion object {
        private const val MAX_ROBUSTNESS_VARIABLE = 7u

        /**
         * Creation of robustness variable from IGMP packet byte.
         *
         * @param variable byte representation of robustness variable
         * @return parsed [IgmpQuerierRobustnessVariable]
         * @throws IllegalArgumentException provided value does not fit <0, [MAX_ROBUSTNESS_VARIABLE]> range
         */
        fun createQueryRobustnessVariable(variable: UByte): IgmpQuerierRobustnessVariable {
            require(variable <= MAX_ROBUSTNESS_VARIABLE) {
                "IGMP Query Robustness Variable must be from the set <0; >"
            }
            return IgmpQuerierRobustnessVariable(variable)
        }
    }

    override fun toString() = "IgmpQuerierRobustnessVariable(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as IgmpQuerierRobustnessVariable
        if (value != other.value) {
            return false
        }
        return true
    }

    override fun hashCode() = value.hashCode()
}