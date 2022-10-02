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
import kotlin.experimental.and
import pcap.spi.PacketBuffer
import sk.jt.igmp.fabric.packet.IgmpPacketUtils.Companion.bit
import sk.jt.igmp.fabric.packet.IgmpPacketUtils.Companion.toIpv4Address
import sk.jt.igmp.fabric.types.IgmpMaxResponseCode
import sk.jt.igmp.fabric.types.IgmpMaxResponseCode.Companion.createResponseCode
import sk.jt.igmp.fabric.types.IgmpQuerierQueryIntervalCode
import sk.jt.igmp.fabric.types.IgmpQuerierQueryIntervalCode.Companion.createQueryIntervalCode
import sk.jt.igmp.fabric.types.IgmpQuerierRobustnessVariable
import sk.jt.igmp.fabric.types.IgmpQuerierRobustnessVariable.Companion.createQueryRobustnessVariable
import sk.jt.igmp.fabric.types.IgmpType
import sk.jt.igmp.fabric.types.IgmpType.MEMBERSHIP_QUERY

/**
 * IGMPv3 membership query message. Type is set to [IgmpType.MEMBERSHIP_QUERY]. Configurable fields:
 * - max response code
 * - group address
 * - suppress router side processing flag
 * - query robustness variable
 * - query interval code
 * - source addresses
 * ```
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Type = 0x11  | Max Resp Code |           Checksum            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Group Address                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Resv  |S| QRV |     QQIC      |     Number of Sources (N)     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Source Address [1]                      |
 * +-                                                             -+
 * |                       Source Address [2]                      |
 * +-                              .                              -+
 * .                               .                               .
 * .                               .                               .
 * +-                                                             -+
 * |                       Source Address [N]                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 *
 * @param buffer packet payload
 * @constructor creation of IGMPv3 membership query message
 */
internal class IgmpV3MembershipQuery(buffer: PacketBuffer) : IgmpV3<IgmpV3MembershipQuery>(buffer) {

    private val maxResponseCodeOffset = typeOffset + 1
    private val groupAddressOffset = checksumOffset + 2
    private val suppressAndRobustnessOffset = groupAddressOffset + 4
    private val queryIntervalCodeOffset = suppressAndRobustnessOffset + 1
    private val numberOfSourcesOffset = queryIntervalCodeOffset + 1
    private val sourceAddressOffset = numberOfSourcesOffset + 2

    init {
        super.type(MEMBERSHIP_QUERY)
        superBuffer.setByte(
            suppressAndRobustnessOffset,
            (superBuffer.getByte(suppressAndRobustnessOffset) and 0x1F).toInt()
        )
    }

    /**
     * The Max Resp Code field specifies the maximum time allowed before
     * sending a responding report.  The actual time allowed, called the Max
     * Resp Time, is represented in units of 1/10 second.
     * Small values of Max Resp Time allow IGMPv3 routers to tune the "leave
     * latency" (the time between the moment the last host leaves a group
     * and the moment the routing protocol is notified that there are no
     * more members).  Larger values, especially in the exponential range,
     * allow tuning of the burstiness of IGMP traffic on a network.
     *
     * @return [IgmpMaxResponseCode]
     */
    fun maxResponseCode() = createResponseCode(superBuffer.getByte(maxResponseCodeOffset).toUByte())

    /**
     * The Group Address field is set to zero when sending a General Query,
     * and set to the IP multicast address being queried when sending a
     * Group-Specific Query or Group-and-Source-Specific Query.
     *
     * @return [Inet4Address]
     */
    fun groupAddress() = superBuffer.getInt(groupAddressOffset).toIpv4Address()

    /**
     * When set to one, the S Flag indicates to any receiving multicast
     * routers that they are to suppress the normal timer updates they
     * perform upon hearing a Query.  It does not, however, suppress the
     * querier election or the normal "host-side" processing of a Query that
     * a router may be required to perform as a consequence of itself being
     * a group member.
     *
     * @return true if suppression is enabled
     */
    fun suppressRouterSideProcessing() = superBuffer.getByte(suppressAndRobustnessOffset) bit 4u

    /**
     * If non-zero, the QRV field contains the [Robustness Variable] value
     * used by the querier, i.e., the sender of the Query.  If the querier's
     * [Robustness Variable] exceeds 7, the maximum value of the QRV field,
     * the QRV is set to zero.  Routers adopt the QRV value from the most
     * recently received Query as their own [Robustness Variable] value,
     * unless that most recently received QRV was zero, in which case the
     * receivers use the default [Robustness Variable] value or a statically
     * configured value.
     *
     * @return [IgmpQuerierRobustnessVariable]
     */
    fun querierRobustnessVariable(): IgmpQuerierRobustnessVariable {
        val value = superBuffer.getByte(suppressAndRobustnessOffset).toUByte() and 0x07u
        return createQueryRobustnessVariable(value)
    }

