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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.provider;

import gov.redhawk.ide.sdr.util.SdrAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SdrItemProviderAdapterFactory extends SdrAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {

	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;
	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();
	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.SdrRoot} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SdrRootItemProvider sdrRootItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.SdrRoot}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSdrRootAdapter() {
		if (sdrRootItemProvider == null) {
			sdrRootItemProvider = new SdrRootItemProvider(this);
		}

		return sdrRootItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.ComponentsContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentsContainerItemProvider componentsContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.ComponentsContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createComponentsContainerAdapter() {
		if (componentsContainerItemProvider == null) {
			componentsContainerItemProvider = new ComponentsContainerItemProvider(this);
		}

		return componentsContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.ComponentsSubContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @since 5.0
	 */
	protected ComponentsSubContainerItemProvider componentsSubContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.ComponentsSubContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createComponentsSubContainerAdapter() {
		if (componentsSubContainerItemProvider == null) {
			componentsSubContainerItemProvider = new ComponentsSubContainerItemProvider(this);
		}

		return componentsSubContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.WaveformsContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaveformsContainerItemProvider waveformsContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.WaveformsContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createWaveformsContainerAdapter() {
		if (waveformsContainerItemProvider == null) {
			waveformsContainerItemProvider = new WaveformsContainerItemProvider(this);
		}

		return waveformsContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.DevicesContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DevicesContainerItemProvider devicesContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.DevicesContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createDevicesContainerAdapter() {
		if (devicesContainerItemProvider == null) {
			devicesContainerItemProvider = new DevicesContainerItemProvider(this);
		}

		return devicesContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.ServicesContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServicesContainerItemProvider servicesContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.ServicesContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createServicesContainerAdapter() {
		if (servicesContainerItemProvider == null) {
			servicesContainerItemProvider = new ServicesContainerItemProvider(this);
		}

		return servicesContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.SharedLibrariesContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @since 5.0
	 */
	protected SharedLibrariesContainerItemProvider sharedLibrariesContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.SharedLibrariesContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSharedLibrariesContainerAdapter() {
		if (sharedLibrariesContainerItemProvider == null) {
			sharedLibrariesContainerItemProvider = new SharedLibrariesContainerItemProvider(this);
		}

		return sharedLibrariesContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link gov.redhawk.ide.sdr.NodesContainer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodesContainerItemProvider nodesContainerItemProvider;

	/**
	 * This creates an adapter for a {@link gov.redhawk.ide.sdr.NodesContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createNodesContainerAdapter() {
		if (nodesContainerItemProvider == null) {
			nodesContainerItemProvider = new NodesContainerItemProvider(this);
		}

		return nodesContainerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StringToAbstractPropertyItemProvider stringToAbstractPropertyItemProvider;

	/**
	 * This creates an adapter for a {@link java.util.Map.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createStringToAbstractPropertyAdapter() {
		if (stringToAbstractPropertyItemProvider == null) {
			stringToAbstractPropertyItemProvider = new StringToAbstractPropertyItemProvider(this);
		}

		return stringToAbstractPropertyItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class< ? >) || (((Class< ? >) type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void dispose() {
		if (sdrRootItemProvider != null)
			sdrRootItemProvider.dispose();
		if (componentsContainerItemProvider != null)
			componentsContainerItemProvider.dispose();
		if (componentsSubContainerItemProvider != null)
			componentsSubContainerItemProvider.dispose();
		if (waveformsContainerItemProvider != null)
			waveformsContainerItemProvider.dispose();
		if (devicesContainerItemProvider != null)
			devicesContainerItemProvider.dispose();
		if (servicesContainerItemProvider != null)
			servicesContainerItemProvider.dispose();
		if (sharedLibrariesContainerItemProvider != null)
			sharedLibrariesContainerItemProvider.dispose();
		if (nodesContainerItemProvider != null)
			nodesContainerItemProvider.dispose();
		if (stringToAbstractPropertyItemProvider != null)
			stringToAbstractPropertyItemProvider.dispose();
	}

}
