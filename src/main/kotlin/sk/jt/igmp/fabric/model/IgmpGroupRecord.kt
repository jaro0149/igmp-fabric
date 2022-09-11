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
package sk.jt.igmp.fabric.model

import java.net.Inet4Address
import sk.jt.igmp.fabric.types.IgmpAuxiliaryWord
import sk.jt.igmp.fabric.types.IgmpRecordType

/**
 * Each Group Record is a block of fields containing information
 * pertaining to the sender's membership in a single multicast group on
 * the interface from which the Report is sent.
 *
 * @property recordType [IgmpRecordType]
 * @property multicastAddress The Multicast Address field contains the IP multicast address to
 *     which this Group Record pertains.
 * @property sourceAddresses The Source Address fields are a vector of n IP unicast addresses,
 *     where n is the value in this record's Number of Sources (N) field.
 * @property auxiliaryData The Auxiliary Data field, if present, contains additional information
 *     pertaining to this Group Record.  The protocol specified in this
 *     document, IGMPv3, does not define any auxiliary data.  Therefore,
 *     implementations of IGMPv3 MUST NOT include any auxiliary data (i.e.,
 *     MUST set the Aux Data Len field to zero) in any transmitted Group
 *     Record, and MUST ignore any auxiliary data present in any received
 *     Group Record.  The semantics and internal encoding of the Auxiliary
 *     Data field are to be defined by any future version or extension of
 *     IGMP that uses this field.
 * @constructor creation of [IgmpGroupRecord]
 */
data class IgmpGroupRecord(
    val recordType: IgmpRecordType,
    val multicastAddress: Inet4Address,
    val sourceAddresses: List<Inet4Address>,
    val auxiliaryData: List<IgmpAuxiliaryWord>)