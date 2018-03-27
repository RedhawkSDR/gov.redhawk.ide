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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import mil.jpeojtrs.sca.spd.Implementation;

/**
 * Extension of {@link ICodeGeneratorPageRegistry2} that allows for passing the {@link Implementation}
 * to the found composite, when can then filter available code-generators based on language and existing codegenId 
 * @since 10.1
 */
public interface ICodeGeneratorLanguagePageRegistry extends ICodeGeneratorPageRegistry2 {
	/**
	 * Finds a composite for the specified codegenId
	 * 
	 * @param language - Passes the programming language so that the Composite can filter out unrelated code-generators
	 * @return a composite by looking it by it's codegenId
	 */
	ICodegenComposite[] findCompositeByGeneratorId(Implementation impl, String codegenId, Composite parent, int style, FormToolkit toolkit);

}
