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
package gov.redhawk.ide.debug.impl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.jdt.annotation.NonNull;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.Application;
import CF.ApplicationHelper;
import CF.ApplicationOperations;
import CF.ApplicationPOATie;
import CF.ComponentType;
import CF.DataType;
import CF.LifeCyclePackage.ReleaseError;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.commands.LocalScaWaveformMergeComponentsCommand;
import gov.redhawk.ide.debug.impl.commands.ProxyScaWaveformMergeComponentsCommand;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.impl.ScaWaveformImpl;
import gov.redhawk.sca.util.OrbSession;
import gov.redhawk.sca.util.SilentJob;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca Waveform</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getLocalApp <em>Local App</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl#getDomainWaveform <em>Domain Waveform</em>}</li>
 * </ul>
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
	 * The default value of the '{@link #getLocalApp() <em>Local App</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalApp()
	 * @generated
	 * @ordered
	 */
	protected static final ApplicationOperations LOCAL_APP_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocalApp() <em>Local App</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalApp()
	 * @generated
	 * @ordered
	 */
	protected ApplicationOperations localApp = LOCAL_APP_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDomainWaveform() <em>Domain Waveform</em>}' reference.
	 * <!-- begin-user-doc -->
	 * @since 9.0
	 * <!-- end-user-doc -->
	 * @see #getDomainWaveform()
	 * @generated
	 * @ordered
	 */
	protected ScaWaveform domainWaveform;

	private OrbSession session = OrbSession.createSession();

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
	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLaunch(ILaunch newLaunch) {
		ILaunch oldLaunch = launch;
		launch = newLaunch;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH, oldLaunch, launch));
	}

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

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
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
	@Override
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
	@Override
	public NotifyingNamingContext getNamingContext() {
		if (namingContext != null && namingContext.eIsProxy()) {
			InternalEObject oldNamingContext = (InternalEObject) namingContext;
			namingContext = (NotifyingNamingContext) eResolveProxy(oldNamingContext);
			if (namingContext != oldNamingContext) {
				if (eNotificationRequired())
					eNotify(
						new ENotificationImpl(this, Notification.RESOLVE, ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT, oldNamingContext, namingContext));
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
	 * @generated
	 */
	@Override
	public void setNamingContext(NotifyingNamingContext newNamingContext) {
		NotifyingNamingContext oldNamingContext = namingContext;
		namingContext = newNamingContext;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT, oldNamingContext, namingContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ApplicationOperations getLocalApp() {
		return localApp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocalAppGen(ApplicationOperations newLocalApp) {
		ApplicationOperations oldLocalApp = localApp;
		localApp = newLocalApp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP, oldLocalApp, localApp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void setLocalApp(final ApplicationOperations newLocalApp) {
		setLocalAppGen(newLocalApp);
		Application ref = null;

		if (newLocalApp != null) {
			try {
				ref = ApplicationHelper.narrow(session.getPOA().servant_to_reference(new ApplicationPOATie(newLocalApp)));
			} catch (ServantNotActive e) {
				ScaDebugPlugin.logError("Failed to setup waveform application servant.", e);
			} catch (WrongPolicy e) {
				ScaDebugPlugin.logError("Failed to setup waveform application servant.", e);
			} catch (CoreException e) {
				ScaDebugPlugin.logError("Failed to setup waveform application servant.", e);
			}
		}

		setCorbaObj(ref);
		setObj(ref);
		if (ref != null && newLocalApp != null) {
			setIdentifier(newLocalApp.identifier());
			setName(newLocalApp.name());
			this.refreshJob.schedule();
		} else {
			super.unsetProfileObj();
			super.unsetProfileURI();
			super.unsetProfile();
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 9.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaWaveform getDomainWaveform() {
		if (domainWaveform != null && domainWaveform.eIsProxy()) {
			InternalEObject oldDomainWaveform = (InternalEObject) domainWaveform;
			domainWaveform = (ScaWaveform) eResolveProxy(oldDomainWaveform);
			if (domainWaveform != oldDomainWaveform) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM, oldDomainWaveform,
						domainWaveform));
			}
		}
		return domainWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 9.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaWaveform basicGetDomainWaveform() {
		return domainWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 9.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDomainWaveform(ScaWaveform newDomainWaveform) {
		ScaWaveform oldDomainWaveform = domainWaveform;
		domainWaveform = newDomainWaveform;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM, oldDomainWaveform, domainWaveform));
	}

	@Override
	public void unsetProfile() {
	}

	@Override
	public void unsetProfileURI() {
	}

	@Override
	public void unsetProfileObj() {
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
			if (resolve)
				return getNamingContext();
			return basicGetNamingContext();
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
			return getLocalApp();
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM:
			if (resolve)
				return getDomainWaveform();
			return basicGetDomainWaveform();
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
			setLaunch((ILaunch) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
			setMode((String) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__NAMING_CONTEXT:
			setNamingContext((NotifyingNamingContext) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
			setLocalApp((ApplicationOperations) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM:
			setDomainWaveform((ScaWaveform) newValue);
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
			setNamingContext((NotifyingNamingContext) null);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LOCAL_APP:
			setLocalApp(LOCAL_APP_EDEFAULT);
			return;
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM:
			setDomainWaveform((ScaWaveform) null);
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
			return LOCAL_APP_EDEFAULT == null ? localApp != null : !LOCAL_APP_EDEFAULT.equals(localApp);
		case ScaDebugPackage.LOCAL_SCA_WAVEFORM__DOMAIN_WAVEFORM:
			return domainWaveform != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class< ? > baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (derivedFeatureID) {
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH:
				return ScaDebugPackage.LOCAL_LAUNCH__LAUNCH;
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE:
				return ScaDebugPackage.LOCAL_LAUNCH__MODE;
			default:
				return -1;
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
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class< ? > baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (baseFeatureID) {
			case ScaDebugPackage.LOCAL_LAUNCH__LAUNCH:
				return ScaDebugPackage.LOCAL_SCA_WAVEFORM__LAUNCH;
			case ScaDebugPackage.LOCAL_LAUNCH__MODE:
				return ScaDebugPackage.LOCAL_SCA_WAVEFORM__MODE;
			default:
				return -1;
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (launch: ");
		result.append(launch);
		result.append(", mode: ");
		result.append(mode);
		result.append(", localApp: ");
		result.append(localApp);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	@NonNull
	public LocalScaComponent launch(final String compID, final DataType[] initConfiguration, final URI spdURI, final String implID, final String mode)
		throws CoreException {
		// END GENERATED CODE
		Assert.isNotNull(spdURI);
		Assert.isNotNull(implID);
		// TODO Fix this hack
		if (getLocalApp() instanceof ApplicationImpl) {
			return ((ApplicationImpl) getLocalApp()).launch(null, compID, initConfiguration, spdURI, implID, mode);
		} else if (getLocalApp() != null) {
			throw new IllegalStateException("Unknown Application type " + getLocalApp());
		} else {
			throw new IllegalStateException("Null Application type");
		}
		// BEGIN GENERATED CODE
	}

	@Override
	protected Command createMergeComponentsCommand(final ComponentType[] compTypes, final IStatus status) {
		// Is this a domain waveform that has been opened with the sandbox?
		if (getDomainWaveform() != null) {
			return new ProxyScaWaveformMergeComponentsCommand(this, compTypes, status);
		} else {
			return new LocalScaWaveformMergeComponentsCommand(this, compTypes, status);
		}
	}

	@Override
	public void releaseObject() throws ReleaseError {
		boolean isSandboxChalkboard = ScaDebugInstance.getLocalSandboxWaveformURI().equals(getProfileURI());
		if (!isSandboxChalkboard) {
			super.releaseObject();
			return;
		}

		// We'll call release, but it will only impact the components, not the application itself
		final Application waveform = fetchNarrowedObject(null);
		if (waveform != null) {
			waveform.releaseObject();
		}

		// Only remove our children from the model, not ourself
		ScaModelCommand.execute(this, new ScaModelCommand() {
			@Override
			public void execute() {
				getComponents().clear();
			}
		});
	}

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}

		// If we have a launch object (i.e. this IDE launched the object locally)
		if (getLaunch() != null) {
			// Call releaseObject() in a job. The dispose method may be called by UI / model threads, and thus cannot
			// block.
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
		}

		super.dispose();
		if (namingContext != null) {
			namingContext.dispose();
			namingContext = null;
		}
		if (session != null) {
			session.dispose();
			session = null;
		}
	}

} //LocalScaWaveformImpl
