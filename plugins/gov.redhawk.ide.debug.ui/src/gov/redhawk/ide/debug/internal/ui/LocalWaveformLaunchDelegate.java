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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.SadLauncherUtil;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.util.StartJob;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.DataType;

/**
 * 
 */
public class LocalWaveformLaunchDelegate extends LaunchConfigurationDelegate implements ILaunchConfigurationDelegate2 {

	public static final String ID = "gov.redhawk.ide.debug.ui.launchLocalWaveform";

	private static final EStructuralFeature[] PATH = new EStructuralFeature[] { SadPackage.Literals.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER,
		SadPackage.Literals.ASSEMBLY_CONTROLLER__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION,
		PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT, PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF,
		PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE, PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final boolean start = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_START, ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		final String path = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, "");
		boolean platform = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_WORKSPACE, true);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(monitor);
		final Map<String, String> implMap = SadLauncherUtil.getImplementationMap(configuration);

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource sadResource;
		if (platform) {
			sadResource = resourceSet.getResource(URI.createPlatformResourceURI(path, true), true);
		} else {
			sadResource = resourceSet.getResource(URI.createURI(path), true);
		}
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(sadResource);
		final String name = sad.getName();
		final List<DataType> assemblyConfig = new ArrayList<DataType>();
		final List<DataType> assemblyExec = new ArrayList<DataType>();
		SoftPkg assemblySoftPkg = ScaEcoreUtils.getFeature(sad, LocalWaveformLaunchDelegate.PATH);
		if (assemblySoftPkg != null) {
			final ScaComponent assemblyController = ScaFactory.eINSTANCE.createScaComponent();
			assemblyController.setProfileObj(assemblySoftPkg);
			for (final ScaAbstractProperty< ? > prop : assemblyController.fetchProperties(null)) {
				prop.setIgnoreRemoteSet(true);
			}
			ScaLaunchConfigurationUtil.loadProperties(configuration, assemblyController);
			for (final ScaAbstractProperty< ? > prop : assemblyController.getProperties()) {
				if (!prop.isDefaultValue() && prop.getDefinition() != null
						&& prop.getDefinition().isKind(PropertyConfigurationType.CONFIGURE, PropertyConfigurationType.EXECPARAM)) {
					assemblyConfig.add(prop.getProperty());
				}
			}
		}
		final LocalApplicationFactory factory = new LocalApplicationFactory(implMap, localSca, mode, launch,
			assemblyExec.toArray(new DataType[assemblyExec.size()]), assemblyConfig.toArray(new DataType[assemblyConfig.size()]));
		final SimpleDateFormat dateFormat = new SimpleDateFormat("DDD_HHmmssSSS");
		final LocalScaWaveform app = factory.create(sad, name + "_" + dateFormat.format(new Date()), monitor);
		if (start) {
			final StartJob job = new StartJob(app.getName(), app);
			job.schedule();
		}
	}

}
