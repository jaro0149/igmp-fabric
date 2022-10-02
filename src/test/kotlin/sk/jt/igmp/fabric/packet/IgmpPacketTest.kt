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

import java.io.File
import java.util.regex.Pattern
import pcap.codec.ethernet.Ethernet
import pcap.codec.ip.Ip4
import pcap.spi.PacketBuffer
import pcap.spi.Pcap
import pcap.spi.Service.Creator
import pcap.spi.option.DefaultOfflineOptions

internal interface IgmpPacketTest {

    /**
     * Parsing IGMP message on the specified file path.
     * IGMP message must be formatted as a hey dump exported using Wireshark application - it must contain
     * the whole encapsulated frame including Ethernet and IPv4 headers.
     *
     * @param packetFilePath path to the file containing hey dump
     * @param igmpType [Class] of expected [Igmp] message
     * @param T expected subtype of [Igmp] message
     * @return parsed [Igmp] message (without encapsulating Ethernet and IPv4 headers)
     */
    fun <T : Igmp<T>> parseIgmpMessage(packetFilePath: String, igmpType: Class<T>): T {
        val packetBuffer = loadFileToPacketBuffer(packetFilePath)
        val ethernetFrame = packetBuffer.cast(Ethernet::class.java)
        val ipv4Packet = packetBuffer.readerIndex(ethernetFrame.size().toLong()).cast(Ip4::class.java)
        return packetBuffer.readerIndex((ethernetFrame.size() + ipv4Packet.size()).toLong()).cast(igmpType)
    }

    /**
     * Create empty IGMP message of specified type.
     *
     * @param igmpType [Class] of expected [Igmp] message
     * @param capacity maximum size of the IGMP message
     * @param T subtype of [Igmp] message
     * @return created empty [Igmp] message
     */
    fun <T : Igmp<T>> createIgmpMessage(igmpType: Class<T>, capacity: Long): T {
        val frame = PCAP_SERVICE.allocate(PacketBuffer::class.java).capacity(capacity)
        val zeroArray = ByteArray(capacity.toInt())
        frame.setBytes(0, zeroArray)
        frame.writerIndex(frame.capacity())
        return frame.cast(igmpType)
    }

    companion object {
        private val HEX_OCTETS_PATTERN = Pattern.compile(" +(?<hex>[0-9a-f]{2})")
        private val PCAP_SERVICE = createPcapService()

        private fun loadFileToPacketBuffer(fileName: String): PacketBuffer {
            val fileContent = File(fileName.toFilePath()).readText(Charsets.UTF_8)
            val bytes = parseByteArray(fileContent)
            val frame = PCAP_SERVICE.allocate(PacketBuffer::class.java).capacity(bytes.size.toLong())
            frame.writeBytes(bytes)
            return frame
        }

        private fun parseByteArray(text: String): ByteArray {
            val matcher = HEX_OCTETS_PATTERN.matcher(text)
            val hexString = StringBuilder()
            while (matcher.find()) {
                val octets = matcher.group("hex")
                hexString.append(octets)
            }
            return hexString.toString().decodeHex()
        }

        private fun String.decodeHex(): ByteArray {
            check(length % 2 == 0) { "Array must have an even length" }
            return chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }

        private fun createPcapService(): Pcap {
            val service = Creator.create("PcapService")
            return service.offline("/empty.pcap".toFilePath(),
                DefaultOfflineOptions()
            )
        }

        private fun String.toFilePath() = IgmpV1PacketTest::class.java.getResource(this)!!.file
    }
}