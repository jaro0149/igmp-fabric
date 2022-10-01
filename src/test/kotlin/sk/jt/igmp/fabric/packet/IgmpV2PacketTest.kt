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
import java.math.BigDecimal.TEN
import java.net.Inet4Address
import java.net.Inet4Address.getByAddress
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
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
        assertEquals(getByAddress(byteArrayOf(0, 0, 0, 0)), membershipQuery.groupAddress())
        assertEquals(BigDecimal("10.0"), membershipQuery.maxResponseTime().seconds())
        assertTrue(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseMembershipQuery_specificGroup() {
        val membershipQuery = parseIgmpMessage("/igmpv2/membership_query_02", IgmpV2MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x0cf0.toUShort(), membershipQuery.checksum())
        assertEquals(getByAddress(byteArrayOf(225.toByte(), 1, 1, 4)), membershipQuery.groupAddress())
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
        assertEquals(getByAddress(byteArrayOf(225.toByte(), 1, 1, 4)), leaveGroup.groupAddress())
        assertEquals(0u, leaveGroup.maxResponseTime().value)
        assertTrue(leaveGroup.isValidChecksum())
    }

    @Test
    fun parseMembershipReport() {
        val membershipReport = parseIgmpMessage("/igmpv2/membership_report", IgmpV2MembershipReport::class.java)
        assertEquals(IGMPV2_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0xfa04.toUShort(), membershipReport.checksum())
        assertEquals(
            getByAddress(byteArrayOf(239.toByte(), 255.toByte(), 255.toByte(), 250.toByte())),
            membershipReport.groupAddress())
        assertEquals(0u, membershipReport.maxResponseTime().value)
        assertTrue(membershipReport.isValidChecksum())
    }

    @Test
    fun createMembershipReport_setInvalidField() {
        val membershipReport = createIgmpMessage(IgmpV2MembershipReport::class.java, 8)
        assertThrows(UnsupportedOperationException::class.java) {
            membershipReport.maxResponseTime(createResponseTime(TEN))
        }
    }

    @Test
    fun createLeaveGroup_setInvalidField() {
        val leaveGroup = createIgmpMessage(IgmpV2LeaveGroup::class.java, 8)
        assertThrows(UnsupportedOperationException::class.java) {
            leaveGroup.maxResponseTime(createResponseTime(TEN))
        }
    }

    @Test
    fun createLeaveGroup() {
        val leaveGroup = createIgmpMessage(IgmpV2LeaveGroup::class.java, 8)
        val groupAddress = getByAddress(byteArrayOf(239.toByte(), 255.toByte(), 255.toByte(), 250.toByte()))
                as Inet4Address
        leaveGroup.groupAddress(groupAddress)
        leaveGroup.checksum(leaveGroup.calculateChecksum())

        assertEquals(LEAVE_GROUP, leaveGroup.type())
        assertEquals(0xf904.toUShort(), leaveGroup.checksum())
        assertEquals(groupAddress, leaveGroup.groupAddress())
        assertEquals(0u, leaveGroup.maxResponseTime().value)
        assertTrue(leaveGroup.isValidChecksum())
    }

    @Test
    fun createMembershipReport() {
        val groupAddress = getByAddress(byteArrayOf(224.toByte(), 0, 0, 1)) as Inet4Address
        val membershipReport = createIgmpMessage(IgmpV2MembershipReport::class.java, 8)
            .groupAddress(groupAddress)
        membershipReport.checksum(membershipReport.calculateChecksum())

        assertEquals(IGMPV2_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0x09fe.toUShort(), membershipReport.checksum())
        assertEquals(groupAddress, membershipReport.groupAddress())
        assertEquals(0u, membershipReport.maxResponseTime().value)
        assertTrue(membershipReport.isValidChecksum())
    }

    @Test
    fun createMembershipQuery() {
        val groupAddress = getByAddress(byteArrayOf(224.toByte(), 0, 0, 1)) as Inet4Address
        val membershipQuery = createIgmpMessage(IgmpV2MembershipQuery::class.java, 8)
            .groupAddress(groupAddress)
            .maxResponseTime(createResponseTime(BigDecimal("11.1")))
        membershipQuery.checksum(membershipQuery.calculateChecksum())

        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x0e8f.toUShort(), membershipQuery.checksum())
        assertEquals(groupAddress, membershipQuery.groupAddress())
        assertEquals(BigDecimal("11.1"), membershipQuery.maxResponseTime().seconds())
        assertTrue(membershipQuery.isValidChecksum())
    }
}