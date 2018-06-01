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
package gov.redhawk.ide.snapshot.tests.writer.internal;

import org.junit.Assert;
import org.junit.Test;

import BULKIO.BitSequence;
import gov.redhawk.ide.snapshot.writer.internal.BitStream;

public class BitStreamTest {

	private static final BitSequence SINGLE_1_BIT = new BitSequence(new byte[] { (byte) 0b10000000 }, 1);
	private static final BitSequence SINGLE_0_BIT = new BitSequence(new byte[] { (byte) 0b00000000 }, 1);

	private static final BitSequence NINE_BITS = new BitSequence(new byte[] { (byte) 0b10010110, (byte) 0b00000000 }, 9);
	private static final BitSequence[] NINE_BITS_INTERMEDIATE_RESULTS = new BitSequence[] { //
		new BitSequence(new byte[] { (byte) 0b10010110 }, 8), //
		new BitSequence(new byte[] { (byte) 0b01001011 }, 8), //
		new BitSequence(new byte[] { (byte) 0b00100101 }, 8), //
		new BitSequence(new byte[] { (byte) 0b10010010 }, 8), //
		new BitSequence(new byte[] { (byte) 0b11001001 }, 8) };
	private static final BitSequence NINE_BITS_FINAL_RESULT = new BitSequence(new byte[] { (byte) 0b01100000 }, 5);

	private static final BitSequence FIFTEEN_BITS = new BitSequence(new byte[] { (byte) 0b10000100, (byte) 0b00100000 }, 15);
	private static final BitSequence[] FIFTEEN_BITS_INTERMEDIATE_RESULTS = new BitSequence[] { //
		new BitSequence(new byte[] { (byte) 0b10000100 }, 8), //
		new BitSequence(new byte[] { (byte) 0b00100001, (byte) 0b00001000 }, 16), //
		new BitSequence(new byte[] { (byte) 0b01000010, (byte) 0b00010000 }, 16) };
	private static final BitSequence FIFTEEN_BITS_FINAL_RESULT = new BitSequence(new byte[] { (byte) 0b10000000 }, 5);

	private static final BitSequence SIXTEEN_BITS = new BitSequence(new byte[] { (byte) 0b00100100, (byte) 0b10010011 }, 16);
	private static final BitSequence[] SIXTEEN_BITS_INTERMEDIATE_RESULTS = new BitSequence[] { SIXTEEN_BITS, SIXTEEN_BITS };
	private static final BitSequence SIXTEEN_BITS_FINAL_RESULT = new BitSequence(new byte[0], 0);

	/**
	 * Tests pushing 1 bit at a time
	 */
	@Test
	public void singleBits() {
		BitStream stream = new BitStream();
		for (int i = 0; i < 7; i++) {
			BitSequence seq;
			if (i % 2 == 0) {
				seq = stream.handleBitBuffer(SINGLE_1_BIT);
			} else {
				seq = stream.handleBitBuffer(SINGLE_0_BIT);
			}
			Assert.assertEquals(0, seq.bits);
		}
		BitSequence seq = stream.handleBitBuffer(SINGLE_0_BIT);
		Assert.assertEquals(8, seq.bits);
		Assert.assertEquals((byte) 0b10101010, seq.data[0]);
		Assert.assertEquals(0, stream.getFinalBits().bits);

		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				seq = stream.handleBitBuffer(SINGLE_1_BIT);
			} else {
				seq = stream.handleBitBuffer(SINGLE_0_BIT);
			}
			Assert.assertEquals(0, seq.bits);
		}
		seq = stream.getFinalBits();
		Assert.assertEquals(5, seq.bits);
		Assert.assertEquals((byte) 0b10101000, seq.data[0]);
	}

	/**
	 * Tests pushing 9 bits at a time
	 */
	@Test
	public void nineBits() {
		commonTest(NINE_BITS, NINE_BITS_INTERMEDIATE_RESULTS, NINE_BITS_FINAL_RESULT);
	}

	/**
	 * Tests pushing 15 bits at a time
	 */
	@Test
	public void fifteenBits() {
		commonTest(FIFTEEN_BITS, FIFTEEN_BITS_INTERMEDIATE_RESULTS, FIFTEEN_BITS_FINAL_RESULT);
	}

	/**
	 * Tests pushing 16 bits at a time
	 */
	@Test
	public void sixteenBits() {
		commonTest(SIXTEEN_BITS, SIXTEEN_BITS_INTERMEDIATE_RESULTS, SIXTEEN_BITS_FINAL_RESULT);
	}

	/**
	 * Pushes sample data multiple times, checking the intermedia result of each push, and then the final bits at the
	 * end
	 * @param pushData The data to push each time
	 * @param intermediateResults The results, in order, that should be returned from sucessive calls to
	 * {@link BitStream#handleBitBuffer(BitSequence)}
	 * @param finalResult The result from {@link BitStream#getFinalBits()} after the pushes
	 */
	private void commonTest(BitSequence pushData, BitSequence[] intermediateResults, BitSequence finalResult) {
		BitStream stream = new BitStream();
		for (int i = 0; i < intermediateResults.length; i++) {
			BitSequence seq = stream.handleBitBuffer(pushData);
			Assert.assertEquals("Failed on iteration " + i, intermediateResults[i].bits, seq.bits);
			for (int j = 0; j < intermediateResults[i].data.length; j++) {
				Assert.assertEquals("Failed on iteration " + i + ", byte index " + j, intermediateResults[i].data[j], seq.data[j]);
			}
		}
		BitSequence seq = stream.getFinalBits();
		Assert.assertEquals(finalResult.bits, seq.bits);
		if (finalResult.bits > 0) {
			Assert.assertEquals(finalResult.data[0], seq.data[0]);
		}
	}
}
