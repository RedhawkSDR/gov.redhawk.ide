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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import ExtendedCF.Sandbox;
import ExtendedCF.SandboxHelper;
import ExtendedCF.SandboxOperations;
import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.impl.listeners.DisposableObjectContainerListener;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.impl.CorbaObjWrapperImpl;
import gov.redhawk.sca.util.OrbSession;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getWaveforms <em>Waveforms</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getSandboxWaveform <em>Sandbox Waveform</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getSandboxDeviceManager <em>Sandbox Device Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getRootContext <em>Root Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getFileManager <em>File Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaImpl#getSandbox <em>Sandbox</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LocalScaImpl extends CorbaObjWrapperImpl<Sandbox> implements LocalSca {
	/**
	 * The cached value of the '{@link #getWaveforms() <em>Waveforms</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaveforms()
	 * @generated
	 * @ordered
	 */
	protected EList<LocalScaWaveform> waveforms;

	/**
	 * The cached value of the '{@link #getSandboxWaveform() <em>Sandbox Waveform</em>}' reference.
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
	 * The default value of the '{@link #getSandbox() <em>Sandbox</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSandbox()
	 * @generated
	 * @ordered
	 */
	protected static final SandboxOperations SANDBOX_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSandbox() <em>Sandbox</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSandbox()
	 * @generated
	 * @ordered
	 */
	protected SandboxOperations sandbox = SANDBOX_EDEFAULT;

	private OrbSession session = OrbSession.createSession();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected LocalScaImpl() {
		// END GENERATED CODE
		super();
		eAdapters().add(new DisposableObjectContainerListener());
		DebugPlugin dp = DebugPlugin.getDefault();
		if (dp != null) {
			ILaunchManager lm = dp.getLaunchManager();
			if (lm != null) {
				lm.addLaunchListener(this.launchListener);
			}
		}
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
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * This is specialized for the more specific type known in this context.
	 * @generated
	 */
	@Override
	public void setObj(Sandbox newObj) {
		super.setObj(newObj);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<LocalScaWaveform> getWaveforms() {
		if (waveforms == null) {
			waveforms = new EObjectContainmentEList<LocalScaWaveform>(LocalScaWaveform.class, this, ScaDebugPackage.LOCAL_SCA__WAVEFORMS);
		}
		return waveforms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaWaveform getSandboxWaveform() {
		if (sandboxWaveform != null && sandboxWaveform.eIsProxy()) {
			InternalEObject oldSandboxWaveform = (InternalEObject) sandboxWaveform;
			sandboxWaveform = (LocalScaWaveform) eResolveProxy(oldSandboxWaveform);
			if (sandboxWaveform != oldSandboxWaveform) {
				if (eNotificationRequired())
					eNotify(
						new ENotificationImpl(this, Notification.RESOLVE, ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, oldSandboxWaveform, sandboxWaveform));
			}
		}
		return sandboxWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 6.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaWaveform basicGetSandboxWaveform() {
		return sandboxWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSandboxWaveform(LocalScaWaveform newSandboxWaveform) {
		LocalScaWaveform oldSandboxWaveform = sandboxWaveform;
		sandboxWaveform = newSandboxWaveform;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM, oldSandboxWaveform, sandboxWaveform));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER,
				oldSandboxDeviceManager, newSandboxDeviceManager);
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
	public void setSandboxDeviceManager(LocalScaDeviceManager newSandboxDeviceManager) {
		if (newSandboxDeviceManager != sandboxDeviceManager) {
			NotificationChain msgs = null;
			if (sandboxDeviceManager != null)
				msgs = ((InternalEObject) sandboxDeviceManager).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER,
					null, msgs);
			if (newSandboxDeviceManager != null)
				msgs = ((InternalEObject) newSandboxDeviceManager).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER,
					null, msgs);
			msgs = basicSetSandboxDeviceManager(newSandboxDeviceManager, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER, newSandboxDeviceManager,
				newSandboxDeviceManager));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, oldRootContext,
				newRootContext);
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
	public void setRootContext(NotifyingNamingContext newRootContext) {
		if (newRootContext != rootContext) {
			NotificationChain msgs = null;
			if (rootContext != null)
				msgs = ((InternalEObject) rootContext).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, null, msgs);
			if (newRootContext != null)
				msgs = ((InternalEObject) newRootContext).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, null, msgs);
			msgs = basicSetRootContext(newRootContext, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT, newRootContext, newRootContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, oldFileManager,
				newFileManager);
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
	public void setFileManager(LocalFileManager newFileManager) {
		if (newFileManager != fileManager) {
			NotificationChain msgs = null;
			if (fileManager != null)
				msgs = ((InternalEObject) fileManager).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, null, msgs);
			if (newFileManager != null)
				msgs = ((InternalEObject) newFileManager).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, null, msgs);
			msgs = basicSetFileManager(newFileManager, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__FILE_MANAGER, newFileManager, newFileManager));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SandboxOperations getSandbox() {
		return sandbox;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSandbox(SandboxOperations newSandbox) {
		SandboxOperations oldSandbox = sandbox;
		sandbox = newSandbox;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA__SANDBOX, oldSandbox, sandbox));
	}

	private final ILaunchesListener2 launchListener = new ILaunchesListener2() {

		@Override
		public void launchesRemoved(final ILaunch[] launches) {
		}

		@Override
		public void launchesChanged(final ILaunch[] launches) {
		}

		@Override
		public void launchesAdded(final ILaunch[] launches) {
		}

		@Override
		public void launchesTerminated(final ILaunch[] launches) {
			for (final ILaunch launch : launches) {
				ScaModelCommand.execute(LocalScaImpl.this, new ScaModelCommand() {

					@Override
					public void execute() {
						// Find a model object with a matching ILaunch
						final TreeIterator<Object> iterator = EcoreUtil.getAllContents(LocalScaImpl.this, false);
						while (iterator.hasNext()) {
							final Object obj = iterator.next();
							if (obj instanceof LocalLaunch) {
								// See if this is the model object we're looking for
								final LocalLaunch scaLaunch = (LocalLaunch) obj;
								if (scaLaunch.getLaunch() == launch) {
									EcoreUtil.delete(scaLaunch);
									return;
								}
							} else if (obj instanceof LocalSca) {
								// LocalSca is the root. We have to ignore it, but check its children
								continue;
							} else {
								// We're not interested in this object nor any of its children
								iterator.prune();
								continue;
							}
						}
					}
				});
			}
		}
	};

	/**
	 * @param sandboxRef 
	 * @param fileManagerRef 
	 * @param sandboxWaveformRef 
	 * @param sandboxDeviceManagerRef 
	 * @param newRootContext 
	 * @generated NOT
	 * @since 4.0
	 */
	public void init(Sandbox sandboxRef, LocalFileManager fileManagerRef, LocalScaWaveform sandboxWaveformRef, LocalScaDeviceManager sandboxDeviceManagerRef,
		NotifyingNamingContext newRootContext) {
		// END GENERATED CODE
		setObj(sandboxRef);
		setRootContext(newRootContext);
		setFileManager(fileManagerRef);
		getWaveforms().add(sandboxWaveformRef);
		setSandboxWaveform(sandboxWaveformRef);
		setSandboxDeviceManager(sandboxDeviceManagerRef);
		// BEGIN GENERATED CODE
	}

	/**
	 * @since 4.0
	 */
	public OrbSession getSession() {
		return session;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void dispose() {
		// END GENERATED CODE
		super.dispose();
		DebugPlugin dp = DebugPlugin.getDefault();
		if (dp != null) {
			dp.getLaunchManager().removeLaunchListener(this.launchListener);
		}
		ScaModelCommand.execute(this, new ScaModelCommand() {

			@Override
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
		if (session != null) {
			session.dispose();
			session = null;
		}
		sandbox = null;
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
			return ((InternalEList< ? >) getWaveforms()).basicRemove(otherEnd, msgs);
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
		case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
			return getWaveforms();
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
			if (resolve)
				return getSandboxWaveform();
			return basicGetSandboxWaveform();
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
			return getSandboxDeviceManager();
		case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
			return getRootContext();
		case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
			return getFileManager();
		case ScaDebugPackage.LOCAL_SCA__SANDBOX:
			return getSandbox();
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
		case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
			getWaveforms().clear();
			getWaveforms().addAll((Collection< ? extends LocalScaWaveform>) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
			setSandboxWaveform((LocalScaWaveform) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
			setSandboxDeviceManager((LocalScaDeviceManager) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
			setRootContext((NotifyingNamingContext) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
			setFileManager((LocalFileManager) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX:
			setSandbox((SandboxOperations) newValue);
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
		case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
			getWaveforms().clear();
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
			setSandboxWaveform((LocalScaWaveform) null);
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
			setSandboxDeviceManager((LocalScaDeviceManager) null);
			return;
		case ScaDebugPackage.LOCAL_SCA__ROOT_CONTEXT:
			setRootContext((NotifyingNamingContext) null);
			return;
		case ScaDebugPackage.LOCAL_SCA__FILE_MANAGER:
			setFileManager((LocalFileManager) null);
			return;
		case ScaDebugPackage.LOCAL_SCA__SANDBOX:
			setSandbox(SANDBOX_EDEFAULT);
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
		case ScaDebugPackage.LOCAL_SCA__SANDBOX:
			return SANDBOX_EDEFAULT == null ? sandbox != null : !SANDBOX_EDEFAULT.equals(sandbox);
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

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (sandbox: ");
		result.append(sandbox);
		result.append(')');
		return result.toString();
	}

	@Override
	protected void internalFetchChildren(IProgressMonitor monitor) throws InterruptedException {
		// PASS

	}

	/**
	 * @since 4.0
	 */
	@Override
	protected Sandbox narrow(org.omg.CORBA.Object obj) {
		return SandboxHelper.narrow(obj);
	}

	/**
	 * @since 4.0
	 */
	@Override
	public void attachDataProviders() {
		// Do nothing
	}

	/**
	 * @since 6.0
	 */
	@Override
	public List<ScaWaveform> fetchWaveforms(IProgressMonitor monitor) {
		final List<ScaWaveform> retVal = new ArrayList<ScaWaveform>();
		ScaModelCommand.execute(this, new ScaModelCommand() {

			@Override
			public void execute() {
				retVal.addAll(getWaveforms());
			}
		});
		return Collections.unmodifiableList(retVal);
	}

} //LocalScaImpl
