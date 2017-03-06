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
package gov.redhawk.ide.debug.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;

import CF.DataType;
import CF.InvalidIdentifier;
import CF.InvalidObjectReference;
import CF.LogEvent;
import CF.PropertiesHolder;
import CF.UnknownIdentifier;
import CF.UnknownProperties;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PropertyEmitterPackage.AlreadyInitialized;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;
import CF.ResourcePackage.StopError;
import CF.TestableObjectPackage.UnknownTest;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.SilentJob;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Used for components of domain waveforms when the waveform is opened with the sandbox. Most operations are proxied
 * to the {@link ScaComponent} so that the two objects stay in sync.
 * @since 9.0
 */
public class ProxyScaComponentImpl extends LocalScaComponentImpl {

	private ScaComponent target;

	private Adapter targetListener = new EContentAdapter() {

		@Override
		public void notifyChanged(Notification msg) {
			super.notifyChanged(msg);
			if (msg.getNotifier() instanceof ScaAbstractProperty< ? >) {
				// Ignore the "ignore remote set" attribute (which gets changed a lot)
				if (msg.getFeatureID(ScaAbstractProperty.class) != ScaPackage.SCA_ABSTRACT_PROPERTY__IGNORE_REMOTE_SET) {
					// If we get here, most likely it's the object's value that changed. Find the root object for the
					// property (necessary for structs and struct sequences). TODO: We could update the value for just
					// the part of the property that changed.
					ScaAbstractProperty< ? > targetProp = (ScaAbstractProperty< ? >) msg.getNotifier();
					while (targetProp.eContainer() instanceof ScaAbstractProperty< ? >) {
						targetProp = (ScaAbstractProperty< ? >) targetProp.eContainer();
						if (targetProp == null) {
							return;
						}
					}

					// Update our property value to match the target's property value
					ScaAbstractProperty< ? > prop = getProperty(targetProp.getId());
					if (prop != null) {
						prop.fromAny(targetProp.toAny());
					}
				}
			} else if (msg.getNotifier() instanceof ScaComponent) {
				if (msg.getEventType() == Notification.SET) {
					switch (msg.getFeatureID(ScaComponent.class)) {
					case ScaPackage.SCA_COMPONENT__DISPOSED:
						if (msg.getNewBooleanValue()) {
							ProxyScaComponentImpl.this.target.eAdapters().remove(this);
							EcoreUtil.delete(ProxyScaComponentImpl.this);
						}
						break;
					case ScaPackage.SCA_COMPONENT__IDENTIFIER:
						setIdentifier(msg.getNewStringValue());
						break;
					case ScaPackage.SCA_COMPONENT__PROFILE:
						setProfile(msg.getNewStringValue());
						break;
					case ScaPackage.SCA_COMPONENT__PROFILE_OBJ:
						setProfileObj((SoftPkg) msg.getNewValue());
						break;
					case ScaPackage.SCA_COMPONENT__STARTED:
						setStarted((Boolean) msg.getNewValue());
						break;
					default:
						break;
					}
				}
			} else if (msg.getNotifier() instanceof ScaUsesPort) {
				if (msg.getFeatureID(ScaUsesPort.class) == ScaPackage.SCA_USES_PORT__CONNECTIONS) {
					// Find our matching port object
					String portName = ((ScaUsesPort) msg.getNotifier()).getName();
					if (portName == null) {
						return;
					}
					final ScaUsesPort usesPort = (ScaUsesPort) getScaPort(portName);
					if (usesPort == null) {
						return;
					}

					// Update our port
					new SilentJob(execParam) {

						@Override
						protected IStatus runSilent(IProgressMonitor monitor) {
							usesPort.fetchConnections(monitor);
							return Status.OK_STATUS;
						}
					}.schedule();
				}
			}
		}

		protected void addAdapter(Notifier notifier) {
			// Watch the component, its uses ports, and its properties
			if (notifier instanceof ScaAbstractProperty || notifier instanceof ScaUsesPort || notifier instanceof ScaComponent) {
				super.addAdapter(notifier);
			}
		}
	};

	/**
	 * This method <b>must</b> be called in a transaction so it can add itself as an adapter to the target.
	 * @param target The {@link ScaComponent} in the domain's model to proxy.
	 */
	public ProxyScaComponentImpl(ScaComponent target) {
		this.target = target;

		// Listen for changes
		ProxyScaComponentImpl.this.target.eAdapters().add(targetListener);
	}

	//////////////////////////////
	// Forward the certain fetch operations to the target first. Use what the target has cached when we fetch for
	// ourself.
	//////////////////////////////

	@Override
	public String identifier() {
		// Use cached value from target
		return target.getIdentifier();
	}

	@Override
	public String fetchIdentifier(IProgressMonitor monitor) {
		// Cause target to fetch, then update ourself using that
		SubMonitor progress = SubMonitor.convert(monitor, 2);
		target.fetchIdentifier(progress.split(1));
		return super.fetchIdentifier(progress.split(1));
	}

	@Override
	public boolean started() {
		// Use cached value from target
		Boolean retVal = target.getStarted();
		return (retVal == null) ? false : retVal;
	}

