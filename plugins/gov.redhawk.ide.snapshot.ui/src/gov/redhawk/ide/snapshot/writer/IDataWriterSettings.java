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
public interface IDataWriterSettings {
	/**
	 * @return  The file to start saving with
	 */
	Object getDestination();
	
	void setDestination(Object destination);
	/**
	 * 
	 * @return  The BulkIOType of the port
	 */
	BulkIOType getType(); 
	
	void setType(BulkIOType type);
	
	/**
	 * @return whether or not unsigned data should be upcast to next larger signed type that can represent it 
	 */
	boolean isUpcastUnsigned();
	
	void setUpcastUnsigned(boolean upcastUnsigned);
}
