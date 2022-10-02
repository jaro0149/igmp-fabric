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
import java.util.Collections.singletonList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import sk.jt.igmp.fabric.model.IgmpGroupRecord
import sk.jt.igmp.fabric.types.IgmpAuxiliaryWord
import sk.jt.igmp.fabric.types.IgmpMaxResponseCode.Companion.createResponseCode
import sk.jt.igmp.fabric.types.IgmpQuerierQueryIntervalCode.Companion.createQueryIntervalCodeFromSeconds
import sk.jt.igmp.fabric.types.IgmpQuerierRobustnessVariable.Companion.createQueryRobustnessVariable
import sk.jt.igmp.fabric.types.IgmpRecordType.ALLOW_NEW_SOURCES
import sk.jt.igmp.fabric.types.IgmpRecordType.CHANGE_TO_EXCLUDE_MODE
import sk.jt.igmp.fabric.types.IgmpRecordType.CHANGE_TO_INCLUDE_MODE
import sk.jt.igmp.fabric.types.IgmpRecordType.MODE_IS_EXCLUDE
import sk.jt.igmp.fabric.types.IgmpRecordType.MODE_IS_INCLUDE
import sk.jt.igmp.fabric.types.IgmpType.IGMPV3_MEMBERSHIP_REPORT
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

internal class IgmpV3PacketTest : IgmpPacketTest {

    @Test
    fun parseMembershipQuery_general() {
        val membershipQuery = parseIgmpMessage("/igmpv3/membership_query_01", IgmpV3MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0xecd3.toUShort(), membershipQuery.checksum())
        assertTrue(membershipQuery.isValidChecksum())
        assertEquals(getByName("0.0.0.0"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("2.4"), membershipQuery.maxResponseCode().seconds())
        assertEquals(BigDecimal("20"), membershipQuery.querierQueryIntervalCode().seconds())
        assertFalse(membershipQuery.suppressRouterSideProcessing())
        assertEquals(2u.toUByte(), membershipQuery.querierRobustnessVariable().value)
        assertEquals(0u.toUShort(), membershipQuery.numberOfSources())
        assertTrue(membershipQuery.sourceAddresses().isEmpty())
    }

    @Test
    fun parseMembershipQuery_specificGroup() {
        val membershipQuery = parseIgmpMessage("/igmpv3/membership_query_03", IgmpV3MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x97f0.toUShort(), membershipQuery.checksum())
        assertTrue(membershipQuery.isValidChecksum())
        assertEquals(getByName("232.100.100.100"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("1.0"), membershipQuery.maxResponseCode().seconds())
        assertEquals(BigDecimal("60"), membershipQuery.querierQueryIntervalCode().seconds())
        assertTrue(membershipQuery.suppressRouterSideProcessing())
        assertEquals(2u.toUByte(), membershipQuery.querierRobustnessVariable().value)
        assertEquals(0u.toUShort(), membershipQuery.numberOfSources())
        assertTrue(membershipQuery.sourceAddresses().isEmpty())
    }

    @Test
    fun parseMembershipQuery_multipleSources() {
        val membershipQuery = parseIgmpMessage("/igmpv3/membership_query_04", IgmpV3MembershipQuery::class.java)
        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x5dd2.toUShort(), membershipQuery.checksum())
        assertTrue(membershipQuery.isValidChecksum())
        assertEquals(getByName("232.100.100.100"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("1.0"), membershipQuery.maxResponseCode().seconds())
        assertEquals(BigDecimal("60"), membershipQuery.querierQueryIntervalCode().seconds())
        assertFalse(membershipQuery.suppressRouterSideProcessing())
        assertEquals(2u.toUByte(), membershipQuery.querierRobustnessVariable().value)
        assertEquals(2u.toUShort(), membershipQuery.numberOfSources())
        assertEquals(
            listOf(
                getByName("192.168.224.100"),
                getByName("192.168.224.101")
            ), membershipQuery.sourceAddresses()
        )
    }

    @Test
    fun parseMembershipQuery_invalidChecksum() {
        val membershipQuery = parseIgmpMessage("/igmpv3/membership_query_02", IgmpV3MembershipQuery::class.java)
        assertFalse(membershipQuery.isValidChecksum())
    }

    @Test
    fun createMembershipQuery() {
        val sourceAddresses = listOf(
            getByName("10.168.224.100") as Inet4Address,
            getByName("10.168.224.101") as Inet4Address
        )
        val membershipQuery = createIgmpMessage(IgmpV3MembershipQuery::class.java, 20)
            .suppressRouterSideProcessing(true)
            .querierRobustnessVariable(createQueryRobustnessVariable(6u))
            .querierQueryIntervalCode(createQueryIntervalCodeFromSeconds(50u))
            .maxResponseCode(createResponseCode(BigDecimal("8.5")))
            .groupAddress(getByName("192.168.224.100") as Inet4Address)
            .sourceAddresses(sourceAddresses)
        membershipQuery.checksum(membershipQuery.calculateChecksum())

        assertEquals(MEMBERSHIP_QUERY, membershipQuery.type())
        assertEquals(0x694e.toUShort(), membershipQuery.checksum())
        assertTrue(membershipQuery.isValidChecksum())
        assertEquals(getByName("192.168.224.100"), membershipQuery.groupAddress())
        assertEquals(BigDecimal("8.5"), membershipQuery.maxResponseCode().seconds())
        assertEquals(BigDecimal("50"), membershipQuery.querierQueryIntervalCode().seconds())
        assertTrue(membershipQuery.suppressRouterSideProcessing())
        assertEquals(6u.toUByte(), membershipQuery.querierRobustnessVariable().value)
        assertEquals(2u.toUShort(), membershipQuery.numberOfSources())
        assertEquals(sourceAddresses, membershipQuery.sourceAddresses())
    }

    @Test
    fun parseMembershipReport_oneGroupAnySources() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_01", IgmpV3MembershipReport::class.java)
        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0xe338.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(1u.toUShort(), membershipReport.numberOfGroupRecords())

        val groupRecords = membershipReport.groupRecords()
        assertEquals(1, groupRecords.size)
        val groupRecord = groupRecords[0]
        assertEquals(CHANGE_TO_EXCLUDE_MODE, groupRecord.recordType)
        assertEquals(getByName("239.195.7.2"), groupRecord.multicastAddress)
        assertTrue(groupRecord.sourceAddresses.isEmpty())
        assertTrue(groupRecord.auxiliaryData.isEmpty())
    }

    @Test
    fun parseMembershipReport_twoGroupsAnySources() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_02", IgmpV3MembershipReport::class.java)
        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0xf33c.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(2u.toUShort(), membershipReport.numberOfGroupRecords())

        val groupRecords = membershipReport.groupRecords()
        assertEquals(2, groupRecords.size)
        val groupRecord1 = groupRecords[0]
        assertEquals(MODE_IS_EXCLUDE, groupRecord1.recordType)
        assertEquals(getByName("239.195.7.2"), groupRecord1.multicastAddress)
        assertTrue(groupRecord1.sourceAddresses.isEmpty())
        assertTrue(groupRecord1.auxiliaryData.isEmpty())

        val groupRecord2 = groupRecords[1]
        assertEquals(MODE_IS_EXCLUDE, groupRecord2.recordType)
        assertEquals(getByName("239.255.255.250"), groupRecord2.multicastAddress)
        assertTrue(groupRecord2.sourceAddresses.isEmpty())
        assertTrue(groupRecord2.auxiliaryData.isEmpty())
    }

