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
import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.packet.IgmpPacketUtils.Companion.toIpv4Address

/**
 * Generic representation of the IGMPv1 message. All IGMPv1 types contain group address, type, and checksum fields.
 * ```
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Version| Type  |    Unused     |           Checksum            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Group Address                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 *
 * @param T type of IGMPv1 message
 * @param buffer packet payload
 * @constructor creation of IGMPv1 message
 */
internal sealed class IgmpV1<out T : Igmp<T>>(buffer: PacketBuffer) : Igmp<T>(buffer) {

    private val unusedOffset = typeOffset + 1
    private val groupAddressOffset = checksumOffset + 2

    init {
        superBuffer.setByte(unusedOffset, 0)
    }

    /**
     * In a Host Membership Query message, the group address field
     * is zeroed when sent, ignored when received.
     * In a Host Membership Report message, the group address field
     * holds the IP host group address of the group being reported.
     *
     * @return [Inet4Address]
     */
    fun groupAddress() = superBuffer.getInt(groupAddressOffset).toIpv4Address()

    /**
     * Set group address.
     *
     * @param address [Inet4Address]
     * @return [T]
     */
    open fun groupAddress(address: Inet4Address): T = superBuffer.setBytes(groupAddressOffset, address.address).let {
        @Suppress("UNCHECKED_CAST")
        this as T
    }

    override fun size() = 8
}