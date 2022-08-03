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
package sk.jt.igmp.fabric

import pcap.spi.PacketBuffer
import pcap.spi.PacketHeader
import pcap.spi.Service
import pcap.spi.exception.ErrorException
import pcap.spi.exception.error.BreakException
import pcap.spi.option.DefaultLiveOptions
import sk.jt.igmp.fabric.netint.NetworkInterfaceLoaderImpl

fun main(args: Array<String>) {
    val service = Service.Creator.create("PcapService")
    val interfaceLoader = NetworkInterfaceLoaderImpl(service)

    val pcap = service.live(interfaceLoader.findFirstInterface(), DefaultLiveOptions())
    try {
        pcap.loop(
            -1,
            { args: String, header: PacketHeader, buffer: PacketBuffer ->
                println("Args     : $args")
                println("Header   : $header")
                println("Packet   : $buffer")
            },
            "Hello pcap!"
        )
    } catch (e: BreakException) {
        System.err.println(e.message)
    } catch (e: ErrorException) {
        System.err.println(e.message)
    }
    pcap.close()
}
