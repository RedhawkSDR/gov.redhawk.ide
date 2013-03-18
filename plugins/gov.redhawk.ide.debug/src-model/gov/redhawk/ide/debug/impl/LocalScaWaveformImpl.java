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

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.commands.LocalScaWaveformMergeComponentsCommand;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.impl.ScaWaveformImpl;
import gov.redhawk.sca.util.SilentJob;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.jacorb.naming.Name;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.ApplicationHelper;
import CF.ComponentType;
import CF.DataType;
import CF.Resource;
import CF.ResourceHelper;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import ExtendedCF.ApplicationExt;
import ExtendedCF.ApplicationExtHelper;
import ExtendedCF.ApplicationExtOperations;
import ExtendedCF.ApplicationExtPOATie;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca Waveform</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getLocalApp <em>Local App</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LocalScaWaveformImpl extends ScaWaveformImpl implements LocalScaWaveform {
	/**
	 * The default value of the '{@link #getLaunch() <em>Launch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLaunch()
	 * @generated
	 * @ordered
	 */
	protected static final ILaunch LAUNCH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLaunch() <em>Launch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLaunch()
	 * @generated
	 * @ordered
	 */
	protected ILaunch launch = LAUNCH_EDEFAULT;

	/**
	 * The default value of the '{@link #getMode() <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMode()
	 * @generated
	 * @ordered
	 */
	protected static final String MODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMode() <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMode()
	 * @generated
	 * @ordered
	 */
	protected String mode = MODE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getNamingContext() <em>Naming Context</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamingContext()
	 * @generated
	 * @ordered
	 */
	protected NotifyingNamingContext namingContext;

	/**
	 * The cached value of the '{@link #getLocalApp() <em>Local App</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalApp()
	 * @generated
	 * @ordered
	 */
	protected ApplicationExtOperations localApp;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaWaveformImpl() {
		super();
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ScaDebugPackage.Literals.LOCAL_SCA_WAVEFORM;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ILaunch getLaunch() {
		return launch;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLaunch(ILaunch newLaunch) {
		ILaunch oldLaunch = launch;
		launch = newLaunch;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH, oldLaunch, launch));
	}

	private final NotifyingNamingContextAdapter adapter = new NotifyingNamingContextAdapter() {
		
		@Override
		protected void removeObject(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
			removeResource(location, obj, msg);
		}
		
		@Override
		protected void addObject(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
			addResource(location, obj, msg);
		}
	};
	
	private final SilentJob refreshJob = new SilentJob("Refresh") {
		{
			setSystem(true);
			setPriority(Job.SHORT);
		}

		@Override
        protected IStatus runSilent(final IProgressMonitor monitor) {
			fetchIdentifier(null);
			try {
				refresh(monitor, RefreshDepth.FULL);
			} catch (final InterruptedException e) {
				// PASS
			}
			return Status.OK_STATUS;
        }

	};

	protected void addResource(final NameComponent[] location, final org.omg.CORBA.Object obj, final Notification msg) {
		// END GENERATED CODE
		final Job addResourceJob = new SilentJob("Add Resource") {

			@Override
			protected IStatus runSilent(final IProgressMonitor monitor) {
				try {
					if (obj._is_a(ApplicationHelper.id())) {
						// PASS
					} else if (obj._is_a(ResourceHelper.id())) {
						addComponent(new Name(location), ResourceHelper.narrow(obj));
					}
				} catch (final SystemException e) {
					// PASS
				} catch (final InvalidName e) {
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
		final Job addResourceJob = new SilentJob("Add Resource") {

			@Override
			protected IStatus runSilent(final IProgressMonitor monitor) {
				try {
					if (obj._is_a(ApplicationHelper.id())) {
						removeComponent(new Name(location), ApplicationHelper.narrow(obj));
					}
				} catch (final SystemException e) {
					// PASS
				} catch (final InvalidName e) {
					// PASS
				}
				return Status.OK_STATUS;
			}
		};
		addResourceJob.setSystem(true);
		addResourceJob.schedule();
		// BEGIN GENERATED CODE
	    
    }


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getMode() {
		// END GENERATED CODE
		if (this.launch != null) {
			return getLaunch().getLaunchMode();
		}
		return null;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMode(String newMode) {
		String oldMode = mode;
		mode = newMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE, oldMode, mode));
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContext getNamingContext() {
		if (namingContext != null && namingContext.eIsProxy()) {
			InternalEObject oldNamingContext = (InternalEObject)namingContext;
			namingContext = (NotifyingNamingContext)eResolveProxy(oldNamingContext);
			if (namingContext != oldNamingContext) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT, oldNamingContext, namingContext));
			}
		}
		return namingContext;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContext basicGetNamingContext() {
		return namingContext;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setNamingContext(final NotifyingNamingContext newNamingContext) {
		if (this.namingContext != null) {
			this.namingContext.eAdapters().remove(this.adapter);
		}
		setNamingContextGen(newNamingContext);
		if (this.namingContext != null) {
			this.namingContext.eAdapters().add(this.adapter);
		} 
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNamingContextGen(NotifyingNamingContext newNamingContext) {
		NotifyingNamingContext oldNamingContext = namingContext;
		namingContext = newNamingContext;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT, oldNamingContext, namingContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ApplicationExtOperations getLocalApp() {
		return localApp;
	}


	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLocalApp(ApplicationExtOperations newLocalApp, NotificationChain msgs) {
		ApplicationExtOperations oldLocalApp = localApp;
		localApp = newLocalApp;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP, oldLocalApp, newLocalApp);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}


	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocalApp(ApplicationExtOperations newLocalApp) {
		if (newLocalApp != localApp) {
			NotificationChain msgs = null;
			if (localApp != null)
				msgs = ((InternalEObject)localApp).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP, null, msgs);
			if (newLocalApp != null)
				msgs = ((InternalEObject)newLocalApp).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP, null, msgs);
			msgs = basicSetLocalApp(newLocalApp, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP, newLocalApp, newLocalApp));
	}


	/**
	 * @since 2.0
	 */
	public void setLocalApp(final ApplicationExtOperations newLocalApp, final POA poa) throws ServantNotActive, WrongPolicy {
		setLocalApp(newLocalApp);
		final ApplicationExt ref;
		if (poa == null) {
			ref = null;
		} else {
			ref = ApplicationExtHelper.narrow(poa.servant_to_reference(new ApplicationExtPOATie(newLocalApp)));
		}
        
	        
	        // We cache the old values since these have probably been set for a local waveform
	    final String profile = getProfile();
	    final boolean profileSet = isSetProfile();
	    final URI profileURI = getProfileURI();
	    final boolean profileURISet = isSetProfileURI();
	    final SoftwareAssembly profileObj = getProfileObj();
	    final boolean profileObjSet = isSetProfileObj();
	    
	    setCorbaObj(ref);
	    setObj(ref);
	    
	    if (profileSet) {
	    	setProfile(profile);
	    }
	    if (profileURISet) {
	    	setProfileURI(profileURI);
	    }
	    if (profileObjSet) {
	    	setProfileObj(profileObj);
	    }
	    this.refreshJob.schedule();
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Resource launch(String id, DataType[] execParams, String spdURI, String implID, String mode) throws ExecuteFail {
		return getLocalApp().launch(id, execParams, spdURI, implID, mode);
	}


	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Resource reset(String compInstId) throws ReleaseError, ExecuteFail {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH:
				return getLaunch();
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
				return getMode();
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT:
				if (resolve) return getNamingContext();
				return basicGetNamingContext();
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
				return getLocalApp();
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
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH:
				setLaunch((ILaunch)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
				setMode((String)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT:
				setNamingContext((NotifyingNamingContext)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
				setLocalApp((ApplicationExtOperations)newValue);
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
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH:
				setLaunch(LAUNCH_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
				setMode(MODE_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT:
				setNamingContext((NotifyingNamingContext)null);
				return;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
				setLocalApp((ApplicationExtOperations)null);
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
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH:
				return LAUNCH_EDEFAULT == null ? launch != null : !LAUNCH_EDEFAULT.equals(launch);
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
				return MODE_EDEFAULT == null ? mode != null : !MODE_EDEFAULT.equals(mode);
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT:
				return namingContext != null;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
				return localApp != null;
		}
		return super.eIsSet(featureID);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (derivedFeatureID) {
				case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH: return ScaDebugPackage.LOCAL_LAUNCH__LAUNCH;
				case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE: return ScaDebugPackage.LOCAL_LAUNCH__MODE;
				default: return -1;
			}
		}
		if (baseClass == ApplicationExtOperations.class) {
			switch (derivedFeatureID) {
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (baseFeatureID) {
				case ScaDebugPackage.LOCAL_LAUNCH__LAUNCH: return ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH;
				case ScaDebugPackage.LOCAL_LAUNCH__MODE: return ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE;
				default: return -1;
			}
		}
		if (baseClass == ApplicationExtOperations.class) {
			switch (baseFeatureID) {
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(" (launch: ");
		result.append(launch);
		result.append(", mode: ");
		result.append(mode);
		result.append(')');
		return result.toString();
	}


	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public LocalScaComponent launch(final String usageName, final DataType[] execParams, final URI spdURI, final String implID, final String mode) throws CoreException {
		// END GENERATED CODE
		Assert.isNotNull(spdURI);
		Assert.isNotNull(implID);
		// TODO Fix this hack
		return ((ApplicationImpl) getLocalApp()).launch(usageName, execParams, spdURI, implID, mode);
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
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
				return basicSetLocalApp(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}


	@Override
    @Deprecated
	protected Command createMergeComponentsCommand(final String assemCtrlId, final ComponentType[] compTypes, final IStatus status) {
		return new LocalScaWaveformMergeComponentsCommand(this, compTypes, status);
	}
	
	@Override
	protected Command createMergeComponentsCommand(final ComponentType[] compTypes, final IStatus status) {
	    return new LocalScaWaveformMergeComponentsCommand(this, compTypes, status);
	}

	private void addComponent(final Name name, final Resource resource) {
		// END GENERATED CODE
		final LocalScaComponent component = ScaDebugFactory.eINSTANCE.createLocalScaComponent();
		component.setDataProvidersEnabled(false);
		component.setCorbaObj(resource);
		String compName = null;
		final NameComponent[] nameComponents = name.components();
		if (nameComponents.length > 0) {
			final NameComponent lastSegment = nameComponents[nameComponents.length - 1];
			compName = lastSegment.id;
		}
		if (compName != null) {
			component.setName(compName);
		}
		final URI uri = this.namingContext.getURI(name.components());
		if (uri == null) {
			return;
		}
		component.setProfileURI(URI.createURI(uri.toString()));

		try {
			component.initialize();
			try {
				component.refresh(null, RefreshDepth.FULL);
			} catch (final InterruptedException e1) {
				// PASS
			}
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					// TODO Find / create component Instantiation and assign
//					DomComponentFile cf = PartitioningFactory.eINSTANCE.createDomComponentFile();
//					cf.setSoftPkg(component.getProfileObj());
//					if (getProfileObj().getComponentFiles() == null) {
//						getProfileObj().setComponentFiles(PartitioningFactory.eINSTANCE.createComponentFiles());
//					}
//					getProfileObj().getComponentFiles().getComponentFile().add(cf);
//					
//					SadComponentInstantiation inst = SadFactory.eINSTANCE.createSadComponentInstantiation();
//					inst.setId(component.getInstantiationIdentifier());
//					inst.setUsageName(component.getName());
//					inst.setStartOrder(BigInteger.valueOf(getProfileObj().getComponentFiles().getComponentFile().size()));
//					
//					SadComponentPlacement cp = SadFactory.eINSTANCE.createSadComponentPlacement();
//					ComponentFileRef ref = PartitioningFactory.eINSTANCE.createComponentFileRef();
//					ref.setFile(cf);
//					
//					cp.setComponentFileRef(ref);
//					cp.getComponentInstantiation().add(inst);
//					if (getProfileObj().getPartitioning() == null) {
//						getProfileObj().setPartitioning(SadFactory.eINSTANCE.createSadPartitioning());
//					}
//					getProfileObj().getPartitioning().getComponentPlacement().add(cp);
//					
//					component.setComponentInstantiation(inst);
					getComponents().add(component);
				}
			});
			try {
				component.refresh(null, RefreshDepth.FULL);
			} catch (final InterruptedException e) {
				// PASS
			}
		} catch (final InitializeError e) {
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					component.setStatus(ScaPackage.Literals.SCA_COMPONENT__COMPONENT_INSTANTIATION, new Status(IStatus.ERROR,
					        ScaDebugPlugin.ID,
					        "Component failed to initialize",
					        e));
				}
			});
		} catch (final SystemException e) {
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					component.setStatus(ScaPackage.Literals.SCA_COMPONENT__COMPONENT_INSTANTIATION, new Status(IStatus.ERROR,
					        ScaDebugPlugin.ID,
					        "Component failed to initialize",
					        e));
				}
			});
		}
		// BEGIN GENERATED CODE
	}
	

	private void removeComponent(final Name name, final Resource resource) {
	    // TODO Auto-generated method stub
	    
    }
	
	@Override
	public void releaseObject() throws ReleaseError {
		if (this == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
			ScaModelCommand.execute(this, new ScaModelCommand() {
				
				public void execute() {
					getComponents().clear();
				}
			});
		} else {
		    super.releaseObject();
		}
	}
	
	@Override
	public void dispose() {
		Job releaseJob = new SilentJob("Local Waveform Release") {
			
			@Override
			protected IStatus runSilent(IProgressMonitor monitor) {
		        try {
	                releaseObject();
                } catch (ReleaseError e) {
	                return new Status(Status.ERROR, ScaDebugPlugin.ID, "Failed to release local waveform: " + getName(), e);
                }
                return Status.OK_STATUS;
            }
			
		};
		releaseJob.setSystem(true);
		releaseJob.setUser(false);
		releaseJob.schedule();
	    super.dispose();
	}

} //LocalScaWaveformImpl
