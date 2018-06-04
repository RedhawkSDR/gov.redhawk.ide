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

import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.BitSequence;
import BULKIO.PortStatistics;
import BULKIO.PortUsageType;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataBit;
import BULKIO.dataBitHelper;
import BULKIO.dataBitOperations;
import BULKIO.dataCharOperations;
import BULKIO.dataDouble;
import BULKIO.dataDoubleHelper;
import BULKIO.dataDoubleOperations;
import BULKIO.dataFloat;
import BULKIO.dataFloatHelper;
import BULKIO.dataFloatOperations;
import BULKIO.dataLong;
import BULKIO.dataLongHelper;
import BULKIO.dataLongLong;
import BULKIO.dataLongLongHelper;
import BULKIO.dataLongLongOperations;
import BULKIO.dataLongOperations;
import BULKIO.dataOctet;
import BULKIO.dataOctetHelper;
import BULKIO.dataOctetOperations;
import BULKIO.dataShort;
import BULKIO.dataShortHelper;
import BULKIO.dataShortOperations;
import BULKIO.dataUlong;
import BULKIO.dataUlongHelper;
import BULKIO.dataUlongLong;
import BULKIO.dataUlongLongHelper;
import BULKIO.dataUlongLongOperations;
import BULKIO.dataUlongOperations;
import BULKIO.dataUshort;
import BULKIO.dataUshortHelper;
import BULKIO.dataUshortOperations;
import BULKIO.updateSRI;
import BULKIO.updateSRIHelper;
import CF.Port;
import CF.PortHelper;
import CF.PortOperations;
import CF.PortPOA;
import CF.PortPOATie;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.sca.util.OrbSession;

/**
 * Simulates a source port. Calling a <code>pushPacket</code> method makes this port call that method on the connected
 * object.
 */
public class SourcePort implements PortOperations, dataBitOperations, dataCharOperations, dataDoubleOperations, dataFloatOperations, dataLongLongOperations,
		dataLongOperations, dataOctetOperations, dataShortOperations, dataUlongLongOperations, dataUlongOperations, dataUshortOperations {

	private static OrbSession session;

	private PortPOA servant;
	private Port reference;

	private BulkIOType bulkioType;
	private boolean connected = false;
	private org.omg.CORBA.Object target = null;
	private String connectionId = null;

	public void init() throws ServantNotActive, WrongPolicy, CoreException {
		session = OrbSession.createSession(SourcePort.class.getSimpleName());
		servant = new PortPOATie(this);
		reference = PortHelper.unchecked_narrow(session.getPOA().servant_to_reference(servant));
	}

	public void destroy() {
		if (session != null) {
			if (reference != null) {
				try {
					byte[] oid = session.getPOA().servant_to_id(servant);
					session.getPOA().deactivate_object(oid);
				} catch (ObjectNotActive | WrongPolicy | CoreException | ServantNotActive e) {
				}
				servant = null;
				reference = null;
			}
			session.dispose();
			session = null;
		}
	}

	public void setBulkIOType(BulkIOType bulkioType) {
		this.bulkioType = bulkioType;
	}

	public org.omg.CORBA.Object getPortObj() {
		return reference;
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (!(target instanceof updateSRI)) {
			target = updateSRIHelper.narrow(target);
		}
		((updateSRI) target).pushSRI(sri);
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (bulkioType != BulkIOType.BIT) {
			Assert.fail();
		}

		if (!(target instanceof dataBit)) {
			target = dataBitHelper.narrow(target);
		}
		((dataBit) target).pushPacket(data, time, eos, streamID);
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		Assert.fail("Not implemented");
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (bulkioType != BulkIOType.DOUBLE) {
			Assert.fail();
		}

		if (!(target instanceof dataDouble)) {
			target = dataDoubleHelper.narrow(target);
		}
		((dataDouble) target).pushPacket(data, time, eos, streamID);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (bulkioType != BulkIOType.FLOAT) {
			Assert.fail();
		}

		if (!(target instanceof dataFloat)) {
			target = dataFloatHelper.narrow(target);
		}
		((dataFloat) target).pushPacket(data, time, eos, streamID);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		switch (bulkioType) {
		case LONG_LONG:
			if (!(target instanceof dataLongLong)) {
				target = dataLongLongHelper.narrow(target);
			}
			((dataLongLong) target).pushPacket(data, time, eos, streamID);
			break;
		case ULONG_LONG:
			if (!(target instanceof dataUlongLong)) {
				target = dataUlongLongHelper.narrow(target);
			}
			((dataUlongLong) target).pushPacket(data, time, eos, streamID);
			break;
		default:
			Assert.fail();
		}
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		switch (bulkioType) {
		case LONG:
			if (!(target instanceof dataLong)) {
				target = dataLongHelper.narrow(target);
			}
			((dataLong) target).pushPacket(data, time, eos, streamID);
			break;
		case ULONG:
			if (!(target instanceof dataUlong)) {
				target = dataUlongHelper.narrow(target);
			}
			((dataUlong) target).pushPacket(data, time, eos, streamID);
			break;
		default:
			Assert.fail();
		}
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (bulkioType != BulkIOType.OCTET) {
			Assert.fail();
		}

		if (!(target instanceof dataOctet)) {
			target = dataOctetHelper.narrow(target);
		}
		((dataOctet) target).pushPacket(data, time, eos, streamID);
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		switch (bulkioType) {
		case SHORT:
			if (!(target instanceof dataShort)) {
				target = dataShortHelper.narrow(target);
			}
			((dataShort) target).pushPacket(data, time, eos, streamID);
			break;
		case USHORT:
			if (!(target instanceof dataUshort)) {
				target = dataUshortHelper.narrow(target);
			}
			((dataUshort) target).pushPacket(data, time, eos, streamID);
			break;
		default:
			Assert.fail();
		}
	}

	@Override
	public StreamSRI[] activeSRIs() {
		return null;
	}

	@Override
	public PortUsageType state() {
		return null;
	}

	@Override
	public PortStatistics statistics() {
		return null;
	}

	@Override
	public void connectPort(Object connection, String connectionId) throws InvalidPort, OccupiedPort {
		if (this.connected) {
			throw new OccupiedPort();
		}
		this.connected = true;
		this.target = connection;
		this.connectionId = connectionId;
	}

	@Override
	public void disconnectPort(String connectionId) throws InvalidPort {
		if (!this.connected) {
			throw new InvalidPort((short) 0, "Not connected");
		}
		if (!this.connectionId.equals(connectionId)) {
			throw new InvalidPort((short) 0, "Invalid  connection ID");
		}
		this.connected = false;
		this.target = null;
		this.connectionId = null;
	}

}
