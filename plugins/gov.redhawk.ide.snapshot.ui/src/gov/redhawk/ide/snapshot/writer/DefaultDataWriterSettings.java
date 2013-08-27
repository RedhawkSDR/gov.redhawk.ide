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
package gov.redhawk.ide.snapshot.writer;

import gov.redhawk.bulkio.util.BulkIOType;

/**
 * 
 */
public class DefaultDataWriterSettings implements IDataWriterSettings {
	private Object destination;
	private BulkIOType type;
	private boolean upcastUnsigned;

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.writer.IDataWriterSettings#getDestination()
	 */
	@Override
	public Object getDestination() {
		return destination;
	}

	@Override
	public void setDestination(Object destination) {
		this.destination = destination;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.writer.IDataWriterSettings#getType()
	 */
	@Override
	public BulkIOType getType() {
		return type;
	}

	@Override
	public void setType(BulkIOType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.writer.IDataWriterSettings#isUpcastUnsigned()
	 */
	@Override
	public boolean isUpcastUnsigned() {
		return this.upcastUnsigned;
	}

	@Override
	public void setUpcastUnsigned(boolean upcastUnsigned) {
		this.upcastUnsigned = upcastUnsigned;
	}

}
