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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import sk.jt.igmp.fabric.types.IgmpType.IGMPV1_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

internal class IgmpV1PacketTest : IgmpPacketTest {

    @Test
    fun parseMembershipQuery() {
        val membershipQuery = parseIgmpMessage("/igmpv1/membership_query_02", IgmpV1MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(61183u, membershipQuery.checksum())
        assertEquals(getByAddress(byteArrayOf(0, 0, 0, 0)), membershipQuery.groupAddress())
        assertTrue(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseMembershipQuery_invalidChecksum() {
        val membershipQuery = parseIgmpMessage("/igmpv1/membership_query_01", IgmpV1MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(60415u, membershipQuery.checksum())
        assertEquals(getByAddress(byteArrayOf(0, 0, 0, 0)), membershipQuery.groupAddress())
        assertFalse(membershipQuery.isValidChecksum())
    }

    @Test
    fun parseMembershipReport() {
        val membershipReport = parseIgmpMessage("/igmpv1/membership_report", IgmpV1MembershipReport::class.java)
        assertEquals(IGMPV1_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(3331u, membershipReport.checksum())
        assertEquals(getByAddress(byteArrayOf(224.toByte(), 0, 0, 252.toByte())), membershipReport.groupAddress())
        assertTrue(membershipReport.isValidChecksum())
    }

    @Test
    fun createMembershipQuery() {
        val membershipQuery = createIgmpMessage(IgmpV1MembershipQuery::class.java, 8)
        membershipQuery.checksum(membershipQuery.calculateChecksum())

        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(61183u, membershipQuery.checksum())
        assertEquals(getByAddress(byteArrayOf(0, 0, 0, 0)), membershipQuery.groupAddress())
        assertTrue(membershipQuery.isValidChecksum())
    }

    @Test
    fun createMembershipQuery_setInvalidField() {
        val membershipQuery = createIgmpMessage(IgmpV1MembershipQuery::class.java, 8)

        assertThrows(UnsupportedOperationException::class.java) {
            membershipQuery.groupAddress(getByAddress(byteArrayOf(224.toByte(), 0, 0, 252.toByte())) as Inet4Address)
        }
    }

    @Test
    fun createMembershipReport() {
        val membershipReport = createIgmpMessage(IgmpV1MembershipReport::class.java, 8)
        membershipReport.groupAddress(getByAddress(byteArrayOf(224.toByte(), 0, 0, 252.toByte())) as Inet4Address)
        membershipReport.checksum(membershipReport.calculateChecksum())

        assertEquals(IGMPV1_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(3331u, membershipReport.checksum())
        assertEquals(getByAddress(byteArrayOf(224.toByte(), 0, 0, 252.toByte())), membershipReport.groupAddress())
        assertTrue(membershipReport.isValidChecksum())
    }
}