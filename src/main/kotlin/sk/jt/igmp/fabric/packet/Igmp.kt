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

import pcap.codec.AbstractPacket
import pcap.codec.AbstractPacket.Checksum.sum
import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.Companion.fromByte

/**
 * Generic representation of IGMP message: it is specified by type and every IGMP packet contains checksum
 * on the same place in the packet.
 *
 * @param T type of message (CRTP)
 * @param buffer packet payload
 * @constructor creation of [Igmp] packet from specified [PacketBuffer]
 */
internal sealed class Igmp<out T : Igmp<T>> constructor(buffer: PacketBuffer) : AbstractPacket(buffer) {

    companion object {
        /**
         * IGMP protocol number used in IP header.
         */
        internal const val TYPE: Int = 0x02
    }

    val typeOffset = superOffset
    val checksumOffset = typeOffset + 2

    /**
     * Type of the IGMP message.
     *
     * @return [IgmpType]
     */
    fun type() = fromByte(superBuffer.getByte(typeOffset).toUByte())

    /**
     * The checksum is the 16-bit one's complement of the one's complement
     * sum of the whole IGMP message (the entire IP payload).  For computing
     * the checksum, the checksum field is set to zero.  When transmitting
     * packets, the checksum MUST be computed and inserted into this field.
     * When receiving packets, the checksum MUST be verified before
     * processing a packet.
     *
     * @return [UShort] value
     */
    fun checksum() = superBuffer.getShort(checksumOffset).toUShort()

    /**
     * Set type of the IGMP message.
     *
     * @param igmpType [IgmpType]
     * @return [T]
     */
    protected open fun type(igmpType: IgmpType): T = superBuffer.setByte(typeOffset, igmpType.type.toInt()).let {
        @Suppress("UNCHECKED_CAST") return this as T
    }

    /**
     * Set checksum of the IGMP message.
     *
     * @param checksum [UShort] value
     * @return [T]
     */
    fun checksum(checksum: UShort): T = superBuffer.setShort(checksumOffset, checksum.toInt()).let {
        @Suppress("UNCHECKED_CAST") return this as T
    }

    /**
     * Calculattion of the checksum from the IGMP message and payload.
     *
     * @return calculated [UShort] checksum value
     */
    fun calculateChecksum(): UShort {
        var accumulation = sum(superBuffer, superOffset, size())
        accumulation -= superBuffer.getShort(checksumOffset).toInt() and 0xFFFF
        accumulation = (accumulation shr 16 and 0xFFFF) + (accumulation and 0xFFFF)
        return accumulation.inv().toUShort()
    }

    /**
     * Verification if set checksum value is correct.
     *
     * @return true if set checksum is valid
     */
    fun isValidChecksum() = checksum() == calculateChecksum()
}