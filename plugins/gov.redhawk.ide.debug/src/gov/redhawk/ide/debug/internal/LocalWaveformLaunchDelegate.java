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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

import CF.DataType;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.SadLauncherUtil;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.util.StartJob;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * An Eclipse launch delegate which handles launching a SoftwareAssembly locally in the Sandbox.
 */
public class LocalWaveformLaunchDelegate extends LaunchConfigurationDelegate implements ILaunchConfigurationDelegate2 {

	/**
	 * Map of component's properties<br/>
	 * Key --> component instantiation ID<br />
	 * Value --> DataType property</br>
	 */
	private final Map<String, List<DataType>> componentPropertyMap = new HashMap<String, List<DataType>>();

	private static final int WORK_GET_LOCAL_SCA = 1, WORK_FETCH_PROPS = 1, WORK_CREATE_WAVEFORM = 10, WORK_UPDATE_AC = 1;

	private static final EStructuralFeature[] SAD_TO_ASSEMBLY_CONTROLLER_SPD = new EStructuralFeature[] {
		SadPackage.Literals.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER, SadPackage.Literals.ASSEMBLY_CONTROLLER__COMPONENT_INSTANTIATION_REF,
		PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, WORK_GET_LOCAL_SCA + WORK_FETCH_PROPS + WORK_CREATE_WAVEFORM + WORK_UPDATE_AC);

		final Map<String, String> implMap = SadLauncherUtil.getImplementationMap(configuration);
		final Resource sadResource = ScaResourceFactoryUtil.createResourceSet().getResource(ScaLaunchConfigurationUtil.getProfileURI(configuration), true);
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(sadResource);

		// Validate all XML before doing anything else
		IStatus status = SadLauncherUtil.validateAllXML(sad);
		if (!status.isOK()) {
			throw new CoreException(status);
		}

		// Clear property map
		componentPropertyMap.clear();

		// Load waveform properties from the sad.xml
		final ScaWaveform scaWaveform = ScaFactory.eINSTANCE.createScaWaveform();
		scaWaveform.setProfileObj(sad);
		scaWaveform.fetchProperties(progress.newChild(WORK_FETCH_PROPS));

		// Load user override values for the waveform
		ScaLaunchConfigurationUtil.loadProperties(configuration, scaWaveform);

		updateAssemblyControllerProperties(sad, scaWaveform, progress.newChild(WORK_UPDATE_AC));

		if (sad.getExternalProperties() != null) {
			updateExternalProperties(sad, scaWaveform);
		}

		updateNonExternalProperties(sad);

		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(progress.newChild(WORK_GET_LOCAL_SCA));
		final LocalApplicationFactory factory = new LocalApplicationFactory(implMap, localSca, mode, launch, componentPropertyMap);

		try {
			final SimpleDateFormat dateFormat = new SimpleDateFormat("DDD_HHmmssSSS");
			final LocalScaWaveform app = factory.create(sad, sad.getName() + "_" + dateFormat.format(new Date()), progress.newChild(WORK_CREATE_WAVEFORM));

			boolean start = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_START, ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
			if (start) {
				final StartJob job = new StartJob(app.getName(), app);
				job.schedule();
			}
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		}
	}

	/**
	 * Update assembly controller properties to reflect <i>"user > sad > prf"</i>, where user provided values take
	 * precedence over sad.xml values, which take precedence over prf.xml values.
	 * 
	 * @param sad
	 * @param scaWaveform
	 * @param progress
	 */
	private void updateAssemblyControllerProperties(SoftwareAssembly sad, ScaWaveform scaWaveform, SubMonitor progress) {
		// Find the assembly controller SPD
		SoftPkg assemblySoftPkg = ScaEcoreUtils.getFeature(sad, LocalWaveformLaunchDelegate.SAD_TO_ASSEMBLY_CONTROLLER_SPD);

		// Load assembly controllers properties from the prf.xml
		final ScaComponent assemblyController = ScaFactory.eINSTANCE.createScaComponent();
		assemblyController.setProfileObj(assemblySoftPkg);
		assemblyController.fetchProperties(progress);

		// Update assembly controllers properties
		List<DataType> acProps = new ArrayList<DataType>();
		for (ScaAbstractProperty< ? > prop : assemblyController.getProperties()) {
			DataType property = scaWaveform.getProperty(prop.getId()).getProperty();
			acProps.add(property);
		}
		String controllerInstId = sad.getAssemblyController().getComponentInstantiationRef().getInstantiation().getId();
		componentPropertyMap.put(controllerInstId, acProps);
	}

	/**
	 * Update external properties to reflect <i>"user > sad > prf"</i>, where user provided values take
	 * precedence over sad.xml values, which take precedence over prf.xml values.
	 * 
	 * @param sad
	 * @param scaWaveform
	 */
	private void updateExternalProperties(SoftwareAssembly sad, ScaWaveform scaWaveform) {
		for (ExternalProperty extProp : sad.getExternalProperties().getProperties()) {
			SadComponentInstantiation inst = sad.getComponentInstantiation(extProp.getCompRefID());

			// We've already added all of the assembly controller's properties, so skip this
			if (SoftwareAssembly.Util.isAssemblyController(inst)) {
				continue;
			}

			// Check if we've previously recorded properties against this component
			List<DataType> externalProps;
			if (componentPropertyMap.get(inst.getId()) != null) {
				externalProps = componentPropertyMap.get(inst.getId());
			} else {
				externalProps = new ArrayList<DataType>();
			}

			// Get the updated component property
			DataType dt = scaWaveform.getProperty(extProp.getExternalPropID()).getProperty();

			// We need the DataType ID to be the PRF_ID, not the ExternalPropID.
			// This matters when the ApplicationFactory is creating the launch configuration.
			dt.id = extProp.getPropID();

			externalProps.add(dt);
			componentPropertyMap.put(inst.getId(), externalProps);
		}
	}

	/**
	 * At this point, we have only updated values for properties that belong to the assembly controller, or that are
	 * marked explicitly as external. We need to check for property value overrides in the sad.xml for non-external
	 * properties.
	 * @param comp
	 */
	private void updateNonExternalProperties(final SoftwareAssembly sad) {

		List<SadComponentInstantiation> instantiations = sad.getAllComponentInstantiations();
		for (SadComponentInstantiation comp : instantiations) {

			// Check what properties we've previously recorded against this component
			List<DataType> props = this.componentPropertyMap.get(comp.getId());
			if (props == null) {
				props = new ArrayList<DataType>();
			}

			// Pulling out all the properties ID's from the previous list for easy comparison below
			List<String> modifiedProps = new ArrayList<String>();
			for (DataType prop : props) {
				modifiedProps.add(prop.id);
			}

			// If this is null, then that means there is no sad.xml override
			final ComponentProperties instProps = comp.getComponentProperties();
			if (instProps == null) {
				continue;
			}

			// Look for non-external properties and make sure these get added to the componentPropertyMap
			for (final Entry entry : instProps.getProperties()) {
				if (!(entry.getValue() instanceof AbstractPropertyRef< ? >)) {
					continue;
				}

				AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
				if (!modifiedProps.contains(ref.getRefID()) && ref.getProperty() != null) {
					props.add(new DataType(ref.getRefID(), ref.toAny()));
				}
			}

			this.componentPropertyMap.put(comp.getId(), props);
		}
	}
}
