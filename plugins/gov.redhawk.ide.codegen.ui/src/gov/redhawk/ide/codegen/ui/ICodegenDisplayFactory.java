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

/**
 * @since 2.0
 */
public interface ICodegenDisplayFactory {
	/**
	 * Create the specified wizard page.
	 * 
	 * @return a wizard page
	 */
	ICodegenWizardPage createPage();

	/**
	 * create the specified composite.
	 * 
	 * @return a composite
	 */
	ICodegenComposite createComposite(Composite parent, int style, FormToolkit toolkit);
}
