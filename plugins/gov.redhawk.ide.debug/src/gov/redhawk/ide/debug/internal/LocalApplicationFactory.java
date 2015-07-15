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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jdt.annotation.Nullable;
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
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

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

	public static NotifyingNamingContext createWaveformContext(NotifyingNamingContext parent, String name) throws CoreException {
		NamingContextExt retVal = null;
		String adjustedName = name;
		for (int i = 2; retVal == null; i++) {
			try {
				retVal = NamingContextExtHelper.narrow(parent.bind_new_context(Name.toName(adjustedName)));
			} catch (AlreadyBound e) {
				adjustedName = name + "_" + i;
			} catch (NotFound e) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
			} catch (CannotProceed e) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
			} catch (InvalidName e) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
			}
		}
		return parent.findContext(retVal);
	}

	/**
	 * Carries out all the steps necessary to launch a waveform in the sandbox, including:
	 * <ul>
	 * <li>Launching components</li>
	 * <li>Configuring components</li>
	 * <li>Making waveform connections</li>
	 * </ul>
	 * @param sad
	 * @param name
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public LocalScaWaveform create(final SoftwareAssembly sad, String name, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, 100);
		String adjustedName = name;

		// Try and narrow to the given name. If an already bound exception occurs, append _ + i to the end and try again
		// until
		// we've found a good name.
		ApplicationImpl app = null;
		LocalScaWaveform waveform = null;
		try {
			progress.subTask("Create application");
			final String appId = DceUuidUtil.createDceUUID();
			final String profile = sad.eResource().getURI().path();

			waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
			waveform.setProfile(profile);
			waveform.setLaunch(this.launch);
			waveform.setMode(this.launch.getLaunchMode());
			waveform.setNamingContext(LocalApplicationFactory.createWaveformContext(namingContext, adjustedName));

			app = new ApplicationImpl(waveform, appId, adjustedName);
			app.setLaunching(true);
			this.launch.addProcess(app);
			waveform.setLocalApp(app);

			progress.subTask("Bind application");
			LocalApplicationFactory.bindApp(app);
			progress.worked(1);

			URI uri = sad.eResource().getURI();
			Map<String, String> query = new HashMap<String, String>(QueryParser.parseQuery(uri.query()));
			query.put(ScaFileSystemConstants.QUERY_PARAM_WF, waveform.getIor());
			String queryStr = QueryParser.createQuery(query);
			URI sadUri = URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), uri.segments(), queryStr, uri.fragment());
			waveform.setProfileURI(sadUri);
			waveform.fetchProfileObject(null);

			final ScaWaveform tmpWaveform = waveform;
			ScaModelCommand.execute(this.localSca, new ScaModelCommand() {

				@Override
				public void execute() {
					LocalApplicationFactory.this.localSca.getWaveforms().add(tmpWaveform);
				}
			});

			progress.worked(1);

			progress.subTask("Launch components");
			launchComponents(progress.newChild(90), app, sad);

			progress.subTask("Configure components");
			configureComponents(app, sad, this.assemblyConfig);
			progress.worked(4);

			progress.subTask("Create connections");
			createConnections(app, sad);
			progress.worked(3);


		} catch (final SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
		} finally {
			if (app != null) {
				app.setLaunching(false);
			}
		}
		
		if (app != null && waveform != null) {
			progress.subTask("Refresh");
			SubMonitor subTask = progress.newChild(1);
			try {
				waveform.refresh(subTask, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}

			app.getStreams().getOutStream().println("Done");
		}
		progress.done();
		return waveform;
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
	 * Performs the initial configure call on each component in the waveform
	 *
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
				configureComponent(app, comp, sad, assemblyConfig);
				app.getStreams().getOutStream().println("");
			} catch (final InvalidConfiguration e) {
				app.logException("WARNING: Error while configuring component: " + comp.getName() + " InvalidConfiguration ", e);
			} catch (final PartialConfiguration e) {
				app.logException("WARNING: Error while configuring component: " + comp.getName() + " PartialConfiguration ", e);
			}
		}
	}

	/**
	 * Launches each component of the waveform
	 *
	 * @param monitor
	 * @param app
	 * @param sad
	 * @param config
	 * @throws CoreException
	 */
	protected void launchComponents(IProgressMonitor monitor, final ApplicationImpl app, final SoftwareAssembly sad) throws CoreException {
		final List<SadComponentInstantiation> instantiations = getComponentInstantiations(sad);
		final SubMonitor progress = SubMonitor.convert(monitor, instantiations.size());

		app.getStreams().getOutStream().println("Launching components...");
		for (final SadComponentInstantiation comp : instantiations) {
			progress.subTask(String.format("Launch component instance '%s'", comp.getUsageName()));

			URI spdUri = getSpdURI(comp);
			if (spdUri == null) {
				String errorMsg = String.format("Failed to find SPD for component: %s", comp.getUsageName());
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, errorMsg));
			} else {
				LocalScaComponent localComp = app.launch(comp.getUsageName(), createExecParam(comp), spdUri, getImplId(comp), this.mode);
				if (localComp != null) {
					TransactionalEditingDomain localEditingDomain = TransactionUtil.getEditingDomain(localComp);
					if (localEditingDomain != null) {
						localEditingDomain.getCommandStack().execute(SetCommand.create(localEditingDomain, localComp, ScaPackage.Literals.SCA_COMPONENT__COMPONENT_INSTANTIATION, comp));
					}
				}
			}

			app.getStreams().getOutStream().println("\n");
			progress.worked(1);
		}
	}

	public static void bindApp(final ApplicationImpl app) throws CoreException {
		app.getStreams().getOutStream().println("Binding application...");
		try {
			NamingContextExt context = app.getWaveformContext();
			NameComponent[] name = Name.toName(app.name());
			org.omg.CORBA.Object obj = app.getLocalWaveform().getCorbaObj();
			context.bind(name, obj);
			app.getStreams().getOutStream().println("Done Binding application.");
		} catch (final NotFound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final CannotProceed e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final AlreadyBound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		} catch (final SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		}

	}

	private void createConnections(final ApplicationImpl app, final SoftwareAssembly sad) throws CoreException {
		if (sad.getConnections() != null) {
			app.getStreams().getOutStream().println("Making connections...");
			for (final SadConnectInterface connection : sad.getConnections().getConnectInterface()) {
				app.getStreams().getOutStream().println("Creating connection " + connection.getId());
				org.omg.CORBA.Object target = null;
				if (connection.getProvidesPort() != null) {
					final String providesId = connection.getProvidesPort().getProvidesIdentifier();

					if (connection.getProvidesPort().getComponentInstantiationRef() != null) {
						final String componentRefId = connection.getProvidesPort().getComponentInstantiationRef().getRefid();
						final ScaComponent componentForPort = app.getLocalWaveform().getScaComponent(componentRefId);
						if (componentForPort == null) {
							String errorMsg = String.format("Couldn't find component instance '%s' to make waveform connection '%s'", componentRefId,
								connection.getId());
							throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, errorMsg));
						}
						target = componentForPort.getScaPort(providesId).getCorbaObj();
					}
				} else if (connection.getComponentSupportedInterface() != null) {
					final String componentRefId = connection.getComponentSupportedInterface().getComponentInstantiationRef().getRefid();
					final ScaComponent component = app.getLocalWaveform().getScaComponent(componentRefId);
					if (component == null) {
						String errorMsg = String.format("Couldn't find component instance '%s' to make waveform connection '%s'", componentRefId,
							connection.getId());
						throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, errorMsg));
					}
					target = component.getCorbaObj();
				} else {
					app.getStreams().getErrStream().println("Unsupported target connection type for connection: " + connection.getId());
				}

				if (target != null) {
					final String usesID = connection.getUsesPort().getUsesIdentifier();
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

	private void configureComponent(ApplicationImpl app, final ScaComponent comp, final SoftwareAssembly sad, final DataType[] assemblyConfig) throws InvalidConfiguration, PartialConfiguration {
		DataType[] configuration = getConfiguration(comp);
		final ApplicationOutputStream outStream = app.getStreams().getOutStream();
		if (configuration == null || configuration.length == 0) {
			outStream.println("\tNo configuration.");
			return;
		}
		final boolean isAssemblyController = isAssemblyController(comp.getComponentInstantiation());
		if (assemblyConfig != null && assemblyConfig.length > 0 && isAssemblyController) {
			// apply user property overrides (in assemblyConfig)
			for (DataType userOverrideProperty : assemblyConfig) {
				for (DataType compSadProperty : configuration) {
					if (userOverrideProperty.id.equals(compSadProperty.id)) {
						compSadProperty.value = userOverrideProperty.value;
						break;
					}
				}
			}
		}
		final ExternalProperties externalProperties = sad.getExternalProperties();
		for (DataType t : configuration) {
			if (externalProperties != null) {
				for (ExternalProperty extProp: externalProperties.getProperties()) {
					if (t.id.equals(extProp.getExternalPropID()) && extProp.getCompRefID().equals(comp.getName())) {
						t.id = extProp.getPropID();
					}
				}
			}
			outStream.println(LocalApplicationFactory.toString(t));
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
				builder.append("\n\t\t" + LocalApplicationFactory.toString(child));
			}
			builder.append("\n\t}");
			return builder.toString();
		} else if (value instanceof DataType) {
			return "\t" + t.id + " = {" + LocalApplicationFactory.toString((DataType) value) + "}";
		} else if (value instanceof Any[]) {
			StringBuilder builder = new StringBuilder();
			builder.append("\t" + t.id + " = [] {");
			int i = 0;
			for (Any child : (Any[]) value) {
				builder.append("\n\t\t[" + i++ + "] = ");
				Object childValue = AnyUtils.convertAny(child);
				String valueStr;
				if (childValue instanceof DataType) {
					valueStr = LocalApplicationFactory.toString((DataType) childValue);
				} else if (childValue instanceof DataType[]) {
					StringBuilder valueStrBuilder = new StringBuilder();
					valueStrBuilder.append("{");
					for (DataType childType : (DataType[]) childValue) {
						valueStrBuilder.append("\n\t\t" + LocalApplicationFactory.toString(childType));
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

	/** Gets Component's properties, override with values from Waveform (SAD) */
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

		final ComponentProperties sadProps = compInst.getComponentProperties();
		if (sadProps != null) {
			// 2. Override default values with values from SAD
			for (final Entry entry : sadProps.getProperties()) {
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

	private static final EStructuralFeature[] PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	@Nullable
	private URI getSpdURI(@Nullable final SadComponentInstantiation comp) {
		final SoftPkg spd = ScaEcoreUtils.getFeature(comp, LocalApplicationFactory.PATH);
		if (spd != null && spd.eResource() != null) {
			return spd.eResource().getURI();
		} else {
			return null;
		}
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
