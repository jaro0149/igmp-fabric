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

import java.math.BigDecimal
import java.net.Inet4Address
import java.net.Inet4Address.getByName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import sk.jt.igmp.fabric.types.IgmpResponseTime.Companion.createResponseTime
import sk.jt.igmp.fabric.types.IgmpType.IGMPV2_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.LEAVE_GROUP
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

internal class IgmpV2PacketTest : IgmpPacketTest {

    @Test
    fun parseMembershipQuery_general() {
        val membershipQuery = parseIgmpMessage("/igmpv2/membership_query_01", IgmpV2MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0xee9b.toUShort(), membershipQuery.checksum())
        assertEquals(getByName("0.0.0.0"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("10.0"), membershipQuery.maxResponseTime().seconds())
        assertTrue(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseMembershipQuery_specificGroup() {
        val membershipQuery = parseIgmpMessage("/igmpv2/membership_query_02", IgmpV2MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x0cf0.toUShort(), membershipQuery.checksum())
        assertEquals(getByName("225.1.1.4"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("1.0"), membershipQuery.maxResponseTime().seconds())
        assertTrue(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseMembershipQuery_invalidChecksum() {
        val membershipQuery = parseIgmpMessage("/igmpv2/membership_query_03", IgmpV2MembershipQuery::class.java)
        assertFalse(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseLeaveGroup() {
        val leaveGroup = parseIgmpMessage("/igmpv2/leave_group", IgmpV2LeaveGroup::class.java)
        assertEquals(LEAVE_GROUP, leaveGroup.type())
        assertEquals(0x06fa.toUShort(), leaveGroup.checksum())
        assertEquals(getByName("225.1.1.4"), leaveGroup.groupAddress())
        assertTrue(leaveGroup.isValidChecksum())
    }

    @Test
    fun parseMembershipReport() {
        val membershipReport = parseIgmpMessage("/igmpv2/membership_report", IgmpV2MembershipReport::class.java)
        assertEquals(IGMPV2_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0xfa04.toUShort(), membershipReport.checksum())
        assertEquals(getByName("239.255.255.250"), membershipReport.groupAddress())
        assertTrue(membershipReport.isValidChecksum())
    }

    @Test
    fun createLeaveGroup() {
        val groupAddress = getByName("239.255.255.250") as Inet4Address
        val leaveGroup = createIgmpMessage(IgmpV2LeaveGroup::class.java, 8)
            .groupAddress(groupAddress)
            .addChecksum()

        assertEquals(LEAVE_GROUP, leaveGroup.type())
        assertEquals(0xf904.toUShort(), leaveGroup.checksum())
        assertEquals(groupAddress, leaveGroup.groupAddress())
        assertEquals(0u.toByte(), leaveGroup.buffer().getByte(1))
        assertTrue(leaveGroup.isValidChecksum())
    }

    @Test
    fun createMembershipReport() {
        val groupAddress = getByName("224.0.0.1") as Inet4Address
        val membershipReport = createIgmpMessage(IgmpV2MembershipReport::class.java, 8)
            .groupAddress(groupAddress)
            .addChecksum()

        assertEquals(IGMPV2_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0x09fe.toUShort(), membershipReport.checksum())
        assertEquals(groupAddress, membershipReport.groupAddress())
        assertEquals(0u.toByte(), membershipReport.buffer().getByte(1))
        assertTrue(membershipReport.isValidChecksum())
    }

    @Test
    fun createMembershipQuery() {
        val groupAddress = getByName("224.0.0.1") as Inet4Address
        val membershipQuery = createIgmpMessage(IgmpV2MembershipQuery::class.java, 8)
            .groupAddress(groupAddress)
            .maxResponseTime(createResponseTime(BigDecimal("11.1")))
            .addChecksum()

        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x0e8f.toUShort(), membershipQuery.checksum())
        assertEquals(groupAddress, membershipQuery.groupAddress())
        assertEquals(BigDecimal("11.1"), membershipQuery.maxResponseTime().seconds())
        assertTrue(membershipQuery.isValidChecksum())
    }
}