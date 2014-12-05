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

import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.ImplementationDetailsPage;
import gov.redhawk.ui.editor.SCAMasterDetailsBlock;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @since 1.1
 * 
 */
public class ImplementationsBlock extends SCAMasterDetailsBlock {

	private static final int PAGE_LIMIT = 10;
	private ImplementationsSection fSection;

	/**
	 * Instantiates a new scrolled properties block.
	 * 
	 * @param page the page
	 */
	public ImplementationsBlock(final ImplementationPage page) {
		super(page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImplementationPage getPage() {
		return (ImplementationPage) super.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScaSection createMasterSection(final IManagedForm managedForm, final Composite parent) {
		this.fSection = new ImplementationsSection(getPage(), parent);
		return this.fSection;
	}

	/**
	 * @return the fSection
	 */
	public ImplementationsSection getSection() {
		return this.fSection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPages(final DetailsPart detailsPart) {
		detailsPart.setPageLimit(ImplementationsBlock.PAGE_LIMIT);
		detailsPart.registerPage(Implementation.class, new ImplementationDetailsPage(this.fSection));
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			@Override
			public Object getPageKey(final Object object) {
				if (object instanceof Implementation) {
					return Implementation.class;
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
