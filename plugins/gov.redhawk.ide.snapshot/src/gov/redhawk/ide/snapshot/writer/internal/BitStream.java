/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.snapshot.writer.internal;

import BULKIO.BitSequence;

/**
 * Handles packing a series of bit buffers so that they form a contiguous stream of bits.
 */
public class BitStream {

	/**
	 * Bits left over from the previous bit buffer. The first bit is the MSB, and unused bits are always 0's.
	 * @see #leftoverBitLen
	 */
	private byte leftoverBits = 0;

	/**
	 * The number of bits left over from the previous bit buffer.
	 */
	private int leftoverBitLen = 0;

	/**
	 * Appends the passed-in bits to the end of the buffer, and then returns bits starting at the beginning of the
	 * buffer. The method returns as many bits as possible, but always returns a multiple of 8 so that the bytes
	 * holding those bits are fully packed.
	 * <p/>
	 * When there is no more data to append to the stream, call {@link #getFinalBitBufferByte()} to see if there are
	 * any bits left in the stream.
	 * @param data The data to append to the stream
	 * @return A bit buffer of the stream beginning whose length is a multiple of 8
	 * @see #getFinalBitBufferByte()
	 */
	public BitSequence handleBitBuffer(BitSequence data) {
		// If there were leftover bits from the last push, place them at the beginning of the data we just received
		final byte[] newBuffer;
		final int newBufferBitLen;
		if (leftoverBitLen == 0) {
			newBufferBitLen = data.bits;
			newBuffer = data.data;
		} else {
			// Create a new buffer which can hold the left over bits plus the new ones
			newBufferBitLen = data.bits + leftoverBitLen;
			newBuffer = new byte[newBufferBitLen / 8 + 1];
			newBuffer[0] = leftoverBits;

			// upperBitsMask is used to take bits from the upper portion (MSB -> x) of a byte in data. These get shifted
			// right (down) and put in newBuffer.
			int upperBitsMask = (1 << (8 - leftoverBitLen)) - 1;

			// lowerBitsShift is used to take bits from the lower portion (x -> LSB) of a byte in data, shift them left
			// (up) and put them in newBuffer.
			int lowerBitsShift = 8 - leftoverBitLen;

			// Shift bits
			for (int i = 0; i < newBuffer.length - 1; i++) {
				newBuffer[i] |= data.data[i] >> leftoverBitLen & upperBitsMask;
				newBuffer[i + 1] = (byte) (data.data[i] << lowerBitsShift & 0xFF);
			}
			if (newBufferBitLen % 8 != 0) {
				newBuffer[newBuffer.length - 1] |= data.data[data.data.length - 1] >> leftoverBitLen & upperBitsMask;
			}
		}

		// If the number of bits wasn't divisible by 8, there are some leftover bits we'll need to hold on to
		leftoverBitLen = newBufferBitLen % 8;
		if (leftoverBitLen == 0) {
			leftoverBits = 0;
		} else {
			int mask = ~((0x100 >> leftoverBitLen) - 1);
			leftoverBits = (byte) (newBuffer[newBufferBitLen / 8] & mask);
		}

		// Return what should actually be written
		return new BitSequence(newBuffer, newBufferBitLen - (newBufferBitLen % 8));
	}

	/**
	 * Removes and returns any bits remaining in the stream (0 - 7 bits).
	 * @return A {@link BitSequence} with the final bits
	 * @see #handleBitBuffer(BitSequence)
	 */
	public BitSequence getFinalBits() {
		BitSequence retVal = new BitSequence(new byte[] { leftoverBits }, leftoverBitLen);
		leftoverBits = 0;
		leftoverBitLen = 0;
		return retVal;
	}
}