    /**
     * The Querier's Query Interval Code field specifies the [Query
     * Interval] used by the querier.  The actual interval, called the
     * Querier's Query Interval (QQI), is represented in units of seconds.
     * Multicast routers that are not the current querier adopt the QQI
     * value from the most recently received Query as their own [Query
     * Interval] value, unless that most recently received QQI was zero, in
     * which case the receiving routers use the default [Query Interval]
     *
     * @return [IgmpQuerierQueryIntervalCode]
     */
    fun querierQueryIntervalCode(): IgmpQuerierQueryIntervalCode {
        val value = superBuffer.getByte(queryIntervalCodeOffset).toUByte()
        return createQueryIntervalCode(value)
    }

    /**
     * The Number of Sources (N) field specifies how many source addresses
     * are present in the Query.  This number is zero in a General Query or
     * a Group-Specific Query, and non-zero in a Group-and-Source-Specific
     * Query.
     *
     * @return [UShort] value
     */
    fun numberOfSources() = superBuffer.getShort(numberOfSourcesOffset).toUShort()

    /**
     * The Source Address fields are a vector of n IP unicast addresses,
     * where n is the value in the Number of Sources (N) field.
     *
     * @return [List] of [Inet4Address]s
     */
    fun sourceAddresses(): List<Inet4Address> {
        val sourceAddresses = ArrayList<Inet4Address>()
        var addressOffset = sourceAddressOffset
        for (addressIndex in 0 until numberOfSources().toInt()) {
            sourceAddresses.add(superBuffer.getInt(addressOffset).toIpv4Address())
            addressOffset += 4
        }
        return sourceAddresses
    }

    /**
     * Set max. response code.
     *
     * @param maxResponseCode [IgmpMaxResponseCode]
     * @return [IgmpV3MembershipQuery]
     */
    fun maxResponseCode(maxResponseCode: IgmpMaxResponseCode): IgmpV3MembershipQuery = superBuffer.setByte(
        maxResponseCodeOffset, maxResponseCode.code.toInt()
    ).let { this }

    /**
     * Set group address.
     *
     * @param address [Inet4Address]
     * @return [IgmpV3MembershipQuery]
     */
    fun groupAddress(address: Inet4Address) = superBuffer.setBytes(groupAddressOffset, address.address).let { this }

    /**
     * Set suppress router-side processing flag.
     *
     * @param suppress supress router-side processing
     * @return [IgmpV3MembershipQuery]
     */
    fun suppressRouterSideProcessing(suppress: Boolean): IgmpV3MembershipQuery {
        var value = superBuffer.getByte(suppressAndRobustnessOffset).toUByte()
        value = if (suppress) {
            value or 0x08u
        } else {
            value and 0xF7u
        }
        superBuffer.setByte(suppressAndRobustnessOffset, value.toInt())
        return this
    }

    /**
     * Set querier's robustness variable.
     *
     * @param variable [IgmpQuerierRobustnessVariable]
     * @return [IgmpV3MembershipQuery]
     */
    fun querierRobustnessVariable(variable: IgmpQuerierRobustnessVariable): IgmpV3MembershipQuery {
        var value = superBuffer.getByte(suppressAndRobustnessOffset).toUByte() and 0xF8u
        value = value or variable.value
        superBuffer.setByte(suppressAndRobustnessOffset, value.toInt())
        return this
    }

    /**
     * Set querier's query interval code.
     *
     * @param intervalCode [IgmpQuerierQueryIntervalCode]
     * @return [IgmpV3MembershipQuery]
     */
    fun querierQueryIntervalCode(intervalCode: IgmpQuerierQueryIntervalCode): IgmpV3MembershipQuery =
        superBuffer.setByte(
            queryIntervalCodeOffset, intervalCode.code.toInt()
        ).let { this }

    /**
     * Set list of source addresses.
     *
     * @param addresses [List] of [Inet4Address]es
     * @return [IgmpV3MembershipQuery]
     */
    fun sourceAddresses(addresses: List<Inet4Address>): IgmpV3MembershipQuery {
        var addressOffset = sourceAddressOffset
        for (address in addresses) {
            superBuffer.setBytes(addressOffset, address.address)
            addressOffset += 4
        }
        superBuffer.setShort(numberOfSourcesOffset, addresses.size)
        return this
    }

    override fun type(igmpType: IgmpType) = throw UnsupportedOperationException(
        "IGMPv3 Type of Membership Query message cannot be changed"
    )

    override fun size() = 12 + (superBuffer.getShort(superBuffer.readerIndex() + 10).toInt() * 4)

    override fun toString() = "IgmpV3MembershipQuery(" +
            "type=${type()}, " +
            "maxResponseCode=${maxResponseCode()}, " +
            "checksum=${checksum()}, " +
            "groupAddress=${groupAddress()}, " +
            "suppressRouterSideProcessing=${suppressRouterSideProcessing()}, " +
            "querierRobustnessVariable=${querierRobustnessVariable()}, " +
            "querierQueryIntervalCode=${querierQueryIntervalCode()}, " +
            "sourceAddresses=${sourceAddresses()})"
}