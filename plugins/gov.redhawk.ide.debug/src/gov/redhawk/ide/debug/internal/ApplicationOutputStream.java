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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * 
 */
public class ApplicationOutputStream implements IStreamMonitor {

	private final Set<IStreamListener> listenerList = new HashSet<IStreamListener>();
	private final StringBuilder buffer = new StringBuilder();

	public void print(final String str) {
		this.buffer.append(str);
		for (final IStreamListener listener : this.listenerList) {
			listener.streamAppended(str, this);
		}
	}

	public void println(final String str) {
		print(str + "\n");
	}

	public void printStackTrace(final String msg, final Throwable e) { // SUPPRESS CHECKSTYLE OUTPUT
		final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		final PrintStream stream = new PrintStream(byteBuffer);
		if (e != null) {
			e.printStackTrace(stream); // SUPPRESS CHECKSTYLE OUTPUT
		}
		stream.flush();
		println(msg);
		println(new String(byteBuffer.toByteArray()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListener(final IStreamListener listener) {
		this.listenerList.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getContents() {
		return this.buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(final IStreamListener listener) {
		this.listenerList.remove(listener);
	}

}
