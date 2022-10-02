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
 * This class is used for representation of additional information pertaining to some Group Record.
 *
 * @param bytes [List] of [UByte] containing 4 elements
 * @constructor creation of [IgmpAuxiliaryWord]
 */
class IgmpAuxiliaryWord private constructor(val bytes: List<UByte>) {

    companion object {
        /**
         * Creation of word using list of 4 bytes.
         *
         * @param bytes [List] of [UByte]
         * @return created [IgmpAuxiliaryWord]
         * @throws IllegalArgumentException input list does not have exactly 4 elements
         */
        fun createWord(bytes: List<UByte>): IgmpAuxiliaryWord {
            require(bytes.size == 4) { "Invalid auxiliary word - it must contain exactly 4 bytes" }
            return IgmpAuxiliaryWord(bytes.toList())
        }

        /**
         * Creation of word by parsing of input byte array as it is written in the IGMP packet.
         *
         * @param bytes [ByteArray]
         * @return parsed [IgmpAuxiliaryWord]
         * @throws IllegalArgumentException input array does not have exactly 4 elements
         */
        internal fun createWords(bytes: ByteArray): List<IgmpAuxiliaryWord> {
            require(bytes.size % 4 == 0) {
                "Failed to create list of auxiliary words from provided array of bytes - " +
                        "number of bytes is not a multiplication of 4"
            }

            val words = ArrayList<IgmpAuxiliaryWord>()
            val buffer = ArrayList<Byte>()
            for (byte in bytes) {
                buffer.add(byte)
                if (buffer.size == 4) {
                    words.add(IgmpAuxiliaryWord(buffer
                        .map { it.toUByte() }
                        .toList()))
                    buffer.clear()
                }
            }
            return words
        }
    }

    /**
     * Conversion of this word into signed list of bytes.
     *
     * @return [List] of [Byte]s
     */
    internal fun toByteArray() = bytes
        .map { it.toByte() }
        .toByteArray()

    override fun toString() = "IgmpAuxiliaryWord(bytes=$bytes)"
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as IgmpAuxiliaryWord
        if (bytes != other.bytes) {
            return false
        }
        return true
    }

    override fun hashCode() = bytes.hashCode()
}