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

import pcap.codec.ip.Ip4
import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.types.IgmpType.Companion.fromByte
import sk.jt.igmp.fabric.types.IgmpType.IGMPV1_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.IGMPV2_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.IGMPV3_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.LEAVE_GROUP
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

/**
 * Tool used for parsing of [Igmp] from network [PacketBuffer].
 */
internal class IgmpPacketParser {

    companion object {
        /**
         * Parsing IGMP message from the [Ip4] packet payload.
         *
         * @param ipv4Packet parsed IPv4 packet (header and payload)
         * @throws IllegalArgumentException IGMP message cannot be parsed from the packet -
         * packet format does not conform any of the known IGMP types
         */
        fun parseIgmpPacket(ipv4Packet: Ip4): Igmp<*> {
            verifyProtocol(ipv4Packet)
            val igmpSize = ipv4Packet.totalLength() - ipv4Packet.size()
            verifyIgmpPacketSize(igmpSize)

            val packetBuffer = ipv4Packet.buffer()
            packetBuffer.readerIndex(packetBuffer.readerIndex() + ipv4Packet.size())
            return when (parseType(packetBuffer)) {
                MEMBERSHIP_QUERY -> parseMembershipQuery(packetBuffer, igmpSize)
                IGMPV1_MEMBERSHIP_REPORT -> packetBuffer.cast(IgmpV1MembershipReport::class.java)
                IGMPV2_MEMBERSHIP_REPORT -> packetBuffer.cast(IgmpV2MembershipReport::class.java)
                IGMPV3_MEMBERSHIP_REPORT -> packetBuffer.cast(IgmpV3MembershipReport::class.java)
                LEAVE_GROUP -> packetBuffer.cast(IgmpV2LeaveGroup::class.java)
            }
        }

        private fun verifyProtocol(ipv4Packet: Ip4) {
            require(ipv4Packet.protocol() == Igmp.TYPE) {
                "Packet does not contain IGMP payload - invalid protocol number: {${ipv4Packet.protocol()}}"
            }
        }

        private fun verifyIgmpPacketSize(igmpSize: Int) {
            require(igmpSize >= 8) {
                "The minimum size of the IGMP message is 8 bytes: {$igmpSize}"
            }
            require(igmpSize.mod(4) == 0) {
                "Size of the IGMP message must be multiple of number 4: {$igmpSize}"
            }
        }

        private fun parseType(packetBuffer: PacketBuffer) = fromByte(
            packetBuffer.getByte(packetBuffer.readerIndex()).toUByte()
        )

        private fun parseMembershipQuery(packetBuffer: PacketBuffer, igmpSize: Int): Igmp<*> {
            val responseType = packetBuffer.getByte(packetBuffer.readerIndex() + 1)
            if (responseType.toInt() == 0) {
                return packetBuffer.cast(IgmpV1MembershipQuery::class.java)
            }
            if (igmpSize == 8) {
                return packetBuffer.cast(IgmpV2MembershipQuery::class.java)
            }
            return packetBuffer.cast(IgmpV3MembershipQuery::class.java)
        }
    }
}