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
import sk.jt.igmp.fabric.model.IgmpGroupRecord
import sk.jt.igmp.fabric.packet.IgmpPacketUtils.Companion.toIpv4Address
import sk.jt.igmp.fabric.types.IgmpAuxiliaryWord.Companion.createWords
import sk.jt.igmp.fabric.types.IgmpRecordType.Companion.fromByte
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.IGMPV3_MEMBERSHIP_REPORT

/**
 * IGMPv3 membership report message. Type is set to [IgmpType.IGMPV3_MEMBERSHIP_REPORT].
 * Group records are configurable.
 * ```
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Type = 0x22  |    Reserved   |           Checksum            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           Reserved            |  Number of Group Records (M)  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * .                                                               .
 * .                        Group Record [1]                       .
 * .                                                               .
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * .                                                               .
 * .                        Group Record [2]                       .
 * .                                                               .
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               .                               |
 * .                               .                               .
 * |                               .                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * .                                                               .
 * .                        Group Record [M]                       .
 * .                                                               .
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 * Internal structure of group record:
 * ```
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Record Type  |  Aux Data Len |     Number of Sources (N)     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Multicast Address                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Source Address [1]                      |
 * +-                                                             -+
 * |                       Source Address [2]                      |
 * +-                                                             -+
 * .                               .                               .
 * .                               .                               .
 * .                               .                               .
 * +-                                                             -+
 * |                       Source Address [N]                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * .                                                               .
 * .                         Auxiliary Data                        .
 * .                                                               .
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 *
 * @param buffer packet payload
 * @constructor creation of IGMPv3 membership report message
 */
internal class IgmpV3MembershipReport(buffer: PacketBuffer) : IgmpV3<IgmpV3MembershipReport>(buffer) {

    private val numberOfGroupRecordsOffset = typeOffset + 6
    private val groupRecordOffset = numberOfGroupRecordsOffset + 2

    init {
        super.type(IGMPV3_MEMBERSHIP_REPORT)
    }

    override fun type(igmpType: IgmpType) = throw UnsupportedOperationException(
        "IGMPv3 Type of Membership Report message cannot be changed"
    )

    /**
     * The Number of Group Records (M) field specifies how many Group
     * Records are present in this Report.
     *
     * @return [UShort] value
     */
    fun numberOfGroupRecords() = superBuffer.getShort(numberOfGroupRecordsOffset).toUShort()

    /**
     * Each Group Record is a block of fields containing information
     * pertaining to the sender's membership in a single multicast group on
     * the interface from which the Report is sent.
     *
     * @return [List] of [IgmpGroupRecord]s
     */
    fun groupRecords(): List<IgmpGroupRecord> {
        val groupRecords = ArrayList<IgmpGroupRecord>()
        var recordTypeOffset = groupRecordOffset
        for (groupRecordIndex in 0 until numberOfGroupRecords().toInt()) {
            val auxDataLenOffset = recordTypeOffset + 1
            val numberOfSourcesOffset = auxDataLenOffset + 1
            val multicastAddressOffset = numberOfSourcesOffset + 2
            val sourceAddressOffset = multicastAddressOffset + 4

            val recordType = fromByte(superBuffer.getByte(recordTypeOffset).toUByte())
            val auxDataLen = superBuffer.getByte(auxDataLenOffset).toLong()
            val numberOfSources = superBuffer.getShort(numberOfSourcesOffset).toInt()
            val multicastAddress = superBuffer.getInt(multicastAddressOffset).toIpv4Address()

            val sourceAddresses = ArrayList<Inet4Address>()
            var addressOffset = sourceAddressOffset
            for (sourceAddressIndex in 0 until numberOfSources) {
                sourceAddresses.add(superBuffer.getInt(addressOffset).toIpv4Address())
                addressOffset += 4
            }

            val auxiliaryDataOffset = sourceAddressOffset + (numberOfSources * 4)
            val auxiliaryData = ByteArray(auxDataLen.toInt() * 4)
            superBuffer.getBytes(auxiliaryDataOffset, auxiliaryData, 0, auxDataLen)

            groupRecords.add(IgmpGroupRecord(recordType, multicastAddress, sourceAddresses, createWords(auxiliaryData)))
            recordTypeOffset = auxiliaryDataOffset + (auxDataLen * 4)
        }
        return groupRecords
    }

    /**
     * Set IGMP group records.
     *
     * @param records [List] of [IgmpGroupRecord]s
     * @return [IgmpV3MembershipReport]
     */
    fun groupRecords(records: List<IgmpGroupRecord>): IgmpV3MembershipReport {
        var recordTypeOffset = groupRecordOffset
        for (record in records) {
            val auxDataLenOffset = recordTypeOffset + 1
            val numberOfSourcesOffset = auxDataLenOffset + 1
            val multicastAddressOffset = numberOfSourcesOffset + 2
            val sourceAddressOffset = multicastAddressOffset + 4
            val auxiliaryDataOffset = sourceAddressOffset + (record.sourceAddresses.size * 4)

            superBuffer.setByte(recordTypeOffset, record.recordType.type.toInt())
            superBuffer.setByte(numberOfSourcesOffset, record.auxiliaryData.size / 4)
            superBuffer.setBytes(multicastAddressOffset, record.multicastAddress.address)

            var addressOffset = sourceAddressOffset
            for (sourceAddress in record.sourceAddresses) {
                superBuffer.setBytes(addressOffset, sourceAddress.address)
                addressOffset += 4
            }

            var auxiliaryWordOffset = auxiliaryDataOffset
            for (word in record.auxiliaryData) {
                superBuffer.setBytes(auxiliaryWordOffset, word.toByteArray())
                auxiliaryWordOffset += 4
            }
            recordTypeOffset = auxiliaryDataOffset + (record.auxiliaryData.size * 4)
        }

        superBuffer.setShort(numberOfGroupRecordsOffset, records.size)
        return this
    }

    override fun size(): Int {
        var groupRecordsSize = 0
        var recordTypeOffset = groupRecordOffset
        for (groupRecordIndex in 0 until numberOfGroupRecords().toInt()) {
            val auxDataLenOffset = recordTypeOffset + 1
            val numberOfSourcesOffset = auxDataLenOffset + 1
            val auxDataLen = superBuffer.getByte(auxDataLenOffset).toInt()
            val numberOfSources = superBuffer.getShort(numberOfSourcesOffset).toInt()

            val groupRecordSize = 8 + (auxDataLen * 4) + (numberOfSources * 4)
            groupRecordsSize += groupRecordSize
            recordTypeOffset += groupRecordSize
        }
        return 8 + groupRecordsSize
    }
}