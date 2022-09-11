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
package sk.jt.igmp.fabric.packet

import java.net.Inet4Address
import pcap.common.net.InetAddresses.fromBytesToInet4Address
import pcap.common.util.Bytes.toByteArray

internal class IgmpPacketUtils {

    companion object {
        /**
         * Get [Boolean] value of bit on the specified position in the unsigned byte.
         *
         * @param position position of bit in the byte
         * @return [Boolean] value of the bit
         */
        infix fun UByte.bit(position: UByte): Boolean {
            val shiftedValue = this.toUInt() shr (position - 1u).toInt()
            if (shiftedValue == 0u) {
                return false
            }
            return true
        }

        /**
         * Get [Boolean] value of bit on the specified position in the signed byte.
         *
         * @param position position of bit in the byte
         * @return [Boolean] value of the bit
         */
        infix fun Byte.bit(position: UByte): Boolean {
            return this.toUByte() bit position
        }

        /**
         * Conversion of [Int] into [Inet4Address].
         *
         * @return derived [Inet4Address]
         */
        fun Int.toIpv4Address(): Inet4Address = fromBytesToInet4Address(toByteArray(this))
    }
}