	@Override
	public Boolean fetchStarted(IProgressMonitor monitor) {
		// Cause target to fetch, then update ourself using that
		SubMonitor progress = SubMonitor.convert(monitor, 2);
		target.fetchStarted(progress.split(1));
		return super.fetchStarted(progress.split(1));
	}

	@Override
	public String softwareProfile() {
		// Use cached value from target
		String newProfile = target.getProfile();
		if (newProfile == null) {
			throw new COMM_FAILURE();
		}
		return newProfile;
	}

	@Override
	public String fetchProfile(IProgressMonitor monitor) {
		// Cause target to fetch, then update ourself using that
		SubMonitor progress = SubMonitor.convert(monitor, 2);
		target.fetchProfile(progress.split(1));
		return super.fetchProfile(progress.split(1));
	}

	@Override
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		// Use the target's existing cached property values.
		try {
			ScaModelCommand.runExclusive(target, new RunnableWithResult.Impl<Object>() {

				@Override
				public void run() {
					if (configProperties.value.length == 0) {
						List<DataType> dtProps = new ArrayList<>();
						for (ScaAbstractProperty< ? > prop : target.getProperties()) {
							dtProps.add(new DataType(prop.getId(), prop.toAny()));
						}
						configProperties.value = dtProps.toArray(new DataType[0]);
					} else {
						List<DataType> unknownProps = new ArrayList<>();
						for (DataType dt : configProperties.value) {
							ScaAbstractProperty< ? > prop = target.getProperty(dt.id);
							if (prop == null) {
								unknownProps.add(dt);
							} else {
								dt.value = prop.toAny();
							}
						}
						if (unknownProps.size() > 0) {
							UnknownProperties exception = new UnknownProperties(unknownProps.toArray(new DataType[0]));
							setStatus(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Invalid properties", exception));
						} else {
							setStatus(new Status(IStatus.OK, ScaDebugPlugin.ID, ""));
						}
					}
				}
			});
		} catch (InterruptedException e) {
			// PASS
		}
	}

	@Override
	public EList<ScaAbstractProperty< ? >> fetchProperties(IProgressMonitor monitor) {
		// Cause target to fetch, then update ourself using that
		SubMonitor progress = SubMonitor.convert(monitor, 2);
		target.fetchProperties(progress.split(1));
		return super.fetchProperties(progress.split(1));
	}

	@Override
	public SoftPkg fetchProfileObject(IProgressMonitor monitor) {
		// Cause target to fetch, then update ourself
		SubMonitor progress = SubMonitor.convert(monitor, 2);
		target.fetchProfileObject(progress.split(1));
		return super.fetchProfileObject(progress.split(1));
	}

	//////////////////////////////
	// Certain actions can be forwarded to the target. We'll get notified by the target of changes.
	//////////////////////////////

	@Override
	public boolean _is_a(String repId) {
		return target._is_a(repId);
	}

	@Override
	public void initializeProperties(DataType[] initialProperties) throws AlreadyInitialized, InvalidConfiguration, PartialConfiguration {
		target.initializeProperties(initialProperties);
	}

	@Override
	public String registerPropertyListener(Object obj, String[] propIds, float interval) throws UnknownProperties, InvalidObjectReference {
		return target.registerPropertyListener(obj, propIds, interval);
	}

	@Override
	public void unregisterPropertyListener(String id) throws InvalidIdentifier {
		target.unregisterPropertyListener(id);
	}

	@Override
	public void configure(DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		target.configure(configProperties);
	}

	@Override
	public void start() throws StartError {
		target.start();
	}

	@Override
	public void stop() throws StopError {
		target.stop();
	}

	@Override
	public void initialize() throws InitializeError {
		target.initialize();
	}

	@Override
	public void releaseObject() throws ReleaseError {
		target.releaseObject();
	}

	@Override
	public void runTest(int testid, PropertiesHolder testValues) throws UnknownTest, UnknownProperties {
		target.runTest(testid, testValues);
	}

	@Override
	public LogEvent[] retrieve_records(IntHolder howMany, int startingRecord) {
		return target.retrieve_records(howMany, startingRecord);
	}

	@Override
	public LogEvent[] retrieve_records_by_date(IntHolder howMany, long toTimeStamp) {
		return target.retrieve_records_by_date(howMany, toTimeStamp);
	}

	@Override
	public LogEvent[] retrieve_records_from_date(IntHolder howMany, long fromTimeStamp) {
		return target.retrieve_records_from_date(howMany, fromTimeStamp);
	}

	@Override
	public int log_level() {
		return target.log_level();
	}

	@Override
	public void log_level(int newLogLevel) {
		target.log_level(newLogLevel);
	}

	@Override
	public void setLogLevel(String loggerId, int newLevel) throws UnknownIdentifier {
		target.setLogLevel(loggerId, newLevel);
	}

	@Override
	public String getLogConfig() {
		return target.getLogConfig();
	}

	@Override
	public void setLogConfig(String configContents) {
		target.setLogConfig(configContents);
	}

	@Override
	public void setLogConfigURL(String configUrl) {
		target.setLogConfigURL(configUrl);
	}
}
