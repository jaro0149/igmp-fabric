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
package sk.jt.igmp.fabric.types

import sk.jt.igmp.fabric.packet.IgmpV1MembershipQuery
import sk.jt.igmp.fabric.packet.IgmpV1MembershipReport
import sk.jt.igmp.fabric.packet.IgmpV2LeaveGroup
import sk.jt.igmp.fabric.packet.IgmpV2MembershipQuery
import sk.jt.igmp.fabric.packet.IgmpV2MembershipReport
import sk.jt.igmp.fabric.packet.IgmpV3MembershipQuery
import sk.jt.igmp.fabric.packet.IgmpV3MembershipReport

/**
 * Type of the IGMP message.
 */
internal enum class IgmpType(val type: UByte) {
    /**
     * IGMP membership query message (common for IGMPv1, IGMPv2, and IGMPv3).
     *
     * @see [IgmpV1MembershipQuery]
     * @see [IgmpV2MembershipQuery]
     * @see [IgmpV3MembershipQuery]
     */
    MEMBERSHIP_QUERY(0x11u),

    /**
     * IGMPv1 membership report message.
     *
     * @see [IgmpV1MembershipReport]
     */
    IGMPV1_MEMBERSHIP_REPORT(0x12u),

    /**
     * IGMPv2 membership report message.
     *
     * @see [IgmpV2MembershipReport]
     */
    IGMPV2_MEMBERSHIP_REPORT(0x16u),

    /**
     * IGMPv3 membership report message.
     *
     * @see [IgmpV3MembershipReport]
     */
    IGMPV3_MEMBERSHIP_REPORT(0x22u),

    /**
     * IGMP leave group message (specified by IGMPv2 but used by both IGMPv2 and IGMPv3).
     *
     * @see [IgmpV2LeaveGroup]
     */
    LEAVE_GROUP(0x17u);

    companion object {
        /**
         * Deriving [IgmpType] from IGMP type byte field.
         *
         * @param value [UByte] value
         * @return derived [IgmpType]
         */
        fun fromByte(value: UByte) = IgmpType.values().first { it.type == value }
    }
}