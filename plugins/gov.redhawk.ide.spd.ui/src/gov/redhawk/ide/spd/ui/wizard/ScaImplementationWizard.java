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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import mil.jpeojtrs.sca.spd.Implementation;

public interface ScaImplementationWizard {

	/**
	 * This method is called when a generator changes to update the status of
	 * the next/finish buttons.
	 * 
	 * @param codeGeneratorDescriptor the descriptor for the new generator
	 * @since 4.0
	 */
	public void generatorChanged(Implementation impl, ICodeGeneratorDescriptor codeGeneratorDescriptor);

	/**
	 * This returns true if there are more implementations left to display.
	 * 
	 * @param curImpl the implementation of the currently displayed page
	 * @return true if there are more implementations left to display
	 * @since 4.0
	 */
	public boolean hasMoreImplementations(Implementation curImpl);
}
