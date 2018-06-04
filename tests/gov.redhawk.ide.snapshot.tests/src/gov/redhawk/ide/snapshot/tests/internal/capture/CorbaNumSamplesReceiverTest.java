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
package gov.redhawk.ide.snapshot.tests.internal.capture;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.UNITS_TIME;
import BULKIO.dataBitHelper;
import CF.DataType;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.StreamSRIUtil;
import gov.redhawk.ide.snapshot.capture.DataReceiverFactory;
import gov.redhawk.ide.snapshot.capture.IScaPortReceiver;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaUsesPort;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.Uses;

public class CorbaNumSamplesReceiverTest {

	private SourcePort sourcePort;
	private ReceiverBuffer receiverBuffer;
	private IScaPortReceiver receiver;
	private IProgressMonitor monitor;

	@Before
	public void before() throws ServantNotActive, WrongPolicy, CoreException {
		this.sourcePort = new SourcePort();
		this.sourcePort.init();
	}

	@After
	public void after() throws InterruptedException {
		// Cancel the monitor to ensure the receiver stops processing
		if (monitor != null) {
			monitor.setCanceled(true);
			monitor = null;
		}
		long endTime = System.currentTimeMillis() + 5000;
		while (System.currentTimeMillis() < endTime && sourcePort.isConnected()) {
			Thread.sleep(500);
		}

		receiver = null;
		receiverBuffer = null;

		// Destroy the source port
		if (sourcePort != null) {
			sourcePort.destroy();
			sourcePort = null;
		}
	}

	private void setup(String repID, String connectionID, int samples) throws InterruptedException {
		this.sourcePort.setBulkIOType(BulkIOType.getType(repID));

		Uses profile = ScdFactory.eINSTANCE.createUses();
		profile.setRepID(repID);

		ScaUsesPort port = ScaFactory.eINSTANCE.createScaUsesPort();
		port.setCorbaObj(sourcePort.getPortObj());
		port.setProfileObj(profile);

		receiverBuffer = new ReceiverBuffer();

		receiver = DataReceiverFactory.createSamplesReceiver(samples);
		receiver.setPort(port);
		receiver.setConnectionID(connectionID);
		receiver.setDataWriter(receiverBuffer);

		// Start the receiver
		monitor = new NullProgressMonitor();
		new Thread(() -> {
			receiver.run(monitor);
		}).start();

		// Wait for connection
		long endTime = System.currentTimeMillis() + 5000;
		while (!sourcePort.isConnected()) {
			Thread.sleep(500);
			if (System.currentTimeMillis() > endTime) {
				Assert.fail("Source port wasn't connected to the IDE's receiver");
			}
		}
	}

	@Test
	public void captureBits() throws InterruptedException {
		final String CONNETION_ID = "abc";
		final String STREAM_ID = CONNETION_ID;
		setup(dataBitHelper.id(), CONNETION_ID, 10);

		// Push SRI
		StreamSRI sri = new StreamSRI(0, 0, 0.1, UNITS_TIME.value, 0, 0, 0, (short) 0, (short) 0, STREAM_ID, false, new DataType[0]);
		sourcePort.pushSRI(sri);

		// Bug IDE-1528 (SRI is asynchronous from the push it is received in). If we don't wait, we can miss data.
		waitFor("Initial SRI was not received", 5000, () -> {
			// The CorbaDataReceiver waits until it has gotten SRI, then opens the receiver
			return receiverBuffer.isOpen();
		});

		// Push data
		BitSequence data1 = new BitSequence(new byte[] { (byte) 0b10101010 }, 8);
		sourcePort.pushPacket(data1, new PrecisionUTCTime((short) 0, (short) 0, 0, 0, 0), false, STREAM_ID);
		BitSequence data2 = new BitSequence(new byte[] { (byte) 0b10101010 }, 8);
		sourcePort.pushPacket(data2, new PrecisionUTCTime((short) 0, (short) 0, 0, 0, 0), false, STREAM_ID);

		// Wait for the receiver to finish getting the data and writing it out
		waitFor("Receiver buffer wasn't closed", 5000, () -> {
			return !receiverBuffer.isOpen();
		});

		// We should have SRI + 2 pushes in the buffer = 3 total things
		List<Object> buffer = receiverBuffer.getBuffer();
		Assert.assertEquals(3, buffer.size());

		Assert.assertTrue(buffer.get(0) instanceof StreamSRI);
		Assert.assertTrue(StreamSRIUtil.equals(sri, (StreamSRI) buffer.get(0)));

		Assert.assertTrue(buffer.get(1) instanceof ReceiverBuffer.Packet);
		ReceiverBuffer.Packet packet = (ReceiverBuffer.Packet) buffer.get(1);
		BitSequence writtenBits = (BitSequence) packet.data;
		Assert.assertEquals(data1.bits, writtenBits.bits);
		Assert.assertArrayEquals(data1.data, writtenBits.data);

		// Last push will be truncated
		BitSequence data2Trunc = new BitSequence(new byte[] { (byte) 0b10000000 }, 2);
		byte data2Mask = (byte) 0b11000000;
		Assert.assertTrue(buffer.get(2) instanceof ReceiverBuffer.Packet);
		packet = (ReceiverBuffer.Packet) buffer.get(2);
		writtenBits = (BitSequence) packet.data;
		Assert.assertEquals(data2Trunc.bits, writtenBits.bits);
		Assert.assertEquals(data2Trunc.data[0] & data2Mask, writtenBits.data[0] & data2Mask);
	}

	private void waitFor(String failureMessage, int timeoutMs, Callable<Boolean> condition) throws InterruptedException {
		long endTime = System.currentTimeMillis() + timeoutMs;
		try {
			while (!condition.call()) {
				Thread.sleep(250);
				if (System.currentTimeMillis() > endTime) {
					Assert.fail(failureMessage);
				}
			}
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Callable
			Assert.fail(e.getMessage());
		}
	}

}
