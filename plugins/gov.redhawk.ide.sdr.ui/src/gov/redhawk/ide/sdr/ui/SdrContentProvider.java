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
package gov.redhawk.ide.sdr.ui;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.provider.RepositoryItemProviderAdapterFactory;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.provider.SdrItemProviderAdapterFactory;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 3.1
 */
public class SdrContentProvider extends TransactionalAdapterFactoryContentProvider {

	private Object input;

	public SdrContentProvider() {
		super(TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(SdrUiPlugin.EDITING_DOMAIN_ID), SdrContentProvider.createAdapterFactory());
	}

	/**
	 * Creates the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	private static AdapterFactory createAdapterFactory() {
		// Create an adapter factory that yields item providers.
		//
		final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new RepositoryItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new SdrItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdlItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ExpressionsAdapterFactory());
		adapterFactory.addAdapterFactory(new TypesItemProviderAdapterFactory());

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		return adapterFactory;
	}

	@Override
	public Object getParent(final Object object) {
		if (this.input instanceof SdrRoot) {
			final SdrRoot root = (SdrRoot) this.input;
			if (object instanceof SoftPkg) {
				ComponentType type = ComponentType.OTHER;
				final SoftPkg spd = (SoftPkg) object;
				final Descriptor desc = spd.getDescriptor();
				if (desc != null) {
					final SoftwareComponent scd = desc.getComponent();
					type = SoftwareComponent.Util.getWellKnownComponentType(scd);
				}
				switch (type) {
				case DEVICE_MANAGER:
					return root.getNodesContainer();
				case DEVICE:
					return root.getDevicesContainer();
				case SERVICE:
					return root.getServicesContainer();
				case RESOURCE:
				default:
					return root.getComponentsContainer();
				}
			}
		}
		// TODO Auto-generated method stub
		return super.getParent(object);
	}

	@Override
	public void inputChanged(final Viewer vwr, final Object oldInput, final Object newInput) {
		super.inputChanged(vwr, oldInput, newInput);
		this.input = newInput;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		((ComposedAdapterFactory) this.adapterFactory).dispose();
		this.adapterFactory = null;
		super.dispose();
	}

}
