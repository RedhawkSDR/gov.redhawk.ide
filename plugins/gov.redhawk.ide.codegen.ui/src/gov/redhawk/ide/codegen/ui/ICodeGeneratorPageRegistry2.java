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

package gov.redhawk.ide.codegen.ui;

import java.util.List;

/**
 * @since 8.1
 */
public interface ICodeGeneratorPageRegistry2 extends ICodeGeneratorPageRegistry {
	
	/**
	 * Get the default wizard page.
	 */
	ICodegenWizardPage[] getDefaultPages();
	
	/**
	 * Get the list of factories registered to this codegenId
	 * @param codegen CodegenId
	 * @return a list of factories that have registered for this codegenId
	 */
	List<ICodegenDisplayFactory> findCodegenDisplayFactoriesByGeneratorId(final String codegen);

}
