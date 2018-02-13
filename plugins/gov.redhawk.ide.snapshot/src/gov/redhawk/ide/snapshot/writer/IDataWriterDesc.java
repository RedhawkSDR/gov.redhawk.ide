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
package gov.redhawk.ide.snapshot.writer;

import org.eclipse.core.runtime.CoreException;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 1.0
 */
public interface IDataWriterDesc extends Comparable<IDataWriterDesc> {
	IDataWriter createWriter() throws CoreException;
	IDataWriterSettings createWriterSettings() throws CoreException;
	String getName();
	String getID();
	String getDescription();
	boolean hasControlFactory();
}
