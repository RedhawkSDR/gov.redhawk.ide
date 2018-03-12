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

/**
 * @since 1.0
 */
public interface IDataWriterRegistry {
	/**
	 * @since 1.0
	 */
	public IDataWriterDesc getReceiverDesc(String id);
	public IDataWriterDesc [] getRecieverDescs();
}
