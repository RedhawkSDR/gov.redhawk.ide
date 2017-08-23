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
package gov.redhawk.ide.dcd.internal.ui.editor.composite;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ServiceDetailsComposite extends DcdComponentComposite {

	public ServiceDetailsComposite(Composite parent, int style, FormToolkit toolkit, IEditingDomainProvider editingDomainProvider, boolean showProps) {
		super(parent, style, toolkit, editingDomainProvider, showProps);
	}

	@Override
	protected void createNameEntry() {
		super.createNameEntry();
		getNameEntry().getText().setToolTipText("Human readable name for the service instantiation");
	}

	@Override
	protected void createCompositeSections(boolean showProps) {
		createNameEntry();

		if (showProps) {
			createPropertiesArea();
		}
	}

}
