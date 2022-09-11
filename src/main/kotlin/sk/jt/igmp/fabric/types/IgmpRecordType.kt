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

/**
 * There are a number of different types of Group Records that may be included in a IGMPv3 report message.
 */
enum class IgmpRecordType(val type: UByte) {
    /**
     * Indicates that the interface has a
     * filter mode of INCLUDE for the specified multicast
     * address.  The Source Address (i) fields in this Group
     * Record contain the interface's source list for the
     * specified multicast address, if it is non-empty.
     */
    MODE_IS_INCLUDE(0x01u),

    /**
     * Indicates that the interface has a
     * filter mode of EXCLUDE for the specified multicast
     * address.  The Source Address (i) fields in this Group
     * Record contain the interface's source list for the
     * specified multicast address, if it is non-empty.
     */
    MODE_IS_EXCLUDE(0x02u),

    /**
     * Indicates that the interface
     * has changed to INCLUDE filter mode for the specified
     * multicast address.  The Source Address (i) fields
     * in this Group Record contain the interface's new
     * source list for the specified multicast address,
     * if it is non-empty.
     */
    CHANGE_TO_INCLUDE_MODE(0x03u),

    /**
     * Indicates that the interface
     * has changed to EXCLUDE filter mode for the specified
     * multicast address.  The Source Address (i) fields
     * in this Group Record contain the interface's new
     * source list for the specified multicast address,
     * if it is non-empty.
     */
    CHANGE_TO_EXCLUDE_MODE(0x04u),

    /**
     * Indicates that the Source Address
     * (i) fields in this Group Record contain a list of the
     * additional sources that the system wishes to
     * hear from, for packets sent to the specified
     * multicast address.  If the change was to an INCLUDE
     * source list, these are the addresses that were added
     * to the list; if the change was to an EXCLUDE source
     * list, these are the addresses that were deleted from
     * the list.
     */
    ALLOW_NEW_SOURCES(0x05u),

    /**
     * Indicates that the Source Address
     * (i) fields in this Group Record contain a list of the
     * sources that the system no longer wishes to
     * hear from, for packets sent to the specified
     * multicast address.  If the change was to an INCLUDE
     * source list, these are the addresses that were
     * deleted from  the list; if the change was to an
     * EXCLUDE source list, these are the addresses that
     * were added to the list.
     */
    BLOCK_OLD_SOURCES(0x06u);

    companion object {
        /**
         * Deriving [IgmpRecordType] from byte in the IGMP packet.
         *
         * @param value [UByte] value
         * @return derived [IgmpRecordType]
         */
        fun fromByte(value: UByte) = IgmpRecordType.values().first { it.type == value }
    }
}