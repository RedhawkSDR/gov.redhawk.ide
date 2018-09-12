/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.xml.sax.SAXParseException;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dmd.DmdPackage;
import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getLoadStatus <em>Load Status</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getState <em>State</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getComponentsContainer <em>Components Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getWaveformsContainer <em>Waveforms Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getDevicesContainer <em>Devices Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getServicesContainer <em>Services Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getNodesContainer <em>Nodes Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getSharedLibrariesContainer <em>Shared Libraries Container</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getDomainConfiguration <em>Domain Configuration</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getIdlLibrary <em>Idl Library</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getDevFileSystemRoot <em>Dev File System Root</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SdrRootImpl#getDomFileSystemRoot <em>Dom File System Root</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SdrRootImpl extends EObjectImpl implements SdrRoot {

	// END GENERATED CODE

	private static final Debug DEBUG = new Debug(IdeSdrActivator.getDefault(), "gov.redhawk.ide.sdr/debug/invalidDevices"); //$NON-NLS-1$

	private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	private static final String HIDDEN_DIR_PREFIX = "."; //$NON-NLS-1$

	// BEGIN GENERATED CODE

	/**
	 * The default value of the '{@link #getLoadStatus() <em>Load Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 5.0
	 * <!-- end-user-doc -->
	 * @see #getLoadStatus()
	 * @generated NOT
	 * @ordered
	 */
	protected static final IStatus LOAD_STATUS_EDEFAULT = Status.OK_STATUS;

	/**
	 * The cached value of the '{@link #getLoadStatus() <em>Load Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLoadStatus()
	 * @generated
	 * @ordered
	 */
	protected IStatus loadStatus = LOAD_STATUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected static final LoadState STATE_EDEFAULT = LoadState.UNLOADED;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected LoadState state = STATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getComponentsContainer() <em>Components Container</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentsContainer()
	 * @generated
	 * @ordered
	 */
	protected ComponentsContainer componentsContainer;

	/**
	 * The cached value of the '{@link #getWaveformsContainer() <em>Waveforms Container</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaveformsContainer()
	 * @generated
	 * @ordered
	 */
	protected WaveformsContainer waveformsContainer;

	/**
	 * The cached value of the '{@link #getDevicesContainer() <em>Devices Container</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevicesContainer()
	 * @generated
	 * @ordered
	 */
	protected DevicesContainer devicesContainer;

	/**
	 * The cached value of the '{@link #getServicesContainer() <em>Services Container</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServicesContainer()
	 * @generated
	 * @ordered
	 */
	protected ServicesContainer servicesContainer;

	/**
	 * The cached value of the '{@link #getNodesContainer() <em>Nodes Container</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNodesContainer()
	 * @generated
	 * @ordered
	 */
	protected NodesContainer nodesContainer;

	/**
	 * The cached value of the '{@link #getSharedLibrariesContainer() <em>Shared Libraries Container</em>}' containment
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSharedLibrariesContainer()
	 * @generated
	 * @ordered
	 */
	protected SharedLibrariesContainer sharedLibrariesContainer;

	/**
	 * The cached value of the '{@link #getDomainConfiguration() <em>Domain Configuration</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDomainConfiguration()
	 * @generated
	 * @ordered
	 */
	protected DomainManagerConfiguration domainConfiguration;

	/**
	 * The cached value of the '{@link #getIdlLibrary() <em>Idl Library</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdlLibrary()
	 * @generated
	 * @ordered
	 */
	protected IdlLibrary idlLibrary;

	/**
	 * The default value of the '{@link #getDevFileSystemRoot() <em>Dev File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevFileSystemRoot()
	 * @generated
	 * @ordered
	 */
	protected static final URI DEV_FILE_SYSTEM_ROOT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDevFileSystemRoot() <em>Dev File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevFileSystemRoot()
	 * @generated
	 * @ordered
	 */
	protected URI devFileSystemRoot = DEV_FILE_SYSTEM_ROOT_EDEFAULT;

	/**
	 * The default value of the '{@link #getDomFileSystemRoot() <em>Dom File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDomFileSystemRoot()
	 * @generated
	 * @ordered
	 */
	protected static final URI DOM_FILE_SYSTEM_ROOT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDomFileSystemRoot() <em>Dom File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDomFileSystemRoot()
	 * @generated
	 * @ordered
	 */
	protected URI domFileSystemRoot = DOM_FILE_SYSTEM_ROOT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected SdrRootImpl() {
		// END GENERATED CODE
		super();
		setComponentsContainer(SdrFactory.eINSTANCE.createComponentsContainer());
		setDevicesContainer(SdrFactory.eINSTANCE.createDevicesContainer());
		setWaveformsContainer(SdrFactory.eINSTANCE.createWaveformsContainer());
		setNodesContainer(SdrFactory.eINSTANCE.createNodesContainer());
		setServicesContainer(SdrFactory.eINSTANCE.createServicesContainer());
		setSharedLibrariesContainer(SdrFactory.eINSTANCE.createSharedLibrariesContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.SDR_ROOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IStatus getLoadStatus() {
		return loadStatus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	private void setLoadStatus(IStatus newLoadStatus) {
		IStatus oldLoadStatus = loadStatus;
		loadStatus = newLoadStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__LOAD_STATUS, oldLoadStatus, loadStatus));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LoadState getState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setState(LoadState newState) {
		LoadState oldState = state;
		state = newState == null ? STATE_EDEFAULT : newState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__STATE, oldState, state));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComponentsContainer getComponentsContainer() {
		return componentsContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetComponentsContainer(ComponentsContainer newComponentsContainer, NotificationChain msgs) {
		ComponentsContainer oldComponentsContainer = componentsContainer;
		componentsContainer = newComponentsContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER, oldComponentsContainer,
				newComponentsContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponentsContainer(ComponentsContainer newComponentsContainer) {
		if (newComponentsContainer != componentsContainer) {
			NotificationChain msgs = null;
			if (componentsContainer != null)
				msgs = ((InternalEObject) componentsContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER, null,
					msgs);
			if (newComponentsContainer != null)
				msgs = ((InternalEObject) newComponentsContainer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER, null,
					msgs);
			msgs = basicSetComponentsContainer(newComponentsContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER, newComponentsContainer, newComponentsContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WaveformsContainer getWaveformsContainer() {
		return waveformsContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWaveformsContainer(WaveformsContainer newWaveformsContainer, NotificationChain msgs) {
		WaveformsContainer oldWaveformsContainer = waveformsContainer;
		waveformsContainer = newWaveformsContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, oldWaveformsContainer,
				newWaveformsContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWaveformsContainer(WaveformsContainer newWaveformsContainer) {
		if (newWaveformsContainer != waveformsContainer) {
			NotificationChain msgs = null;
			if (waveformsContainer != null)
				msgs = ((InternalEObject) waveformsContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, null,
					msgs);
			if (newWaveformsContainer != null)
				msgs = ((InternalEObject) newWaveformsContainer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, null,
					msgs);
			msgs = basicSetWaveformsContainer(newWaveformsContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, newWaveformsContainer, newWaveformsContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DevicesContainer getDevicesContainer() {
		return devicesContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDevicesContainer(DevicesContainer newDevicesContainer, NotificationChain msgs) {
		DevicesContainer oldDevicesContainer = devicesContainer;
		devicesContainer = newDevicesContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__DEVICES_CONTAINER, oldDevicesContainer,
				newDevicesContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDevicesContainer(DevicesContainer newDevicesContainer) {
		if (newDevicesContainer != devicesContainer) {
			NotificationChain msgs = null;
			if (devicesContainer != null)
				msgs = ((InternalEObject) devicesContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__DEVICES_CONTAINER, null, msgs);
			if (newDevicesContainer != null)
				msgs = ((InternalEObject) newDevicesContainer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__DEVICES_CONTAINER, null, msgs);
			msgs = basicSetDevicesContainer(newDevicesContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__DEVICES_CONTAINER, newDevicesContainer, newDevicesContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServicesContainer getServicesContainer() {
		return servicesContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetServicesContainer(ServicesContainer newServicesContainer, NotificationChain msgs) {
		ServicesContainer oldServicesContainer = servicesContainer;
		servicesContainer = newServicesContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__SERVICES_CONTAINER, oldServicesContainer,
				newServicesContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setServicesContainer(ServicesContainer newServicesContainer) {
		if (newServicesContainer != servicesContainer) {
			NotificationChain msgs = null;
			if (servicesContainer != null)
				msgs = ((InternalEObject) servicesContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__SERVICES_CONTAINER, null, msgs);
			if (newServicesContainer != null)
				msgs = ((InternalEObject) newServicesContainer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__SERVICES_CONTAINER, null, msgs);
			msgs = basicSetServicesContainer(newServicesContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__SERVICES_CONTAINER, newServicesContainer, newServicesContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NodesContainer getNodesContainer() {
		return nodesContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNodesContainer(NodesContainer newNodesContainer, NotificationChain msgs) {
		NodesContainer oldNodesContainer = nodesContainer;
		nodesContainer = newNodesContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__NODES_CONTAINER, oldNodesContainer,
				newNodesContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNodesContainer(NodesContainer newNodesContainer) {
		if (newNodesContainer != nodesContainer) {
			NotificationChain msgs = null;
			if (nodesContainer != null)
				msgs = ((InternalEObject) nodesContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__NODES_CONTAINER, null, msgs);
			if (newNodesContainer != null)
				msgs = ((InternalEObject) newNodesContainer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__NODES_CONTAINER, null, msgs);
			msgs = basicSetNodesContainer(newNodesContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__NODES_CONTAINER, newNodesContainer, newNodesContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SharedLibrariesContainer getSharedLibrariesContainer() {
		return sharedLibrariesContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSharedLibrariesContainer(SharedLibrariesContainer newSharedLibrariesContainer, NotificationChain msgs) {
		SharedLibrariesContainer oldSharedLibrariesContainer = sharedLibrariesContainer;
		sharedLibrariesContainer = newSharedLibrariesContainer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER,
				oldSharedLibrariesContainer, newSharedLibrariesContainer);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSharedLibrariesContainer(SharedLibrariesContainer newSharedLibrariesContainer) {
		if (newSharedLibrariesContainer != sharedLibrariesContainer) {
			NotificationChain msgs = null;
			if (sharedLibrariesContainer != null)
				msgs = ((InternalEObject) sharedLibrariesContainer).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER, null, msgs);
			if (newSharedLibrariesContainer != null)
				msgs = ((InternalEObject) newSharedLibrariesContainer).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER, null, msgs);
			msgs = basicSetSharedLibrariesContainer(newSharedLibrariesContainer, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER, newSharedLibrariesContainer,
				newSharedLibrariesContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DomainManagerConfiguration getDomainConfiguration() {
		if (domainConfiguration != null && domainConfiguration.eIsProxy()) {
			InternalEObject oldDomainConfiguration = (InternalEObject) domainConfiguration;
			domainConfiguration = (DomainManagerConfiguration) eResolveProxy(oldDomainConfiguration);
			if (domainConfiguration != oldDomainConfiguration) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION, oldDomainConfiguration,
						domainConfiguration));
			}
		}
		return domainConfiguration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DomainManagerConfiguration basicGetDomainConfiguration() {
		return domainConfiguration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDomainConfiguration(DomainManagerConfiguration newDomainConfiguration) {
		DomainManagerConfiguration oldDomainConfiguration = domainConfiguration;
		domainConfiguration = newDomainConfiguration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION, oldDomainConfiguration, domainConfiguration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdlLibrary getIdlLibrary() {
		return idlLibrary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIdlLibrary(IdlLibrary newIdlLibrary, NotificationChain msgs) {
		IdlLibrary oldIdlLibrary = idlLibrary;
		idlLibrary = newIdlLibrary;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__IDL_LIBRARY, oldIdlLibrary, newIdlLibrary);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdlLibrary(IdlLibrary newIdlLibrary) {
		if (newIdlLibrary != idlLibrary) {
			NotificationChain msgs = null;
			if (idlLibrary != null)
				msgs = ((InternalEObject) idlLibrary).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__IDL_LIBRARY, null, msgs);
			if (newIdlLibrary != null)
				msgs = ((InternalEObject) newIdlLibrary).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SdrPackage.SDR_ROOT__IDL_LIBRARY, null, msgs);
			msgs = basicSetIdlLibrary(newIdlLibrary, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__IDL_LIBRARY, newIdlLibrary, newIdlLibrary));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public URI getDevFileSystemRoot() {
		return devFileSystemRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDevFileSystemRoot(URI newDevFileSystemRoot) {
		URI oldDevFileSystemRoot = devFileSystemRoot;
		devFileSystemRoot = newDevFileSystemRoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT, oldDevFileSystemRoot, devFileSystemRoot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public URI getDomFileSystemRoot() {
		return domFileSystemRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDomFileSystemRoot(URI newDomFileSystemRoot) {
		URI oldDomFileSystemRoot = domFileSystemRoot;
		domFileSystemRoot = newDomFileSystemRoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT, oldDomFileSystemRoot, domFileSystemRoot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public synchronized void load(IProgressMonitor monitor) {
		// END GENERATED CODE
		if (getState() == LoadState.LOADED) {
			return;
		}

		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(this);
		if (editingDomain == null) {
			setState(null);
			setLoadStatus(new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_NoEditingDomain, null));
			return;
		}

		editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this, SdrPackage.Literals.SDR_ROOT__STATE, LoadState.LOADING));

		// Start with status OK, but set an error message just in case there is an error later
		final CustomMultiStatus overallLoadStatus = new CustomMultiStatus(IdeSdrActivator.PLUGIN_ID, IStatus.OK, Messages.SdrRootImpl_ProblemsLoading, null);
		final SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.SdrRootImpl_ProgressLoading, 3);

		try {
			overallLoadStatus.merge(loadDomFileSystem(eResource().getResourceSet(), subMonitor.newChild(1)));
			overallLoadStatus.merge(loadDevFileSystem(eResource().getResourceSet(), subMonitor.newChild(1)));
			overallLoadStatus.merge(loadIdlLibrary(subMonitor.newChild(1)));
			overallLoadStatus.merge(checkForDuplicates());

		} finally {
			if (!overallLoadStatus.isOK()) {
				editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this, SdrPackage.Literals.SDR_ROOT__LOAD_STATUS, overallLoadStatus));
			} else {
				editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this, SdrPackage.Literals.SDR_ROOT__LOAD_STATUS, Status.OK_STATUS));
			}

			editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this, SdrPackage.Literals.SDR_ROOT__STATE, LoadState.LOADED));
			if (monitor != null) {
				monitor.done();
			}
		}
		// BEGIN GENERATED CODE
	}

	// END GENERATED CODE

	/**
	 * Check through all objects in the Target SDR and ensure that no duplicate ID's are found
	 */
	private IStatus checkForDuplicates() {
		final CustomMultiStatus duplicateStatus = new CustomMultiStatus(IdeSdrActivator.PLUGIN_ID, IStatus.OK, null, null);
		Map<String, EObject> duplicatesMap = new HashMap<String, EObject>();
		// Components
		for (SoftPkg spd : getComponentsContainer().getComponents()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, spd, spd.getId());
		}
		// Shared Libraries
		for (SoftPkg spd : getSharedLibrariesContainer().getComponents()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, spd, spd.getId());
		}
		// Devices
		for (SoftPkg spd : getDevicesContainer().getComponents()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, spd, spd.getId());
		}
		// Services
		for (SoftPkg spd : getServicesContainer().getComponents()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, spd, spd.getId());
		}
		// Waveforms
		for (SoftwareAssembly waveform : getWaveformsContainer().getWaveforms()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, waveform, waveform.getId());
		}
		// Nodes
		for (DeviceConfiguration node : getNodesContainer().getNodes()) {
			updateDuplicateMap(duplicateStatus, duplicatesMap, node, node.getId());
		}

		return duplicateStatus;
	}

	/**
	 * Add a Status.ERROR for every duplicate found
	 */
	private void updateDuplicateMap(CustomMultiStatus duplicateStatus, Map<String, EObject> duplicatesMap, EObject newObj, String key) {
		EObject exitingObj = duplicatesMap.put(key, newObj);
		if (exitingObj != null) {
			String msg = Messages.bind(Messages.SdrRootImpl_DuplicateID, convertToFilePath(newObj.eResource().getURI()),
				convertToFilePath(exitingObj.eResource().getURI()));
			duplicateStatus.merge(new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, msg));
		}
	}

	private IStatus loadIdlLibrary(IProgressMonitor monitor) {
		try {
			if ((getIdlLibrary() != null) && (getIdlLibrary().getLoadStatus() == null)) {
				getIdlLibrary().load(monitor);
			}
			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_FailedToLoadIDLLibrary, e);
		}
	}

	private IStatus loadDevFileSystem(ResourceSet resourceSet, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, 20);

		if (getDevFileSystemRoot() == null) {
			return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_SdrRootDevIsNull, null);
		}

		IFileStore devRoot;
		try {
			devRoot = EFS.getStore(java.net.URI.create(getDevFileSystemRoot().toString()));
			if (!devRoot.fetchInfo(EFS.NONE, progress.newChild(1)).exists()) {
				// This isn't necessarily an error, since the SDR doesn't have to contain a dev file system
				return new Status(IStatus.WARNING, IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_SdrRootDevDoesNotExist, null);
			}
		} catch (CoreException e) {
			String msg = Messages.bind(Messages.SdrRootImpl_CannotAccessSdrRootDev, getDevFileSystemRoot());
			return new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, msg, e);
		}

		return processStore(resourceSet, devRoot, progress.newChild(19));
	}

	private IStatus loadDomFileSystem(ResourceSet resourceSet, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, 20);

		if (getDomFileSystemRoot() == null) {
			return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_SdrRootDomIsNull, null);
		}

		IFileStore domRoot;
		try {
			domRoot = EFS.getStore(java.net.URI.create(getDomFileSystemRoot().toString()));
			if (!domRoot.fetchInfo(EFS.NONE, progress.newChild(1)).exists()) {
				// This isn't necessarily an error, since the SDR doesn't have to contain a dom file system
				return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_SdrRootDomDoesNotExist, null);
			}
		} catch (CoreException e) {
			String msg = Messages.bind(Messages.SdrRootImpl_CannotAccessSdrRootDom, getDomFileSystemRoot());
			return new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, msg, e);
		}

		return processStore(resourceSet, domRoot, monitor);
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public synchronized void unload(IProgressMonitor monitor) {
		// END GENERATED CODE
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.SdrRootImpl_ProgressUnloading, 2);

		// Unload each XML file and remove the resource
		final Resource[] resources = eResource().getResourceSet().getResources().toArray(new Resource[eResource().getResourceSet().getResources().size()]);
		for (final Resource resource : resources) {
			if (resource == eResource()) {
				continue;
			} else if (resource.getURI().lastSegment().endsWith(".xml")) { //$NON-NLS-1$
				resource.unload();
				eResource().getResourceSet().getResources().remove(resource);
			}
		}
		subMonitor.worked(1);

		// Empty this object of its references, etc
		ScaModelCommand.execute(this, () -> {
			getComponentsContainer().getComponents().clear();
			getComponentsContainer().getChildContainers().clear();
			getDevicesContainer().getComponents().clear();
			getDevicesContainer().getChildContainers().clear();
			getNodesContainer().getNodes().clear();
			getNodesContainer().getChildContainers().clear();
			getServicesContainer().getComponents().clear();
			getServicesContainer().getChildContainers().clear();
			getSharedLibrariesContainer().getComponents().clear();
			getSharedLibrariesContainer().getChildContainers().clear();
			getWaveformsContainer().getWaveforms().clear();
			getWaveformsContainer().getChildContainers().clear();
			setLoadStatus(null);
			setState(LoadState.UNLOADED);
			setDomainConfiguration(null);
		});
		subMonitor.worked(1);
		subMonitor.done();
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public synchronized void reload(IProgressMonitor monitor) {
		// END GENERATED CODE
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.SdrRootImpl_ProgressReloading, 3);
		final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(this);
		if (editingDomain == null) {
			throw new IllegalStateException(Messages.SdrRootImpl_NoEditingDomain);
		}
		unload(subMonitor.newChild(1));
		load(subMonitor.newChild(1));
		if (getIdlLibrary() != null) {
			try {
				getIdlLibrary().reload(subMonitor.newChild(1));
			} catch (CoreException e) {
				IdeSdrActivator.getDefault().getLog().log(new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, Messages.SdrRootImpl_FailedToReloadSdr, e));
			}
		}
		subMonitor.done();
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void setSdrRoot(URI sdrRoot, String domPath, String devPath) {
		// END GENERATED CODE
		if (sdrRoot != null) {
			final URI domUri;
			if (domPath != null) {
				String[] path = domPath.split(PATH_SEPARATOR);
				domUri = URI.createHierarchicalURI(ScaFileSystemConstants.SCHEME, null, null, null,
					QueryParser.createQuery(Collections.singletonMap(ScaFileSystemConstants.QUERY_PARAM_FS, sdrRoot.appendSegments(path).toString())), null);
			} else {
				domUri = null;
			}
			setDomFileSystemRoot(domUri);

			final URI devUri;
			if (devPath != null) {
				String[] path = devPath.split(PATH_SEPARATOR);
				devUri = URI.createHierarchicalURI(ScaFileSystemConstants.SCHEME, null, null, null,
					QueryParser.createQuery(Collections.singletonMap(ScaFileSystemConstants.QUERY_PARAM_FS, sdrRoot.appendSegments(path).toString())), null);
			} else {
				devUri = null;
			}
			setDevFileSystemRoot(devUri);
		} else {
			setDomFileSystemRoot(null);
			setDevFileSystemRoot(null);
		}
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public Resource getDevResource(String path) {
		// END GENERATED CODE
		if (path == null) {
			return null;
		}
		path = trimPath(path);
		return eResource().getResourceSet().getResource(getDevFileSystemRoot().appendSegments(path.split(PATH_SEPARATOR)), true);
		// BEGIN GENERATED CODE
	}

	// END GENERATED CODE

	private String trimPath(String path) {
		if (path != null && path.charAt(0) == '/') {
			return trimPath(path.substring(1));
		}
		return path;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public Resource getDomResource(String path) {
		// END GENERATED CODE
		if (path == null) {
			return null;
		}
		path = trimPath(path);
		return eResource().getResourceSet().getResource(getDomFileSystemRoot().appendSegments(path.split(PATH_SEPARATOR)), true);
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
			return basicSetComponentsContainer(null, msgs);
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
			return basicSetWaveformsContainer(null, msgs);
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
			return basicSetDevicesContainer(null, msgs);
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
			return basicSetServicesContainer(null, msgs);
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
			return basicSetNodesContainer(null, msgs);
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
			return basicSetSharedLibrariesContainer(null, msgs);
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			return basicSetIdlLibrary(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SdrPackage.SDR_ROOT__LOAD_STATUS:
			return getLoadStatus();
		case SdrPackage.SDR_ROOT__STATE:
			return getState();
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
			return getComponentsContainer();
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
			return getWaveformsContainer();
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
			return getDevicesContainer();
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
			return getServicesContainer();
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
			return getNodesContainer();
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
			return getSharedLibrariesContainer();
		case SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION:
			if (resolve)
				return getDomainConfiguration();
			return basicGetDomainConfiguration();
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			return getIdlLibrary();
		case SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT:
			return getDevFileSystemRoot();
		case SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT:
			return getDomFileSystemRoot();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case SdrPackage.SDR_ROOT__LOAD_STATUS:
			setLoadStatus((IStatus) newValue);
			return;
		case SdrPackage.SDR_ROOT__STATE:
			setState((LoadState) newValue);
			return;
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
			setComponentsContainer((ComponentsContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
			setWaveformsContainer((WaveformsContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
			setDevicesContainer((DevicesContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
			setServicesContainer((ServicesContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
			setNodesContainer((NodesContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
			setSharedLibrariesContainer((SharedLibrariesContainer) newValue);
			return;
		case SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION:
			setDomainConfiguration((DomainManagerConfiguration) newValue);
			return;
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			setIdlLibrary((IdlLibrary) newValue);
			return;
		case SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT:
			setDevFileSystemRoot((URI) newValue);
			return;
		case SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT:
			setDomFileSystemRoot((URI) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case SdrPackage.SDR_ROOT__LOAD_STATUS:
			setLoadStatus(LOAD_STATUS_EDEFAULT);
			return;
		case SdrPackage.SDR_ROOT__STATE:
			setState(STATE_EDEFAULT);
			return;
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
			setComponentsContainer((ComponentsContainer) null);
			return;
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
			setWaveformsContainer((WaveformsContainer) null);
			return;
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
			setDevicesContainer((DevicesContainer) null);
			return;
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
			setServicesContainer((ServicesContainer) null);
			return;
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
			setNodesContainer((NodesContainer) null);
			return;
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
			setSharedLibrariesContainer((SharedLibrariesContainer) null);
			return;
		case SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION:
			setDomainConfiguration((DomainManagerConfiguration) null);
			return;
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			setIdlLibrary((IdlLibrary) null);
			return;
		case SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT:
			setDevFileSystemRoot(DEV_FILE_SYSTEM_ROOT_EDEFAULT);
			return;
		case SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT:
			setDomFileSystemRoot(DOM_FILE_SYSTEM_ROOT_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case SdrPackage.SDR_ROOT__LOAD_STATUS:
			return LOAD_STATUS_EDEFAULT == null ? loadStatus != null : !LOAD_STATUS_EDEFAULT.equals(loadStatus);
		case SdrPackage.SDR_ROOT__STATE:
			return state != STATE_EDEFAULT;
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
			return componentsContainer != null;
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
			return waveformsContainer != null;
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
			return devicesContainer != null;
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
			return servicesContainer != null;
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
			return nodesContainer != null;
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
			return sharedLibrariesContainer != null;
		case SdrPackage.SDR_ROOT__DOMAIN_CONFIGURATION:
			return domainConfiguration != null;
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			return idlLibrary != null;
		case SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT:
			return DEV_FILE_SYSTEM_ROOT_EDEFAULT == null ? devFileSystemRoot != null : !DEV_FILE_SYSTEM_ROOT_EDEFAULT.equals(devFileSystemRoot);
		case SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT:
			return DOM_FILE_SYSTEM_ROOT_EDEFAULT == null ? domFileSystemRoot != null : !DOM_FILE_SYSTEM_ROOT_EDEFAULT.equals(domFileSystemRoot);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (loadStatus: ");
		result.append(loadStatus);
		result.append(", state: ");
		result.append(state);
		result.append(", devFileSystemRoot: ");
		result.append(devFileSystemRoot);
		result.append(", domFileSystemRoot: ");
		result.append(domFileSystemRoot);
		result.append(')');
		return result.toString();
	}

	// END GENERATED CODE

	/**
	 * @param resourceSet
	 * @param parent
	 * @param monitor
	 * @throws CoreException
	 */
	private IStatus processDirectory(ResourceSet resourceSet, final IFileStore parent, final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.bind(Messages.SdrRootImpl_ProgressProcessingDirectory, parent.getName()), 100);
		IFileStore[] childStores;
		try {
			childStores = parent.childStores(EFS.NONE, subMonitor.newChild(10)); // SUPPRESS CHECKSTYLE MAGIC NUMBER
		} catch (CoreException e) {
			String msg = Messages.bind(Messages.SdrRootImpl_UnableToGetChildrenOfDir, parent);
			return new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, msg, e);
		}

		final SubMonitor loopProgress = subMonitor.newChild(90).setWorkRemaining(childStores.length); // SUPPRESS CHECKSTYLE MAGIC NUMBER
		CustomMultiStatus multiStatus = new CustomMultiStatus(IdeSdrActivator.PLUGIN_ID, Status.OK,
			Messages.bind(Messages.SdrRootImpl_FailedToProcessChildren, parent), null);
		for (final IFileStore child : childStores) {
			multiStatus.merge(processStore(resourceSet, child, loopProgress.newChild(1)));
		}
		return multiStatus;
	}

	/**
	 * @param resourceSet
	 * @param child
	 * @param monitor
	 * @throws CoreException
	 */
	private IStatus processStore(ResourceSet resourceSet, final IFileStore child, final IProgressMonitor monitor) {
		if (child.getName().startsWith(HIDDEN_DIR_PREFIX)) {
			return Status.OK_STATUS;
		}
		if (child.fetchInfo().isDirectory()) {
			return processDirectory(resourceSet, child, monitor);
		} else {
			if (child.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				return loadSpd(resourceSet, child, monitor);
			} else if (child.getName().endsWith(SadPackage.FILE_EXTENSION)) {
				return loadSad(resourceSet, child, monitor);
			} else if (child.getName().endsWith(DcdPackage.FILE_EXTENSION)) {
				return loadDcd(resourceSet, child, monitor);
			} else if (child.getName().endsWith(DmdPackage.FILE_EXTENSION)) {
				return loadDmd(resourceSet, child, monitor);
			} else {
				return Status.OK_STATUS;
			}
		}
	}

	/**
	 * @param resourceSet
	 * @param spdFile
	 * @param monitor
	 */
	private IStatus loadSpd(ResourceSet resourceSet, final IFileStore spdFile, final IProgressMonitor monitor) {
		SubMonitor submonitor = SubMonitor.convert(monitor, Messages.bind(Messages.SdrRootImpl_ProgressLoadingFile, spdFile.getName()), 2);

		URI spdFileUri = URI.createURI(spdFile.toURI().toString());
		SoftPkg softPkg;
		try {
			softPkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdFileUri, true));
		} catch (WrappedException e) {
			return statusForWrappedException(e, spdFileUri);
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Exception is returned in status
			String msg = Messages.bind(Messages.SdrRootImpl_FailedToLoadFile, convertToFilePath(spdFileUri));
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, msg, e);
		}
		if (softPkg == null) {
			String msg = Messages.bind(Messages.SdrRootImpl_FailedToLoadFile, convertToFilePath(spdFileUri));
			Exception e = new Exception(msg);
			e.fillInStackTrace();
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, msg, e);
		}
		submonitor.worked(1);

		// Determine type of spd to be added
		final SoftwareComponent component = ScaEcoreUtils.getFeature(softPkg, SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
			SpdPackage.Literals.DESCRIPTOR__COMPONENT);
		if (component != null) {
			ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(component);
			switch (type) {
			case DEVICE:
				addDevice(softPkg);
				break;
			case OTHER:
			case FILE_MANAGER:
			case FILE_SYSTEM:
			case LOG:
			case NAMING_SERVICE:
			case RESOURCE_FACTORY:
			case RESOURCE:
				addResource(softPkg, component);
				break;
			case EVENT_SERVICE:
			case SERVICE:
				addService(softPkg);
				break;
			case DEVICE_MANAGER:
			case DOMAIN_MANAGER:
				// Silently pass on these
				break;
			default:
				addUnknownComponentType(component, softPkg);
			}

		} else {
			// Treat it as a resource because all other types *must* have an
			// scd file
			addResource(softPkg, null);
		}
		submonitor.worked(1);
		return Status.OK_STATUS;
	}

	/**
	 * @param component
	 * @param softPkg
	 */
	private void addUnknownComponentType(final SoftwareComponent component, final SoftPkg softPkg) {
		IdeSdrActivator.getDefault().logWarning(
			Messages.bind(Messages.SdrRootImpl_ResourceIgnoredUnknownType, softPkg.getName(), component.getComponentType()));
	}

	/**
	 * @param softPkg
	 * @param component
	 */
	private void addResource(final SoftPkg softPkg, final SoftwareComponent component) {
		if (component != null) {
			for (final SupportsInterface iface : component.getComponentFeatures().getSupportsInterface()) {
				if (iface.getRepId().startsWith("IDL:CF/Device") || iface.getRepId().startsWith("IDL:CF/LoadableDevice") //$NON-NLS-1$ //$NON-NLS-2$
					|| iface.getRepId().startsWith("IDL:CF/ExecutableDevice") || iface.getRepId().startsWith("IDL:CF/AggregateDevice")) { //$NON-NLS-1$ //$NON-NLS-2$
					if (DEBUG.enabled) {
						DEBUG.message(Messages.SdrRootImpl_ResourceIsDeviceBasedOnInterfaces, softPkg.getName());
					}
					addDevice(softPkg);
					return;
				}
			}
		}

		for (Implementation impl : softPkg.getImplementation()) {
			if (impl.isExecutable()) {
				ScaModelCommand.execute(getComponentsContainer(), () -> {
					// Root container
					ComponentsContainer container = getComponentsContainer();

					// For each namespace segment in the SPD's name
					String[] segments = softPkg.getName().split("\\.");
					segments = Arrays.copyOf(segments, segments.length - 1);
					nextSegment: for (String segment : segments) {
						// Find the child container for the namespace
						for (ComponentsContainer childContainer : container.getChildContainers()) {
							if (segment.equals(childContainer.getName())) {
								container = childContainer;
								continue nextSegment;
							}
						}

						// Create a new container for the namespace
						ComponentsContainer childContainer = SdrFactory.eINSTANCE.createComponentsContainer(segment);
						container.getChildContainers().add(childContainer);
						container = childContainer;
					}

					container.getComponents().add(softPkg);
				});
				return;
			} else if (impl.isSharedLibrary()) {
				ScaModelCommand.execute(getComponentsContainer(), () -> {
					// Root container
					SharedLibrariesContainer container = getSharedLibrariesContainer();

					// For each namespace segment in the SPD's name
					String[] segments = softPkg.getName().split("\\.");
					segments = Arrays.copyOf(segments, segments.length - 1);
					nextSegment: for (String segment : segments) {
						// Find the child container for the namespace
						for (SharedLibrariesContainer childContainer : container.getChildContainers()) {
							if (segment.equals(childContainer.getName())) {
								container = childContainer;
								continue nextSegment;
							}
						}

						// Create a new container for the namespace
						SharedLibrariesContainer childContainer = SdrFactory.eINSTANCE.createSharedLibrariesContainer(segment);
						container.getChildContainers().add(childContainer);
						container = childContainer;
					}

					container.getComponents().add(softPkg);
				});
				return;
			}
		}
	}

	/**
	 * @param softPkg
	 */
	private void addDevice(final SoftPkg softPkg) {
		ScaModelCommand.execute(getComponentsContainer(), () -> {
			// Root container
			DevicesContainer container = getDevicesContainer();

			// For each namespace segment in the SPD's name
			String[] segments = softPkg.getName().split("\\.");
			segments = Arrays.copyOf(segments, segments.length - 1);
			nextSegment: for (String segment : segments) {
				// Find the child container for the namespace
				for (DevicesContainer childContainer : container.getChildContainers()) {
					if (segment.equals(childContainer.getName())) {
						container = childContainer;
						continue nextSegment;
					}
				}

				// Create a new container for the namespace
				DevicesContainer childContainer = SdrFactory.eINSTANCE.createDevicesContainer(segment);
				container.getChildContainers().add(childContainer);
				container = childContainer;
			}

			container.getComponents().add(softPkg);
		});
	}

	/**
	 * @param softPkg
	 */
	private void addService(final SoftPkg softPkg) {
		ScaModelCommand.execute(getComponentsContainer(), () -> {
			// Root container
			ServicesContainer container = getServicesContainer();

			// For each namespace segment in the SPD's name
			String[] segments = softPkg.getName().split("\\.");
			segments = Arrays.copyOf(segments, segments.length - 1);
			nextSegment: for (String segment : segments) {
				// Find the child container for the namespace
				for (ServicesContainer childContainer : container.getChildContainers()) {
					if (segment.equals(childContainer.getName())) {
						container = childContainer;
						continue nextSegment;
					}
				}

				// Create a new container for the namespace
				ServicesContainer childContainer = SdrFactory.eINSTANCE.createServicesContainer(segment);
				container.getChildContainers().add(childContainer);
				container = childContainer;
			}

			container.getComponents().add(softPkg);
		});
	}

	/**
	 * Converts the URI to a file path for display, if possible. Falls back on <code>toString()</code> if the URI can't
	 * be converted.
	 * @param fileURI
	 * @return
	 */
	private String convertToFilePath(org.eclipse.emf.common.util.URI fileURI) {
		final String uriToString = fileURI.toString();
		if (ScaFileSystemConstants.SCHEME.equals(fileURI.scheme())) {
			Map<String, String> query = QueryParser.parseQuery(fileURI.query());
			if (query == null) {
				return uriToString;
			}
			String fs = query.get(ScaFileSystemConstants.QUERY_PARAM_FS);
			if (fs == null) {
				return uriToString;
			}
			return new Path(URI.createURI(fs).toFileString()).append(fileURI.path()).toOSString();
		}
		return uriToString;
	}

	/**
	 * Provides an error status for a {@link WrappedException} generated by an attempted EMF resource load.
	 * @param e
	 * @param spdFileUri
	 * @return
	 */
	private IStatus statusForWrappedException(WrappedException e, URI spdFileUri) {
		if (e.getCause() instanceof SAXParseException) {
			SAXParseException sax = (SAXParseException) e.getCause();
			String msg = String.format("Failed to parse %s at line %d, column %d: %s", convertToFilePath(spdFileUri), sax.getLineNumber(), sax.getColumnNumber(), sax.getMessage());
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, msg, e.getCause());
		}
		return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, "Failed to load " + convertToFilePath(spdFileUri), e.getCause());
	}

	/**
	 * @param resourceSet
	 * @param sadFile
	 * @param monitor
	 */
	private IStatus loadSad(ResourceSet resourceSet, final IFileStore sadFile, final IProgressMonitor monitor) {
		SubMonitor submonitor = SubMonitor.convert(monitor, Messages.bind(Messages.SdrRootImpl_ProgressLoadingFile, sadFile.getName()), 2);

		URI sadFileUri = URI.createURI(sadFile.toURI().toString());
		SoftwareAssembly sad;
		try {
			sad = SoftwareAssembly.Util.getSoftwareAssembly(resourceSet.getResource(sadFileUri, true));
		} catch (WrappedException e) {
			return statusForWrappedException(e, sadFileUri);
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Exception is returned in status
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.bind(Messages.SdrRootImpl_FailedToLoadFile, convertToFilePath(sadFileUri)), e);
		}
		submonitor.worked(1);

		IStatus retVal = Status.OK_STATUS;
		for (SoftwareAssembly currentSad : getWaveformsContainer().getWaveforms()) {
			if (PluginUtil.equals(currentSad.getId(), sad.getId())) {
				String msg = Messages.bind(Messages.SdrRootImpl_DuplicateID, convertToFilePath(sadFileUri), convertToFilePath(currentSad.eResource().getURI()));
				retVal = new Status(Status.WARNING, IdeSdrActivator.PLUGIN_ID, msg);
				break;
			}
		}

		ScaModelCommand.execute(getComponentsContainer(), () -> {
			// Root container
			WaveformsContainer container = getWaveformsContainer();

			// For each namespace segment in the SPD's name
			String[] segments = sad.getName().split("\\.");
			segments = Arrays.copyOf(segments, segments.length - 1);
			nextSegment: for (String segment : segments) {
				// Find the child container for the namespace
				for (WaveformsContainer childContainer : container.getChildContainers()) {
					if (segment.equals(childContainer.getName())) {
						container = childContainer;
						continue nextSegment;
					}
				}

				// Create a new container for the namespace
				WaveformsContainer childContainer = SdrFactory.eINSTANCE.createWaveformsContainer(segment);
				container.getChildContainers().add(childContainer);
				container = childContainer;
			}

			container.getWaveforms().add(sad);
		});
		submonitor.worked(1);

		return retVal;
	}

	/**
	 * @param resourceSet
	 * @param dcdFile
	 * @param monitor
	 */
	private IStatus loadDcd(ResourceSet resourceSet, final IFileStore dcdFile, final IProgressMonitor monitor) {
		SubMonitor submonitor = SubMonitor.convert(monitor, Messages.bind(Messages.SdrRootImpl_ProgressLoadingFile, dcdFile.getName()), 2);

		URI dcdFileUri = URI.createURI(dcdFile.toURI().toString());
		DeviceConfiguration dcd;
		try {
			dcd = DeviceConfiguration.Util.getDeviceConfiguration(resourceSet.getResource(dcdFileUri, true));
		} catch (WrappedException e) {
			return statusForWrappedException(e, dcdFileUri);
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Exception is returned in status
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.bind(Messages.SdrRootImpl_FailedToLoadFile, convertToFilePath(dcdFileUri)), e);
		}

		IStatus retVal = Status.OK_STATUS;
		for (DeviceConfiguration current : getNodesContainer().getNodes()) {
			if (PluginUtil.equals(current.getId(), dcd.getId())) {
				String msg = Messages.bind(Messages.SdrRootImpl_DuplicateID, convertToFilePath(dcdFileUri), convertToFilePath(current.eResource().getURI()));
				retVal = new Status(Status.WARNING, IdeSdrActivator.PLUGIN_ID, msg);
				break;
			}
		}

		ScaModelCommand.execute(getComponentsContainer(), () -> {
			// Root container
			NodesContainer container = getNodesContainer();

			// For each namespace segment in the SPD's name
			String[] segments = dcd.getName().split("\\.");
			segments = Arrays.copyOf(segments, segments.length - 1);
			nextSegment: for (String segment : segments) {
				// Find the child container for the namespace
				for (NodesContainer childContainer : container.getChildContainers()) {
					if (segment.equals(childContainer.getName())) {
						container = childContainer;
						continue nextSegment;
					}
				}

				// Create a new container for the namespace
				NodesContainer childContainer = SdrFactory.eINSTANCE.createNodesContainer(segment);
				container.getChildContainers().add(childContainer);
				container = childContainer;
			}

			container.getNodes().add(dcd);
		});
		submonitor.worked(1);
		return retVal;
	}

	/**
	 * @param dmdFile
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 */
	private IStatus loadDmd(ResourceSet resourceSet, final IFileStore dmdFile, final IProgressMonitor monitor) {
		SubMonitor submonitor = SubMonitor.convert(monitor, Messages.bind(Messages.SdrRootImpl_ProgressLoadingFile, dmdFile.getName()), 1);

		URI dmdUri = URI.createURI(dmdFile.toURI().toString());
		DomainManagerConfiguration dmd;
		try {
			dmd = DomainManagerConfiguration.Util.getDomainManagerConfiguration(resourceSet.getResource(dmdUri, true));
			submonitor.worked(1);
		} catch (WrappedException e) {
			return statusForWrappedException(e, dmdUri);
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Exception is returned in status
			return new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.bind(Messages.SdrRootImpl_FailedToLoadFile, convertToFilePath(dmdUri)), e);
		}

		IStatus retVal = Status.OK_STATUS;
		if (getDomainConfiguration() != null) {
			String msg = Messages.bind(Messages.SdrRootImpl_ExtraDMDFile, convertToFilePath(dmdUri));
			return new Status(Status.WARNING, IdeSdrActivator.PLUGIN_ID, msg);
		}

		ScaModelCommand.execute(this, () -> {
			setDomainConfiguration(dmd);
		});
		submonitor.worked(1);
		return retVal;
	}

	// BEGIN GENERATED CODE

} // SdrRootImpl
