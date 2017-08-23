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

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;

import CF.PortSupplierHelper;
import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.DevicesDetailsPage;
import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.ServicesDetailsPage;
import gov.redhawk.ide.dcd.internal.ui.editor.detailspart.ServicesDetailsPage2;
import gov.redhawk.ui.editor.SCAMasterDetailsBlock;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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
		detailsPart.registerPage(DevicesDetailsPage.class, new DevicesDetailsPage(this.fSection));
		detailsPart.registerPage(ServicesDetailsPage.class, new ServicesDetailsPage(this.fSection));
		detailsPart.registerPage(ServicesDetailsPage2.class, new ServicesDetailsPage2(this.fSection));
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			@Override
			public Object getPageKey(final Object object) {
				if (object instanceof DcdComponentInstantiation) {
					DcdComponentInstantiation compInst = (DcdComponentInstantiation) object;
					final EStructuralFeature[] COMP_TO_SPD = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
						PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
						PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
					SoftPkg spd = ScaEcoreUtils.getFeature(compInst, COMP_TO_SPD);
					if (spd == null) {
						return Object.class;
					}

					SoftwareComponent scd = spd.getDescriptor().getComponent();
					switch (SoftwareComponent.Util.getWellKnownComponentType(scd)) {
					case DEVICE:
						return DevicesDetailsPage.class;
					case SERVICE:
						Interface tmpInterface = ScdFactory.eINSTANCE.createInterface();
						tmpInterface.setRepid(PortSupplierHelper.id());
						for (Interface serviceInterface : scd.getInterfaces().getInterface()) {
							if (serviceInterface.isInstance(tmpInterface)) {
								return ServicesDetailsPage2.class;
							}
						}
						return ServicesDetailsPage.class;

					default:
						return Object.class;
					}
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
