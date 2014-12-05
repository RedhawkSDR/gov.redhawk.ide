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

import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.DevicesDetailsPage;
import gov.redhawk.ui.editor.SCAMasterDetailsBlock;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @since 1.1
 * 
 */
public class DevicesBlock extends SCAMasterDetailsBlock {

	private static final int PAGE_LIMIT = 10;
	private DevicesSection fSection;

	/**
	 * Instantiates a new scrolled properties block.
	 * 
	 * @param page the page
	 */
	public DevicesBlock(final DevicesPage page) {
		super(page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScaSection createMasterSection(final IManagedForm managedForm, final Composite parent) {
		this.fSection = new DevicesSection(getPage(), parent);
		return this.fSection;
	}

	/**
	 * @return the fSection
	 */
	public DevicesSection getSection() {
		return this.fSection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPages(final DetailsPart detailsPart) {
		detailsPart.setPageLimit(DevicesBlock.PAGE_LIMIT);

		// TODO Fix this!
		detailsPart.registerPage(DcdComponentPlacement.class, new DevicesDetailsPage(this.fSection));
		//		detailsPart.registerPage(UsesDevice.class, new UsesDeviceDetailsPage(this.fSection));
		//		detailsPart.registerPage(PropertyRef.class, new PropertyRefDetailsPage(this.fSection));
		//		detailsPart.registerPage(Dependency.class, new DependencyDetailsPage(this.fSection));
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			@Override
			public Object getPageKey(final Object object) {
				if (object instanceof DcdComponentPlacement) {
					return DcdComponentPlacement.class;
				} else if (object instanceof UsesDevice) {
					return UsesDevice.class;
				} else if (object instanceof PropertyRef) {
					return PropertyRef.class;
				} else if (object instanceof Dependency) {
					return Dependency.class;
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
