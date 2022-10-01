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
import sk.jt.igmp.fabric.types.IgmpResponseTime.Companion.ZERO_RESPONSE_TIME
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.IGMPV2_MEMBERSHIP_REPORT

/**
 * IGMPv2 membership query message. Type is set to [IgmpType.IGMPV2_MEMBERSHIP_REPORT] and response time is set to 0.
 * Group address is configurable.
 *
 * @param buffer packet payload
 * @constructor creation of IGMPv2 membership report message
 */
internal class IgmpV2MembershipReport(buffer: PacketBuffer) : IgmpV2<IgmpV2MembershipReport>(buffer) {

    init {
        super.type(IGMPV2_MEMBERSHIP_REPORT)
        super.maxResponseTime(ZERO_RESPONSE_TIME)
    }

    override fun type(igmpType: IgmpType) = throw UnsupportedOperationException(
        "IGMPv2 Type of Membership Report message cannot be changed"
    )

    override fun maxResponseTime(responseTime: IgmpResponseTime) = throw UnsupportedOperationException(
        "IGMPv2 Max. Response Time cannot be set in the Membership Report message - it is always set to 0"
    )
}