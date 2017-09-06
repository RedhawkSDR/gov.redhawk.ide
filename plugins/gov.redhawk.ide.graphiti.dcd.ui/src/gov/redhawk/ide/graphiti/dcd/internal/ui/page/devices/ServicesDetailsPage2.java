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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.devices;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * Like {@link ServicesDetailsPage}, but with a properties section
 */
public class ServicesDetailsPage2 extends ServicesDetailsPage {

	public ServicesDetailsPage2(DevicesSection fSection) {
		super(fSection);
	}

	@Override
	protected ServiceDetailsComposite getComposite(Section section, FormToolkit toolkit) {
		return new ServiceDetailsComposite(section, SWT.NONE, toolkit, this.getEditor(), true);		
	}
}
