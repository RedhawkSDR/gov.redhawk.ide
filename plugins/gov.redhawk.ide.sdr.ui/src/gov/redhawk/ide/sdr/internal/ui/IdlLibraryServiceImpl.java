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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ui.editor.IIdlLibraryService;

public class IdlLibraryServiceImpl implements IIdlLibraryService {

	public IdlLibraryServiceImpl() {
	}

	@Override
	public IdlLibrary getLibrary() {
		return TargetSdrRoot.getSdrRoot().getIdlLibrary();
	}

}
