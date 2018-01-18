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

import java.util.Arrays;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.jface.viewers.Viewer;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.provider.RepositoryItemProviderAdapterFactory;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.provider.SdrItemProviderAdapterFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.SoftPkg;

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
		if (!(this.input instanceof SdrRoot)) {
			return super.getParent(object);
		}
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
			case DEVICE:
				return getParentOf(root.getDevicesContainer(), spd);
			case SERVICE:
				return getParentOf(root.getServicesContainer(), spd);
			case RESOURCE:
				boolean isExecutable = run(new RunnableWithResult.Impl<Boolean>() {
					public void run() {
						setResult(spd.getImplementation().size() > 0 && spd.getImplementation().get(0).isExecutable());
					}
				});
				if (isExecutable) {
					return getParentOf(root.getComponentsContainer(), spd);
				} else {
					return getParentOf(root.getSharedLibrariesContainer(), spd);
				}
			default:
				return getParentOf(root.getComponentsContainer(), spd);
			}
		} else if (object instanceof SoftwareAssembly) {
			final SoftwareAssembly sad = (SoftwareAssembly) object;
			return getParentOf(root, sad);
		} else if (object instanceof DeviceConfiguration) {
			final DeviceConfiguration dcd = (DeviceConfiguration) object;
			return getParentOf(root, dcd);
		}

		return super.getParent(object);
	}

	private SoftPkgRegistry getParentOf(SoftPkgRegistry root, SoftPkg spd) {
		return run(new RunnableWithResult.Impl<SoftPkgRegistry>() {
			public void run() {
				if (spd.getName() == null) {
					return;
				}

				SoftPkgRegistry container = root;
				String[] segments = spd.getName().split("\\.");
				segments = Arrays.copyOf(segments, segments.length - 1);
				nextSegment: for (String segment : segments) {
					for (EObject childContent : container.eContents()) {
						if (!(childContent instanceof SoftPkgRegistry)) {
							continue;
						}
						SoftPkgRegistry childContainer = (SoftPkgRegistry) childContent;
						if (segment.equals(childContainer.getName())) {
							container = childContainer;
							continue nextSegment;
						}
					}
					return;
				}
				setResult(container);
			}
		});
	}

	private WaveformsContainer getParentOf(SdrRoot root, SoftwareAssembly sad) {
		return run(new RunnableWithResult.Impl<WaveformsContainer>() {
			public void run() {
				if (sad.getName() == null) {
					return;
				}

				WaveformsContainer container = root.getWaveformsContainer();
				String[] segments = sad.getName().split("\\.");
				segments = Arrays.copyOf(segments, segments.length - 1);
				nextSegment: for (String segment : segments) {
					for (WaveformsContainer childContainer : container.getChildContainers()) {
						if (segment.equals(childContainer.getName())) {
							container = childContainer;
							continue nextSegment;
						}
					}
					return;
				}
				setResult(container);
			}
		});
	}

	private NodesContainer getParentOf(SdrRoot root, DeviceConfiguration dcd) {
		return run(new RunnableWithResult.Impl<NodesContainer>() {
			public void run() {
				if (dcd.getName() == null) {
					return;
				}

				NodesContainer container = root.getNodesContainer();
				String[] segments = dcd.getName().split("\\.");
				segments = Arrays.copyOf(segments, segments.length - 1);
				nextSegment: for (String segment : segments) {
					for (NodesContainer childContainer : container.getChildContainers()) {
						if (segment.equals(childContainer.getName())) {
							container = childContainer;
							continue nextSegment;
						}
					}
					return;
				}
				setResult(container);
			}
		});
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
