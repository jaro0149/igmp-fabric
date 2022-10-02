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
import sk.jt.igmp.fabric.types.IgmpResponseTime
import sk.jt.igmp.fabric.types.IgmpResponseTime.Companion.createResponseTime

/**
 * Generic representation of the IGMPv2 message. All IGMPv1 types contain group address, response time, type,
 * and checksum fields.
 * ```
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Type     | Max Resp Time |           Checksum            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Group Address                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 *
 * @param T type of IGMPv2 message (CRTP)
 * @param buffer packet payload
 * @constructor creation of IGMPv2 message
 */
internal sealed class IgmpV2<out T : Igmp<T>>(buffer: PacketBuffer) : Igmp<T>(buffer) {

    protected val responseTimeOffset = typeOffset + 1
    private val groupAddressOffset = checksumOffset + 2

    /**
     * The Max Response Time field is meaningful only in Membership Query
     * messages, and specifies the maximum allowed time before sending a
     * responding report in units of 1/10 second.  In all other messages, it
     * is set to zero by the sender and ignored by receivers.
     * Varying this setting allows IGMPv2 routers to tune the "leave
     * latency" (the time between the moment the last host leaves a group
     * and when the routing protocol is notified that there are no more
     * members).  It also allows tuning of the burstiness of IGMP traffic
     * on a subnet.
     *
     * @return [IgmpResponseTime]
     */
    fun maxResponseTime() = createResponseTime(superBuffer.getByte(responseTimeOffset).toUByte())

    /**
     * In a Membership Query message, the group address field is set to zero
     * when sending a General Query, and set to the group address being
     * queried when sending a Group-Specific Query.
     * In a Membership Report or Leave Group message, the group address
     * field holds the IP multicast group address of the group being
     * reported or left.
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
    fun groupAddress(address: Inet4Address): T = superBuffer.setBytes(groupAddressOffset, address.address).let {
        @Suppress("UNCHECKED_CAST")
        this as T
    }

    override fun size() = 8
}