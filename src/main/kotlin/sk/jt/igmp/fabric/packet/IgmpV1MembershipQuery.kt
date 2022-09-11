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
import java.net.Inet4Address.getByAddress
import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

/**
 * IGMPv1 membership query message. Type is set to [IgmpType.MEMBERSHIP_QUERY] and group address is set to 0.0.0.0.
 *
 * @param buffer packet payload
 * @constructor creation of IGMPv1 membership query message
 */
internal class IgmpV1MembershipQuery(buffer: PacketBuffer) : IgmpV1<IgmpV1MembershipQuery>(buffer) {

    companion object {
        private val ALL_ZERO_ADDRESS = getByAddress(byteArrayOf(0, 0, 0, 0)) as Inet4Address
    }

    init {
        super.type(MEMBERSHIP_QUERY)
        super.groupAddress(ALL_ZERO_ADDRESS)
    }

    override fun type(igmpType: IgmpType) = throw UnsupportedOperationException(
        "IGMPv1 Type of Membership Query message cannot be changed"
    )

    override fun groupAddress(address: Inet4Address) = throw UnsupportedOperationException(
        "IGMPv1 Group Address cannot be set in the Membership Query message - it is always set to 0.0.0.0"
    )
}