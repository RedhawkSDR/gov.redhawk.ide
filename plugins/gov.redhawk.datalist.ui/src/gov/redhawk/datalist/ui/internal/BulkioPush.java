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
package gov.redhawk.datalist.ui.internal;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

/**
 * Encapsulates all data and metadata from a BULKIO pushPacket call.
 */
public class BulkioPush {

	private StreamSRI sri;
	private Object data;
	private int length;
	private PrecisionUTCTime time;
	private boolean eos;
	private String streamID;

	/* package */ BulkioPush(StreamSRI sri, Object data, int length, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		this.sri = sri;
		this.data = data;
		this.length = length;
		this.time = time;
		this.eos = eos;
		this.streamID = streamID;
	}

	public StreamSRI getSRI() {
		return sri;
	}

	public Object getData() {
		return data;
	}

	public int getLength() {
		return length;
	}

	public PrecisionUTCTime getTime() {
		return time;
	}

	public boolean getEOS() {
		return eos;
	}

	public String getStreamID() {
		return streamID;
	}
}
