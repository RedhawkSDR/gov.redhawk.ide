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
 * Extension of {@link ICodegenDisplayFactory} that allows for passing the {@link Implementation} and codegenId
 * to the created composite, when can then filter available code-generators based on language and existing codegenId 
 * @since 10.1
 */
public interface ICodegenLanguageDisplayFactory extends ICodegenDisplayFactory {

	/**
	 * create the specified composite.
	 * 
	 * @return a composite
	 */
	ICodegenComposite createComposite(Implementation impl, String codegenId, Composite parent, int style, FormToolkit toolkit);
}
