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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.jacorb.naming.Name;
import org.omg.CORBA.Any;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.DataType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;

/**
 * 
 */
public class LocalApplicationFactory {

	private final Map<String, String> implMap;
	private final LocalSca localSca;
	private final String mode;
	private final ILaunch launch;
	private final DataType[] assemblyConfig;
	private final DataType[] assemblyExec;
	private final NotifyingNamingContext namingContext;

	public LocalApplicationFactory(final LocalSca localSca) {
		this.implMap = null;
		this.localSca = localSca;
		this.mode = ILaunchManager.RUN_MODE;
		this.launch = null;
		this.assemblyExec = null;
		this.assemblyConfig = null;
		this.namingContext = this.localSca.getRootContext();
	}

	public LocalApplicationFactory(final Map<String, String> implMap, final LocalSca localSca, final String mode, final ILaunch launch,
		final DataType[] assemblyExec, final DataType[] assemblyConfig) {
		this.implMap = implMap;
		this.localSca = localSca;
		this.mode = mode;
		this.launch = launch;
		this.assemblyExec = assemblyExec;
		this.assemblyConfig = assemblyConfig;
		this.namingContext = this.localSca.getRootContext();
	}

	/**
	 * {@inheritDoc}
	 */
	public LocalScaWaveform create(final SoftwareAssembly sad, String name, final IProgressMonitor monitor) throws CoreException {
		String adjustedName = name;
		NamingContextExt waveformContext = null;

		// Try and narrow to the given name.  If an already bound exception occurs, append _ + i to the end and try again until 
		// we've found a good name.
		try {
			for (int i = 2; waveformContext == null; i++) {
				try {
					waveformContext = NamingContextExtHelper.narrow(this.namingContext.bind_new_context(Name.toName(adjustedName)));
				} catch (AlreadyBound e) {
					adjustedName = name + "_" + i;
				} catch (NotFound e) {
					throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
						"Failed to create application: " + adjustedName + " " + e.getMessage(), e));
				} catch (CannotProceed e) {
					throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
						"Failed to create application: " + adjustedName + " " + e.getMessage(), e));
				} catch (InvalidName e) {
					throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
						"Failed to create application: " + adjustedName + " " + e.getMessage(), e));
				}
			}

			final String appId = DceUuidUtil.createDceUUID();
			final String profile = sad.eResource().getURI().path();

			final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
			waveform.setDataProvidersEnabled(false);
			waveform.setProfile(profile);
			waveform.setProfileURI(sad.eResource().getURI());
			waveform.setProfileObj(sad);
			waveform.setLaunch(this.launch);
			waveform.setMode(this.launch.getLaunchMode());
			waveform.setNamingContext(this.namingContext.findContext(waveformContext));

			final ApplicationImpl app = new ApplicationImpl(waveform, appId, adjustedName);
			this.launch.addProcess(app);
			waveform.setLocalApp(app);
			ScaModelCommand.execute(this.localSca, new ScaModelCommand() {

				public void execute() {
					LocalApplicationFactory.this.localSca.getWaveforms().add(waveform);
				}
			});

			launchComponents(app, sad);

			configureComponents(app, sad, this.assemblyConfig);

			createConnections(app, sad);

			bindApp(app);
			
			try {
				waveform.refresh(null, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}

			app.getStreams().getOutStream().println("Done");
			return waveform;
		} catch (final SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
		}
	}

	private String getImplId(final SadComponentInstantiation comp) {
		String retVal = null;
		if (this.implMap != null) {
			retVal = this.implMap.get(comp.getId());
		}
		if (retVal == null) {
			retVal = comp.getPlacement().getComponentFileRef().getFile().getSoftPkg().getImplementation().get(0).getId();
		}
		return retVal;
	}

	/**
	 * @param app
	 * @param sad
	 * @param assemblyConfig
	 * @throws CoreException
	 */
	protected void configureComponents(final ApplicationImpl app, final SoftwareAssembly sad, final DataType[] assemblyConfig) {
		app.getStreams().getOutStream().println("Configuring Components...");
		for (final ScaComponent comp : app.getLocalWaveform().getComponents()) {
			try {
				app.getStreams().getOutStream().println("Configuring component: " + comp.getName());
				configureComponent(app, comp);
				app.getStreams().getOutStream().println("");
			} catch (final InvalidConfiguration e) {
				app.logException("WARNING: Error while configuring component: " + comp.getName() + " InvalidConfiguration ", e);
			} catch (final PartialConfiguration e) {
				app.logException("WARNING: Error while configuring component: " + comp.getName() + " PartialConfiguration ", e);
			}
		}
	}

	/**
	 * @param app
	 * @param sad
	 * @param config
	 * @throws CoreException
	 */
	protected void launchComponents(final ApplicationImpl app, final SoftwareAssembly sad) throws CoreException {
		final List<SadComponentInstantiation> instantiations = getComponentInstantiations(sad);

		app.getStreams().getOutStream().println("Launching components...");
		for (final SadComponentInstantiation comp : instantiations) {
			try {
				app.launch(comp.getUsageName(), createExecParam(comp), getSpdURI(comp).toString(), getImplId(comp), this.mode);
			} catch (final ExecuteFail e) {
				app.getStreams().getErrStream().println("Failed to launch " + comp.getUsageName() + " " + e.msg);
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to launch " + comp.getUsageName(), e));
			}
			app.getStreams().getOutStream().println("\n");
		}
	}

	private void bindApp(final ApplicationImpl app) throws CoreException {
		app.getStreams().getOutStream().println("Binding application...");
		try {
			NamingContextExt context = app.getWaveformContext();
			NameComponent[] name = Name.toName(app.name());
			org.omg.CORBA.Object obj = app.getLocalWaveform().getCorbaObj();
			context.bind(name, obj);
		} catch (final NotFound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final CannotProceed e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final AlreadyBound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		}

	}

	private void createConnections(final ApplicationImpl app, final SoftwareAssembly sad) {
		if (sad.getConnections() != null) {
			app.getStreams().getOutStream().println("Making connections...");
			for (final SadConnectInterface connection : sad.getConnections().getConnectInterface()) {
				app.getStreams().getOutStream().println("Creating connection " + connection.getId());
				org.omg.CORBA.Object target = null;
				if (connection.getProvidesPort() != null) {
					final String providesId = connection.getProvidesPort().getProvidesIdentifier();

					if (connection.getProvidesPort().getComponentInstantiationRef() != null) {
						target = app.getLocalWaveform().getScaComponent(connection.getProvidesPort().getComponentInstantiationRef().getRefid()).getScaPort(
							providesId).getCorbaObj();
					}
				} else if (connection.getComponentSupportedInterface() != null) {
					target = app.getLocalWaveform().getScaComponent(connection.getComponentSupportedInterface().getComponentInstantiationRef().getRefid()).getCorbaObj();
				}

				if (target != null) {
					final String usesID = connection.getUsesPort().getUsesIndentifier();
					if (connection.getUsesPort().getComponentInstantiationRef() == null) {
						app.getStreams().getErrStream().println("Failed to create connection " + connection.getId());
						continue;
					}
					final ScaComponent component = app.getLocalWaveform().getScaComponent(connection.getUsesPort().getComponentInstantiationRef().getRefid());
					final ScaPort< ? , ? > port = component.getScaPort(usesID);
					if (port instanceof ScaUsesPort) {
						final ScaUsesPort usesPort = (ScaUsesPort) port;
						try {
							usesPort.connectPort(target, connection.getId());
						} catch (final InvalidPort e) {
							app.getStreams().getErrStream().println("Failed to create connection " + connection.getId());
						} catch (final OccupiedPort e) {
							app.getStreams().getErrStream().println("Failed to create connection " + connection.getId());
						}
					}
				}
			}
			app.getStreams().getOutStream().println("Done making connections");
		}
	}

	private void configureComponent(ApplicationImpl app, final ScaComponent comp) throws InvalidConfiguration, PartialConfiguration {
		DataType[] configuration = getConfiguration(comp);
		if (configuration == null || configuration.length == 0) {
			app.getStreams().getOutStream().println("\tNo configuration.");
			return;
		}
		if (this.assemblyConfig != null && isAssemblyController(comp.getComponentInstantiation())) {
			configuration = this.assemblyConfig;
		}
		for (DataType t : configuration) {
			app.getStreams().getOutStream().println(toString(t));
		}
		comp.configure(configuration);
		comp.fetchProperties(null);
	}

	public static String toString(DataType t) {
		Object value = AnyUtils.convertAny(t.value);
		if (value instanceof DataType[]) {
			StringBuilder builder = new StringBuilder();
			builder.append("\t" + t.id + " = {");
			for (DataType child : (DataType[]) value) {
				builder.append("\n\t\t" + toString(child));
			}
			builder.append("\n\t}");
			return builder.toString();
		} else if (value instanceof DataType) {
			return "\t" + t.id + " = {" + toString((DataType) value) + "}";
		} else if (value instanceof Any[]) {
			StringBuilder builder = new StringBuilder();
			builder.append("\t" + t.id + " = [] {");
			int i = 0;
			for (Any child : (Any[]) value) {
				builder.append("\n\t\t[" + i++ + "] = ");
				Object childValue = AnyUtils.convertAny(child);
				String valueStr;
				if (childValue instanceof DataType) {
					valueStr = toString((DataType) childValue);
				} else if (childValue instanceof DataType[]) {
					StringBuilder valueStrBuilder = new StringBuilder();
					valueStrBuilder.append("{");
					for (DataType childType : (DataType[]) childValue) {
						valueStrBuilder.append("\n\t\t" + toString(childType));
					}
					valueStrBuilder.append("\n\t\t}");
					valueStr = valueStrBuilder.toString();
				} else {
					valueStr = String.valueOf(childValue);
				}
				builder.append(valueStr);
			}
			builder.append("\n\t}");
			return builder.toString();
		} else if (value != null && value.getClass().isArray()) {

			return "\t" + t.id + " = " + ArrayUtils.toString(value);
		} else {
			return "\t" + t.id + " = " + value;
		}
	}

	private DataType[] getConfiguration(final ScaComponent comp) {
		final Map<String, DataType> retVal = new HashMap<String, DataType>();
		comp.fetchProperties(null);
		for (final ScaAbstractProperty< ? > prop : comp.getProperties()) {
			if (PropertiesUtil.canConfigure(prop.getDefinition())) {
				retVal.put(prop.getId(), new DataType(prop.getId(), prop.toAny()));
			}
		}
		final ScaWaveform waveform = comp.getWaveform();
		final SoftwareAssembly sad = waveform.getProfileObj();
		SadComponentInstantiation compInst = null;

		for (final SadComponentInstantiation ci : getComponentInstantiations(sad)) {
			if (ci.getId().equals(comp.getInstantiationIdentifier())) {
				compInst = ci;
				break;
			}
		}

		if (compInst == null) {
			throw new IllegalStateException("Unable to find component instantiation");
		}

		final ComponentProperties props = compInst.getComponentProperties();
		if (props != null) {
			// Override default values
			for (final Entry entry : props.getProperties()) {
				if (entry.getValue() instanceof AbstractPropertyRef< ? >) {
					final AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
					if (retVal.containsKey(ref.getRefID())) {
						retVal.put(ref.getRefID(), new DataType(ref.getRefID(), ref.toAny()));
					}
				}

			}
		}
		return retVal.values().toArray(new DataType[retVal.size()]);
	}

	private List<SadComponentInstantiation> getComponentInstantiations(final SoftwareAssembly sad) {
		final List<SadComponentInstantiation> retVal = new ArrayList<SadComponentInstantiation>();
		if (sad.getPartitioning() != null) {
			for (final SadComponentPlacement cp : sad.getPartitioning().getComponentPlacement()) {
				retVal.addAll(cp.getComponentInstantiation());
			}
			for (final HostCollocation hc : sad.getPartitioning().getHostCollocation()) {
				for (final SadComponentPlacement cp : hc.getComponentPlacement()) {
					retVal.addAll(cp.getComponentInstantiation());
				}
			}
		}
		return retVal;
	}

	private URI getSpdURI(final SadComponentInstantiation comp) {
		final SoftPkg spd = comp.getPlacement().getComponentFileRef().getFile().getSoftPkg();
		return spd.eResource().getURI();
	}

	private DataType[] createExecParam(final SadComponentInstantiation comp) {
		if (this.assemblyExec != null && isAssemblyController(comp)) {
			return this.assemblyExec;
		}
		final ComponentProperties props = comp.getComponentProperties();
		final List<DataType> retVal = new ArrayList<DataType>();
		if (props != null) {
			for (final Entry entry : props.getProperties()) {
				if (entry.getValue() instanceof SimpleRef) {
					final SimpleRef ref = (SimpleRef) entry.getValue();
					if (ref.getProperty().isKind(PropertyConfigurationType.EXECPARAM)) {
						retVal.add(new DataType(ref.getRefID(), ref.toAny()));
					}
				}
			}
		}
		return retVal.toArray(new DataType[retVal.size()]);
	}

	private boolean isAssemblyController(final SadComponentInstantiation comp) {
		if (comp == null) {
			return false;
		}
		final SoftwareAssembly sad = (SoftwareAssembly) EcoreUtil.getRootContainer(comp);
		if (sad.getAssemblyController() != null) {
			return sad.getAssemblyController().getComponentInstantiationRef().getInstantiation() == comp;
		}
		return false;
	}
}
