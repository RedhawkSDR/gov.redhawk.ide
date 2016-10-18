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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.CFErrorFormatter;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

public class LocalApplicationFactory {

	private final Map<String, String> implMap;
	private final LocalSca localSca;
	private final String mode;
	private final ILaunch launch;
	private final Map<String, List<DataType>> componentPropertyMap;
	private final NotifyingNamingContext namingContext;

	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	public LocalApplicationFactory(final Map<String, String> implMap, final LocalSca localSca, final String mode, final ILaunch launch,
		final Map<String, List<DataType>> componentPropertyMap) {
		this.implMap = implMap;
		this.localSca = localSca;
		this.mode = mode;
		this.launch = launch;
		this.namingContext = this.localSca.getRootContext();
		this.componentPropertyMap = componentPropertyMap;
	}

	public static NotifyingNamingContext createWaveformContext(NotifyingNamingContext parent, String name) throws CoreException {
		// We need to escape all '.' characters in namespaced waveforms as they have specific meaning in the CORBA
		// naming service
		String adjustedName = name.replaceAll("\\.", "\\\\.");

		// The framework always adds "_n" to the end of the requested name, where n is one of {1, 2, 3, ...}.
		NamingContextExt retVal = null;
		for (int i = 1; retVal == null; i++) {
			try {
				retVal = NamingContextExtHelper.narrow(parent.bind_new_context(Name.toName(adjustedName + "_" + i)));
			} catch (AlreadyBound e) {
				// PASS - we'll try another name
			} catch (NotFound | CannotProceed | InvalidName e) {
				throw new CoreException(
					new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + adjustedName + " " + e.getMessage(), e));
			}
		}
		return parent.findContext(retVal);
	}

	// Need to escape all '.' characters in namespaced waveforms
	public static void bindApp(final ApplicationImpl app) throws CoreException {
		app.getStreams().getOutStream().println("Binding application...");
		try {
			NamingContextExt context = app.getWaveformContext();
			NameComponent[] name = Name.toName(app.name().replaceAll("\\.", "\\\\."));
			org.omg.CORBA.Object obj = app.getLocalWaveform().getCorbaObj();
			context.bind(name, obj);
			app.getStreams().getOutStream().println("Done Binding application.");
		} catch (final NotFound | CannotProceed | InvalidName | AlreadyBound | SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to bind application to context " + e.getMessage(), e));
		}
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

		// Try and narrow to the given name. If an already bound exception occurs, append _ + i to the end and try again
		// until we've found a good name.
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
			waveform.setNamingContext(LocalApplicationFactory.createWaveformContext(namingContext, name));

			app = new ApplicationImpl(waveform, appId, name);
			app.setLaunching(true);
			this.launch.addProcess(app);
			waveform.setLocalApp(app);

			progress.subTask("Bind application");
			LocalApplicationFactory.bindApp(app);
			progress.worked(1);

			URI uri = sad.eResource().getURI();
			if (uri.isFile()) {
				waveform.setProfileURI(uri);
			} else {
				Map<String, String> query = new HashMap<String, String>(QueryParser.parseQuery(uri.query()));
				query.put(ScaFileSystemConstants.QUERY_PARAM_WF, waveform.getIor());
				String queryStr = QueryParser.createQuery(query);
				URI sadUri = URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), uri.segments(), queryStr, uri.fragment());
				waveform.setProfileURI(sadUri);
			}
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

			progress.subTask("Create connections");
			createConnections(app, sad);
			progress.worked(3);

		} catch (final SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create application: " + name + " " + e.getMessage(), e));
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
		final List<SadComponentInstantiation> instantiations = sad.getAllComponentInstantiations();
		final SubMonitor progress = SubMonitor.convert(monitor, instantiations.size());

		app.getStreams().getOutStream().println("Launching components...");
		for (final SadComponentInstantiation comp : instantiations) {
			progress.subTask(String.format("Launch component instance '%s'", comp.getUsageName()));

			URI spdUri = getSpdURI(comp);
			if (spdUri != null) {
				List<DataType> componentProps = this.componentPropertyMap.get(comp.getId());
				if (componentProps == null) {
					componentProps = new ArrayList<DataType>();
				}
				LocalScaComponent localComp = app.launch(comp.getUsageName(), comp.getId(), componentProps.toArray(new DataType[0]), spdUri, getImplId(comp),
					this.mode);
				if (localComp != null) {
					TransactionalEditingDomain localEditingDomain = TransactionUtil.getEditingDomain(localComp);
					if (localEditingDomain != null) {
						localEditingDomain.getCommandStack().execute(
							SetCommand.create(localEditingDomain, localComp, ScaPackage.Literals.SCA_COMPONENT__COMPONENT_INSTANTIATION, comp));
					}
				}
			} else {
				String errorMsg = String.format("Failed to find SPD for component: %s", comp.getUsageName());
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, errorMsg));
			}
			app.getStreams().getOutStream().println("\n");
			progress.worked(1);
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
						if (componentForPort.getScaPort(providesId) == null) {
							app.getStreams().getErrStream().println(
								componentForPort.getInstantiationIdentifier() + " does not contain a port of name: " + providesId);
						} else {
							target = componentForPort.getScaPort(providesId).getCorbaObj();
						}
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
					if (port == null) {
						app.getStreams().getErrStream().println(component.getInstantiationIdentifier() + " does not contain a port of name: " + usesID);
					} else if (port instanceof ScaUsesPort) {
						final ScaUsesPort usesPort = (ScaUsesPort) port;
						try {
							usesPort.connectPort(target, connection.getId());
						} catch (final InvalidPort e) {
							app.getStreams().getErrStream().println(CFErrorFormatter.format(e, "connection " + connection.getId()));
						} catch (final OccupiedPort e) {
							app.getStreams().getErrStream().println(CFErrorFormatter.format(e, "connection " + connection.getId()));
						}
					}
				}
			}
			app.getStreams().getOutStream().println("Done making connections");
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

	@Nullable
	private URI getSpdURI(@Nullable final SadComponentInstantiation comp) {
		final SoftPkg spd = ScaEcoreUtils.getFeature(comp, LocalApplicationFactory.SPD_PATH);
		if (spd != null && spd.eResource() != null) {
			return spd.eResource().getURI();
		} else {
			return null;
		}
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
}
