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

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pcap.codec.ethernet.Ethernet
import pcap.codec.ip.Ip4
import sk.jt.igmp.fabric.packet.IgmpPacketParser.Companion.parseIgmpPacket
import sk.jt.igmp.fabric.packet.IgmpPacketTest.Companion.loadFileToPacketBuffer

internal class IgmpPacketParserTest : IgmpPacketTest {

    companion object {
        private fun parseIpv4Packet(packetFilePath: String): Ip4 {
            val packetBuffer = loadFileToPacketBuffer(packetFilePath)
            val ethernetFrame = packetBuffer.cast(Ethernet::class.java)
            return packetBuffer.readerIndex(ethernetFrame.size().toLong()).cast(Ip4::class.java)
        }
    }

    @Test
    fun parseInvalidProtocol() {
        assertThrows(IllegalArgumentException::class.java) {
            val ipv4Packet = parseIpv4Packet("/other/other_01")
            parseIgmpPacket(ipv4Packet)
        }
    }

    @Test
    fun parseIgmpV1MembershipQuery() {
        val ipv4Packet = parseIpv4Packet("/igmpv1/membership_query_01")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV1MembershipQuery)
    }

    @Test
    fun parseIgmpV1MembershipReport() {
        val ipv4Packet = parseIpv4Packet("/igmpv1/membership_report")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV1MembershipReport)
    }

    @Test
    fun parseIgmpV2MembershipQuery() {
        val ipv4Packet = parseIpv4Packet("/igmpv2/membership_query_01")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV2MembershipQuery)
    }

    @Test
    fun parseIgmpV2MembershipReport() {
        val ipv4Packet = parseIpv4Packet("/igmpv2/membership_report")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV2MembershipReport)
    }

    @Test
    fun parseIgmpV2LeaveGroup() {
        val ipv4Packet = parseIpv4Packet("/igmpv2/leave_group")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV2LeaveGroup)
    }

    @Test
    fun parseIgmpV3MembershipReport() {
        val ipv4Packet = parseIpv4Packet("/igmpv3/membership_report_06")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV3MembershipReport)
    }

    @Test
    fun parseIgmpV3MembershipQuery() {
        val ipv4Packet = parseIpv4Packet("/igmpv3/membership_query_04")
        val igmpMessage = parseIgmpPacket(ipv4Packet)
        assertTrue(igmpMessage is IgmpV3MembershipQuery)
    }
}