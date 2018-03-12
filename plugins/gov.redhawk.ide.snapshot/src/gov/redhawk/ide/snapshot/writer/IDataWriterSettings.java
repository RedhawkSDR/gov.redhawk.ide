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

import gov.redhawk.bulkio.util.BulkIOType;

/**
 * @since 1.0
 */
public interface IDataWriterSettings {

	/**
	 * @return The {@link org.eclipse.core.resources.IFile} or {@link java.io.File} to save to
	 */
	Object getDestination();

	/**
	 * @param destination The {@link org.eclipse.core.resources.IFile} or {@link java.io.File} to save to
	 */
	void setDestination(Object destination);

	/**
	 * @return The type of BULKIO port to snapshot
	 */
	BulkIOType getType();

	/**
	 * @param type The type of BULKIO port to snapshot
	 */
	void setType(BulkIOType type);

	/**
	 * @return Whether or not unsigned data should be upcast to next larger signed type that can represent it
	 */
	boolean isUpcastUnsigned();

	/**
	 * @param upcastUnsigned Whether or not unsigned data should be upcast to next larger signed type that can represent
	 * it
	 */
	void setUpcastUnsigned(boolean upcastUnsigned);
}
