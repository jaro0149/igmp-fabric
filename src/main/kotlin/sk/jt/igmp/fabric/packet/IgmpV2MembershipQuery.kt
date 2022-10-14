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

import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.types.IgmpResponseTime
import sk.jt.igmp.fabric.types.IgmpResponseTime.Companion.createResponseTime
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

/**
 * IGMPv2 membership query message. Type is set to [IgmpType.MEMBERSHIP_QUERY].
 * Response time and group address fields are configurable.
 *
 * @param buffer packet payload
 * @constructor creation of IGMPv2 membership query message
 */
internal class IgmpV2MembershipQuery(buffer: PacketBuffer) : IgmpV2<IgmpV2MembershipQuery>(buffer) {

    init {
        super.type(MEMBERSHIP_QUERY)
    }

    override fun type(igmpType: IgmpType) = throw UnsupportedOperationException(
        "IGMPv2 Type of Membership Query message cannot be changed"
    )

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
     * Set maximum response time.
     *
     * @param responseTime [IgmpResponseTime]
     * @return [IgmpV2MembershipQuery]
     */
    fun maxResponseTime(responseTime: IgmpResponseTime) =
        superBuffer.setByte(responseTimeOffset, responseTime.value.toInt()).let { this }

    override fun toString() = "IgmpV2MembershipQuery(" +
            "type=${type()}, " +
            "maxResponseTime=${maxResponseTime()}, " +
            "checksum=${checksum()}, " +
            "groupAddress=${groupAddress()})"
}