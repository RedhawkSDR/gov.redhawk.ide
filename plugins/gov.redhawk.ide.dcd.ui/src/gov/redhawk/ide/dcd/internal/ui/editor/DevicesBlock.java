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
package gov.redhawk.ide.dcd.internal.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;

import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.DevicesDetailsPage;
import gov.redhawk.ui.editor.SCAMasterDetailsBlock;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

/**
 * @since 1.2
 */
public class DevicesBlock extends SCAMasterDetailsBlock {

	private DevicesSection fSection;

	/**
	 * Instantiates a new scrolled properties block.
	 * 
	 * @param page the page
	 */
	public DevicesBlock(final DevicesPage page) {
		super(page);
	}

	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	@Override
	protected ScaSection createMasterSection(final IManagedForm managedForm, final Composite parent) {
		this.fSection = new DevicesSection(getPage(), parent);
		return this.fSection;
	}

	public DevicesSection getSection() {
		return this.fSection;
	}

	@Override
	protected void registerPages(final DetailsPart detailsPart) {
		detailsPart.registerPage(DcdComponentInstantiation.class, new DevicesDetailsPage(this.fSection));
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			@Override
			public Object getPageKey(final Object object) {
				if (object instanceof DcdComponentInstantiation) {
					return DcdComponentInstantiation.class;
				}
				return null;
			}

			@Override
			public IDetailsPage getPage(final Object key) {
				return null;
			}
		});
	}

}