    @Test
    fun parseMembershipReport_oneGroupOneSource() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_03", IgmpV3MembershipReport::class.java)
        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0x4eeb.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(1u.toUShort(), membershipReport.numberOfGroupRecords())

        val groupRecords = membershipReport.groupRecords()
        assertEquals(1, groupRecords.size)
        val groupRecord = groupRecords[0]
        assertEquals(CHANGE_TO_INCLUDE_MODE, groupRecord.recordType)
        assertEquals(getByName("232.2.3.2"), groupRecord.multicastAddress)
        assertEquals(1, groupRecord.sourceAddresses.size)
        assertEquals(getByName("192.168.224.100"), groupRecord.sourceAddresses[0])
        assertTrue(groupRecord.auxiliaryData.isEmpty())
    }

    @Test
    fun parseMembershipReport_twoGroupsThreeSources() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_04", IgmpV3MembershipReport::class.java)
        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0x2bd0.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(2u.toUShort(), membershipReport.numberOfGroupRecords())

        val groupRecords = membershipReport.groupRecords()
        assertEquals(2, groupRecords.size)
        val groupRecord1 = groupRecords[0]
        assertEquals(MODE_IS_INCLUDE, groupRecord1.recordType)
        assertEquals(getByName("232.2.3.2"), groupRecord1.multicastAddress)
        assertEquals(1, groupRecord1.sourceAddresses.size)
        assertEquals(getByName("192.168.224.100"), groupRecord1.sourceAddresses[0])
        assertTrue(groupRecord1.auxiliaryData.isEmpty())

        val groupRecord2 = groupRecords[1]
        assertEquals(MODE_IS_EXCLUDE, groupRecord2.recordType)
        assertEquals(getByName("224.0.0.251"), groupRecord2.multicastAddress)
        assertEquals(2, groupRecord2.sourceAddresses.size)
        assertEquals(getByName("192.168.224.100"), groupRecord2.sourceAddresses[0])
        assertEquals(getByName("192.168.224.101"), groupRecord2.sourceAddresses[1])
        assertTrue(groupRecord1.auxiliaryData.isEmpty())
    }

    @Test
    fun parseMembershipReport_withAuxData() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_05", IgmpV3MembershipReport::class.java)
        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0xd8c5.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(1u.toUShort(), membershipReport.numberOfGroupRecords())

        val groupRecords = membershipReport.groupRecords()
        assertEquals(1, groupRecords.size)
        val groupRecord = groupRecords[0]
        assertEquals(CHANGE_TO_EXCLUDE_MODE, groupRecord.recordType)
        assertTrue(groupRecord.sourceAddresses.isEmpty())
        assertEquals(2, groupRecord.auxiliaryData.size)
        assertEquals(listOf<UByte>(1u, 2u, 3u, 4u), groupRecord.auxiliaryData[0].bytes)
        assertEquals(listOf<UByte>(5u, 6u, 7u, 8u), groupRecord.auxiliaryData[1].bytes)
    }

    @Test
    fun parseMembershipReport_invalidChecksum() {
        val membershipReport = parseIgmpMessage("/igmpv3/membership_report_06", IgmpV3MembershipReport::class.java)
        assertFalse(membershipReport.isValidChecksum())
    }

    @Test
    fun createMembershipReport() {
        val groupRecords = listOf(
            IgmpGroupRecord(
                recordType = CHANGE_TO_EXCLUDE_MODE,
                multicastAddress = getByName("10.0.0.1") as Inet4Address,
                sourceAddresses = listOf(
                    getByName("192.168.0.1") as Inet4Address,
                    getByName("192.168.0.2") as Inet4Address
                ),
                auxiliaryData = listOf(
                    IgmpAuxiliaryWord.createWord(listOf(1u, 2u, 3u, 4u)),
                    IgmpAuxiliaryWord.createWord(listOf(5u, 6u, 7u, 8u))
                )
            ),
            IgmpGroupRecord(
                recordType = ALLOW_NEW_SOURCES,
                multicastAddress = getByName("10.0.0.2") as Inet4Address,
                sourceAddresses = emptyList(),
                auxiliaryData = singletonList(IgmpAuxiliaryWord.createWord(listOf(10u, 20u, 30u, 40u)))
            )
        )
        val membershipReport = createIgmpMessage(IgmpV3MembershipReport::class.java, 44)
            .groupRecords(groupRecords)
        membershipReport.checksum(membershipReport.calculateChecksum())

        assertEquals(IGMPV3_MEMBERSHIP_REPORT, membershipReport.type())
        assertEquals(0x751.toUShort(), membershipReport.checksum())
        assertTrue(membershipReport.isValidChecksum())
        assertEquals(2u.toUShort(), membershipReport.numberOfGroupRecords())
        val records = membershipReport.groupRecords()
        assertEquals(2, records.size)

        val groupRecord1 = records[0]
        assertEquals(CHANGE_TO_EXCLUDE_MODE, groupRecord1.recordType)
        assertEquals(getByName("10.0.0.1"), groupRecord1.multicastAddress)
        assertEquals(2, groupRecord1.sourceAddresses.size)
        assertEquals(getByName("192.168.0.1"), groupRecord1.sourceAddresses[0])
        assertEquals(getByName("192.168.0.2"), groupRecord1.sourceAddresses[1])
        assertEquals(2, groupRecord1.auxiliaryData.size)
        assertEquals(listOf<UByte>(1u, 2u, 3u, 4u), groupRecord1.auxiliaryData[0].bytes)
        assertEquals(listOf<UByte>(5u, 6u, 7u, 8u), groupRecord1.auxiliaryData[1].bytes)

        val groupRecord2 = records[1]
        assertEquals(ALLOW_NEW_SOURCES, groupRecord2.recordType)
        assertEquals(getByName("10.0.0.2"), groupRecord2.multicastAddress)
        assertTrue(groupRecord2.sourceAddresses.isEmpty())
        assertEquals(1, groupRecord2.auxiliaryData.size)
        assertEquals(listOf<UByte>(10u, 20u, 30u, 40u), groupRecord2.auxiliaryData[0].bytes)
    }
}