/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.debug.internal;


import java.io.IOException;

import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy2;

/**
 * 
 */
public class ApplicationStreams implements IStreamsProxy2 {
	private final ApplicationOutputStream errStream = new ApplicationOutputStream();
	private final ApplicationOutputStream outStream = new ApplicationOutputStream();

	/**
	 * {@inheritDoc}
	 */
	public IStreamMonitor getErrorStreamMonitor() {
		return this.errStream;
	}

	public ApplicationOutputStream getErrStream() {
		return this.errStream;
	}

	/**
	 * {@inheritDoc}
	 */
	public IStreamMonitor getOutputStreamMonitor() {
		return this.outStream;
	}

	public ApplicationOutputStream getOutStream() {
		return this.outStream;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write(final String input) throws IOException {
		// PASS
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeInputStream() throws IOException {
		// PASS
	}

}
