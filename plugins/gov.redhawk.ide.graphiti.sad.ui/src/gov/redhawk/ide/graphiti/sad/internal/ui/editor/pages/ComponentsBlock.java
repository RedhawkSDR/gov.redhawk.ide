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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;

import gov.redhawk.ui.editor.SCAMasterDetailsBlock;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class ComponentsBlock extends SCAMasterDetailsBlock {

	private ComponentsSection fSection;

	public ComponentsBlock(final SadComponentsPage page) {
		super(page);
	}

	@Override
	public SadComponentsPage getPage() {
		return (SadComponentsPage) super.getPage();
	}

	@Override
	protected ScaSection createMasterSection(IManagedForm managedForm, Composite parent) {
		this.fSection = new ComponentsSection(getPage(), parent);
		return this.fSection;
	}

	public ComponentsSection getSection() {
		return this.fSection;
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(SadComponentInstantiation.class, new ComponentsDetailsPage(this.fSection));
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			@Override
			public Object getPageKey(Object object) {
				if (object instanceof SadComponentInstantiation) {
					return SadComponentInstantiation.class;
				}

				return null;
			}

			@Override
			public IDetailsPage getPage(Object key) {
				return null;
			}
		});
	}

}
