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
package gov.redhawk.ide.debug.impl;


import gov.redhawk.core.filemanager.IFileManager;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.listeners.DisposableObjectContainerListener;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.ide.debug.internal.cf.impl.DeviceManagerImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.ORBUtil;
import gov.redhawk.sca.util.SilentJob;

import java.util.Collection;

import mil.jpeojtrs.sca.dcd.DcdDocumentRoot;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.jacorb.naming.Name;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.Application;
import CF.ApplicationHelper;
import CF.FileManager;
import CF.FileManagerHelper;
import CF.FileManagerPOATie;
import CF.LifeCyclePackage.InitializeError;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#isDisposed <em>Disposed</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getOrb <em>Orb</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getPoa <em>Poa</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getWaveforms <em>Waveforms</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getSandboxWaveform <em>Sandbox Waveform</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getSandboxDeviceManager <em>Sandbox Device Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getRootContext <em>Root Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getFileManager <em>File Manager</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LocalScaImpl extends EObjectImpl implements LocalSca {
	/**
	 * The default value of the '{@link #isDisposed() <em>Disposed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisposed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DISPOSED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDisposed() <em>Disposed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisposed()
	 * @generated
	 * @ordered
	 */
	protected boolean disposed = DISPOSED_EDEFAULT;

	/**
	 * The default value of the '{@link #getOrb() <em>Orb</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @see #getOrb()
	 * @generated
	 * @ordered
	 */
	protected static final ORB ORB_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOrb() <em>Orb</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @see #getOrb()
	 * @generated
	 * @ordered
	 */
	protected ORB orb = ORB_EDEFAULT;

	/**
	 * The default value of the '{@link #getPoa() <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @see #getPoa()
	 * @generated
	 * @ordered
	 */
	protected static final POA POA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPoa() <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @see #getPoa()
	 * @generated
	 * @ordered
	 */
	protected POA poa = POA_EDEFAULT;

	/**
	 * The cached value of the '{@link #getWaveforms() <em>Waveforms</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaveforms()
	 * @generated
	 * @ordered
	 */
	protected EList<ScaWaveform> waveforms;

	/**
	 * The cached value of the '{@link #getSandboxWaveform() <em>Sandbox Waveform</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSandboxWaveform()
	 * @generated
	 * @ordered
	 */
	protected LocalScaWaveform sandboxWaveform;

	/**
	 * The cached value of the '{@link #getSandboxDeviceManager() <em>Sandbox Device Manager</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSandboxDeviceManager()
	 * @generated
	 * @ordered
	 */
	protected LocalScaDeviceManager sandboxDeviceManager;

	/**
	 * The cached value of the '{@link #getRootContext() <em>Root Context</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRootContext()
	 * @generated
	 * @ordered
	 */
	protected NotifyingNamingContext rootContext;

	/**
	 * The cached value of the '{@link #getFileManager() <em>File Manager</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileManager()
	 * @generated
	 * @ordered
	 */
	protected LocalFileManager fileManager;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected LocalScaImpl() {
              // END GENERATED CODE
              super();
              eAdapters().add(new DisposableObjectContainerListener());
              // BEGIN GENERATED CODE
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ScaDebugPackage.Literals.LOCAL_SCA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDisposed() {
		return disposed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ORB getOrb() {
		return orb;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOrb(ORB newOrb) {
		ORB oldOrb = orb;
		orb = newOrb;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__ORB, oldOrb, orb));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public POA getPoa() {
		return poa;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPoa(POA newPoa) {
		POA oldPoa = poa;
		poa = newPoa;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__POA, oldPoa, poa));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ScaWaveform> getWaveforms() {
		if (waveforms == null) {
			waveforms = new EObjectContainmentEList<ScaWaveform>(ScaWaveform.class, this, ScaDebugPackage.LOCAL_SCA__WAVEFORMS);
		}
		return waveforms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaWaveform getSandboxWaveform() {
		return sandboxWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSandboxWaveform(LocalScaWaveform newSandboxWaveform, NotificationChain msgs) {
		LocalScaWaveform oldSandboxWaveform = sandboxWaveform;
		sandboxWaveform = newSandboxWaveform;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, oldSandboxWaveform, newSandboxWaveform);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSandboxWaveform(LocalScaWaveform newSandboxWaveform) {
		if (newSandboxWaveform != sandboxWaveform) {
			NotificationChain msgs = null;
			if (sandboxWaveform != null)
				msgs = ((InternalEObject)sandboxWaveform).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, null, msgs);
			if (newSandboxWaveform != null)
				msgs = ((InternalEObject)newSandboxWaveform).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, null, msgs);
			msgs = basicSetSandboxWaveform(newSandboxWaveform, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, newSandboxWaveform, newSandboxWaveform));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaDeviceManager getSandboxDeviceManager() {
		return sandboxDeviceManager;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSandboxDeviceManager(LocalScaDeviceManager newSandboxDeviceManager, NotificationChain msgs) {
		LocalScaDeviceManager oldSandboxDeviceManager = sandboxDeviceManager;
		sandboxDeviceManager = newSandboxDeviceManager;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER, oldSandboxDeviceManager, newSandboxDeviceManager);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSandboxDeviceManager(LocalScaDeviceManager newSandboxDeviceManager) {
		if (newSandboxDeviceManager != sandboxDeviceManager) {
			NotificationChain msgs = null;
			if (sandboxDeviceManager != null)
				msgs = ((InternalEObject)sandboxDeviceManager).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER, null, msgs);
			if (newSandboxDeviceManager != null)
				msgs = ((InternalEObject)newSandboxDeviceManager).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER, null, msgs);
			msgs = basicSetSandboxDeviceManager(newSandboxDeviceManager, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER, newSandboxDeviceManager, newSandboxDeviceManager));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContext getRootContext() {
		return rootContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRootContext(NotifyingNamingContext newRootContext, NotificationChain msgs) {
		NotifyingNamingContext oldRootContext = rootContext;
		rootContext = newRootContext;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, oldRootContext, newRootContext);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRootContext(NotifyingNamingContext newRootContext) {
		if (newRootContext != rootContext) {
			NotificationChain msgs = null;
			if (rootContext != null)
				msgs = ((InternalEObject)rootContext).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, null, msgs);
			if (newRootContext != null)
				msgs = ((InternalEObject)newRootContext).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, null, msgs);
			msgs = basicSetRootContext(newRootContext, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, newRootContext, newRootContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalFileManager getFileManager() {
		return fileManager;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFileManager(LocalFileManager newFileManager, NotificationChain msgs) {
		LocalFileManager oldFileManager = fileManager;
		fileManager = newFileManager;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, oldFileManager, newFileManager);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFileManager(LocalFileManager newFileManager) {
		if (newFileManager != fileManager) {
			NotificationChain msgs = null;
			if (fileManager != null)
				msgs = ((InternalEObject)fileManager).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, null, msgs);
			if (newFileManager != null)
				msgs = ((InternalEObject)newFileManager).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, null, msgs);
			msgs = basicSetFileManager(newFileManager, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, newFileManager, newFileManager));
	}

	private static final Debug DEBUG = new Debug(ScaDebugPlugin.getInstance(), "localSca");

	private final NotifyingNamingContextAdapter adapter = new NotifyingNamingContextAdapter() {

		@Override
		protected void addObject(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
			addResource(location, obj, msg);

		}

		@Override
		protected void removeObject(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
			removeResource(location, obj, msg);
		}

	};
	
	private CheckupJob checkupJob = new CheckupJob(this);

	private final ILaunchesListener2 launchListener = new ILaunchesListener2() {
		
		public void launchesRemoved(final ILaunch[] launches) {
			
		}
		
		public void launchesChanged(final ILaunch[] launches) {
			
		}
		
		public void launchesAdded(final ILaunch[] launches) {
			
		}
		
		public void launchesTerminated(final ILaunch[] launches) {
			for (final ILaunch launch : launches) {
				ScaModelCommand.execute(LocalScaImpl.this, new ScaModelCommand() {
					
					public void execute() {
						final TreeIterator<Object> iterator = EcoreUtil.getAllContents(LocalScaImpl.this, false);
						while(iterator.hasNext()) {
							final Object obj = iterator.next();
							if (obj instanceof LocalSca) {
								continue;
							} else if (obj instanceof LocalLaunch) {
								final LocalLaunch scaLaunch = (LocalLaunch) obj;
								if (scaLaunch.getLaunch() == launch) {
									EcoreUtil.delete(scaLaunch);
									return;
								}
							} else {
								iterator.prune();
								continue;
							}
						}
					}
				});
			}
		}
	};

	private static LocalFileManager createFileManager(final POA poa) throws CoreException {
		final LocalFileManager tmp = ScaDebugFactory.eINSTANCE.createLocalFileManager();
		final IFileManager fm = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry().getFileManager();
        FileManager ref;
        try {
	        ref = FileManagerHelper.narrow(poa.servant_to_reference(new FileManagerPOATie(fm)));
        } catch (final ServantNotActive e) {
	        throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create file manager"));
        } catch (final WrongPolicy e) {
        	throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create file manager"));
        }
        tmp.setCorbaObj(ref);
        return tmp;
    }

	private static NotifyingNamingContext createRootContext(final POA poa) {
	    final NotifyingNamingContext context = ScaDebugFactory.eINSTANCE.createNotifyingNamingContext();
	    context.setPoa(poa);
	    return context;
    }

	private static POA createPOA(final ORB orb) throws CoreException {
        try {
	        final POA retVal = (POA) orb.resolve_initial_references("RootPOA");
	        retVal.the_POAManager().activate();
	        return retVal;
        } catch (final InvalidName e) {
	        throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to find root POA reference"));
        } catch (final AdapterInactive e) {
        	throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to activate root POA reference"));
        }
        	
    }

	private static ORB createOrb() {
		return ORBUtil.init(null);
    }

	private static LocalScaDeviceManager createSandboxDeviceManager(final ResourceSet resourceSet, final FileManager fm, final POA poa, final NotifyingNamingContext context) throws CoreException {
		final LocalScaDeviceManager tmp = ScaDebugFactory.eINSTANCE.createLocalScaDeviceManager();
		final DeviceConfiguration dcd = LocalScaImpl.createSandboxDeviceConfiguration(resourceSet);
		final URI uri = dcd.eResource().getURI();
		
		tmp.setDataProvidersEnabled(false);
		tmp.setProfileURI(uri);
		tmp.setProfile(uri.path());
		tmp.setProfileObj(dcd);
		tmp.setNamingContext(context.getResourceContext(dcd.eResource().getURI()));
		final DeviceManagerImpl impl = new DeviceManagerImpl(uri.path(), DceUuidUtil.createDceUUID(), "Device Manager", tmp, fm);
        try {
        	tmp.setLocalDeviceManager(impl, poa);
        } catch (final ServantNotActive e) {
        	throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create device manager"));
        } catch (final WrongPolicy e) {
        	throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create device manager"));
        }
		return tmp;
    }

	private static DeviceConfiguration createSandboxDeviceConfiguration(final ResourceSet resourceSet) {
		final URI uri = URI.createURI("mem:///sandboxDeviceManager.dcd.xml");
		final Resource resource = resourceSet.createResource(uri);
		final DeviceConfiguration dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
		final DcdDocumentRoot root = DcdFactory.eINSTANCE.createDcdDocumentRoot();
		root.setDeviceconfiguration(dcd);
		resource.getContents().add(root);
	    return dcd;
    }

	private static LocalScaWaveform createSandboxWaveform(final ResourceSet resourceSet, final POA poa, final NotifyingNamingContext context) throws CoreException {
		final String name = "Chalkboard";
		final SoftwareAssembly sad = LocalScaImpl.createSandboxSoftwareAssembly(resourceSet);
		
		final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
		waveform.setDataProvidersEnabled(false);
		waveform.setProfile(sad.eResource().getURI().path());
		waveform.setProfileURI(sad.eResource().getURI());
		waveform.setProfileObj(sad);
		waveform.setNamingContext(context.getResourceContext(sad.eResource().getURI()));

		final ApplicationImpl app = new ApplicationImpl(waveform, name, name);
		try {
	        waveform.setLocalApp(app, poa);
        } catch (final ServantNotActive e) {
	        throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create local waveform.",e));
        } catch (final WrongPolicy e) {
        	throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create local waveform.",e));
        }
		return waveform;
    }

	private static SoftwareAssembly createSandboxSoftwareAssembly(final ResourceSet resourceSet) {
		final URI sadUri = URI.createURI("mem://sandbox.sad.xml");
		
		final SoftwareAssembly sad = SadFactory.eINSTANCE.createSoftwareAssembly();
		final SadDocumentRoot root = SadFactory.eINSTANCE.createSadDocumentRoot();
		root.setSoftwareassembly(sad);
		
		if (resourceSet != null) {
			final Resource resource = resourceSet.createResource(sadUri);
			resource.getContents().add(root);
		}
		return sad;
    }

	private ResourceSet getResourceSet() {
		if (eResource() == null) {
			return null;
		}
	    return eResource().getResourceSet();
    }

	protected void addResource(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
		// END GENERATED CODE
		final Job addResourceJob = new SilentJob("Add Resource") {

			@Override
			protected IStatus runSilent(final IProgressMonitor monitor) {
				try {
					if (obj._is_a(ApplicationHelper.id())) {
						addApplication(new Name(location), ApplicationHelper.narrow(obj));
					}
				} catch (final SystemException e) {
					// PASS
				} catch (final org.omg.CosNaming.NamingContextPackage.InvalidName e) {
					// PASS
				}
				return Status.OK_STATUS;
			}
		};
		addResourceJob.setSystem(true);
		addResourceJob.schedule();
		// BEGIN GENERATED CODE
	}
	

	protected void removeResource(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
		// END GENERATED CODE
		final Job addResourceJob = new SilentJob("Remove Resource") {

			@Override
			protected IStatus runSilent(final IProgressMonitor monitor) {
				try {
					if (obj._is_a(ApplicationHelper.id())) {
						removeApplication(new Name(location), ApplicationHelper.narrow(obj));
					}
				} catch (final SystemException e) {
					// PASS
				} catch (final org.omg.CosNaming.NamingContextPackage.InvalidName e) {
					// PASS
				}
				return Status.OK_STATUS;
			}
		};
		addResourceJob.setSystem(true);
		addResourceJob.schedule();
		// BEGIN GENERATED CODE
	    
    }
	
	private void removeApplication(final Name key, final Application corbaObject) {
        // TODO Auto-generated method stub
        
    }

	protected void addApplication(final Name key, final Application app) {
		// END GENERATED CODE
		final String id = app.identifier();
		for (final ScaWaveform waveform : getWaveforms()) {
			if (waveform.getIdentifier().equals(id)) {
				return;
			}
		}

		final String profilePath = app.profile();
		final URI uri = this.rootContext.getURI(key.components());
		final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
		final NotifyingNamingContext waveformContext = this.rootContext.getResourceContext(uri);
		waveform.setNamingContext(waveformContext);
		waveform.setDataProvidersEnabled(false);
		waveform.setCorbaObj(app);
		waveform.setProfile(profilePath);
		waveform.setProfileURI(uri);

		try {
			waveform.initialize();
		} catch (final InitializeError e) {
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					waveform.setStatus(ScaPackage.Literals.SCA_WAVEFORM__DOM_MGR, new Status(IStatus.ERROR,
					        ScaDebugPlugin.ID,
					        "Component failed to initialize",
					        e));
				}
			});
		} catch (final SystemException e) {
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					waveform.setStatus(ScaPackage.Literals.SCA_WAVEFORM__DOM_MGR, new Status(IStatus.ERROR,
					        ScaDebugPlugin.ID,
					        "Component failed to initialize",
					        e));
				}
			});
		}

		ScaModelCommand.execute(this, new ScaModelCommand() {

			public void execute() {
				getWaveforms().add(waveform);
			}
		});

		try {
			waveform.refresh(null, RefreshDepth.SELF);
		} catch (final InterruptedException e) {
			// PASS
		}

		// BEGIN GENERATED CODE
    }

	

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void init() throws CoreException {
		// END GENERATED CODE
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this.launchListener);
		setOrb(LocalScaImpl.createOrb());
		setPoa(LocalScaImpl.createPOA(getOrb()));
		setRootContext(LocalScaImpl.createRootContext(getPoa()));
		setFileManager(LocalScaImpl.createFileManager(getPoa()));
		setSandboxWaveform(LocalScaImpl.createSandboxWaveform(getResourceSet(), getPoa(), getRootContext()));
		setSandboxDeviceManager(LocalScaImpl.createSandboxDeviceManager(getResourceSet(), getFileManager().getObj(), getPoa(), getRootContext()));
		this.checkupJob.schedule();
		eAdapters().add(this.adapter);
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dispose() {
		// END GENERATED CODE
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this.launchListener);
		ScaModelCommand.execute(this, new ScaModelCommand() {

			public void execute() {
				getWaveforms().clear();
				setSandboxWaveform(null);
				setSandboxDeviceManager(null);
				if (getRootContext() != null) {
					getRootContext().dispose();
				}
				setRootContext(null);
			}
		});
		if (this.checkupJob != null) {
			this.checkupJob.cancel();
			this.checkupJob = null;
		}
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
			case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
				return ((InternalEList<?>)getWaveforms()).basicRemove(otherEnd, msgs);
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
				return basicSetSandboxWaveform(null, msgs);
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
				return basicSetSandboxDeviceManager(null, msgs);
			case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
				return basicSetRootContext(null, msgs);
			case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
				return basicSetFileManager(null, msgs);
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
			case ScaDebugPackage.LOCAL_SCA__DISPOSED:
				return isDisposed();
			case ScaDebugPackage.LOCAL_SCA__ORB:
				return getOrb();
			case ScaDebugPackage.LOCAL_SCA__POA:
				return getPoa();
			case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
				return getWaveforms();
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
				return getSandboxWaveform();
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
				return getSandboxDeviceManager();
			case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
				return getRootContext();
			case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
				return getFileManager();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ScaDebugPackage.LOCAL_SCA__ORB:
				setOrb((ORB)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__POA:
				setPoa((POA)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
				getWaveforms().clear();
				getWaveforms().addAll((Collection<? extends ScaWaveform>)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
				setSandboxWaveform((LocalScaWaveform)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
				setSandboxDeviceManager((LocalScaDeviceManager)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
				setRootContext((NotifyingNamingContext)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
				setFileManager((LocalFileManager)newValue);
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
			case ScaDebugPackage.LOCAL_SCA__ORB:
				setOrb(ORB_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA__POA:
				setPoa(POA_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
				getWaveforms().clear();
				return;
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
				setSandboxWaveform((LocalScaWaveform)null);
				return;
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
				setSandboxDeviceManager((LocalScaDeviceManager)null);
				return;
			case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
				setRootContext((NotifyingNamingContext)null);
				return;
			case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
				setFileManager((LocalFileManager)null);
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
			case ScaDebugPackage.LOCAL_SCA__DISPOSED:
				return disposed != DISPOSED_EDEFAULT;
			case ScaDebugPackage.LOCAL_SCA__ORB:
				return ORB_EDEFAULT == null ? orb != null : !ORB_EDEFAULT.equals(orb);
			case ScaDebugPackage.LOCAL_SCA__POA:
				return POA_EDEFAULT == null ? poa != null : !POA_EDEFAULT.equals(poa);
			case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
				return waveforms != null && !waveforms.isEmpty();
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
				return sandboxWaveform != null;
			case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
				return sandboxDeviceManager != null;
			case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
				return rootContext != null;
			case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
				return fileManager != null;
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
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (disposed: ");
		result.append(disposed);
		result.append(", orb: ");
		result.append(orb);
		result.append(", poa: ");
		result.append(poa);
		result.append(')');
		return result.toString();
	}

} //LocalScaImpl
