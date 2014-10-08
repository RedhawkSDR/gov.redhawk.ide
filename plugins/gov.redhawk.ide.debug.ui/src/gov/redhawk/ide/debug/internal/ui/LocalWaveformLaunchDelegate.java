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
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaSimpleSequenceProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.model.sca.impl.ScaFactoryImpl;
import gov.redhawk.model.sca.util.StartJob;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
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

		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(monitor);
		final Map<String, String> implMap = SadLauncherUtil.getImplementationMap(configuration);

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource sadResource = resourceSet.getResource(ScaLaunchConfigurationUtil.getProfileURI(configuration), true);
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
		
		final Map<String, AbstractProperty> extProps = new HashMap<String, AbstractProperty>();
		for (ExternalProperty extProp: sad.getExternalProperties().getProperties()) {
			SadComponentInstantiation inst = sad.getComponentInstantiation(extProp.getCompRefID());
			if (inst == null) {
				continue;
			}
			AbstractProperty absProp = inst.getPlacement().getComponentFileRef().getFile().getSoftPkg().getPropertyFile().getProperties().getProperty(extProp.getPropID());
			if (absProp == null) {
				continue;
			}
			extProps.put(extProp.resolveExternalID(), absProp);
		}
		final String properties = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROPERTIES, (String) null);
		final XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(properties.getBytes()));
		final Map< ? , ? > propMap = (Map< ? , ? >) decoder.readObject();
		decoder.close();
		ScaFactory propFactory = ScaFactoryImpl.init();
		for (Object key: propMap.keySet()) {
			if (extProps.containsKey(key)) {
				AbstractProperty prop = (AbstractProperty) extProps.get(key);
				ScaAbstractProperty< ? > newProp = makeScaProperty(propFactory, prop);
				final Object value = propMap.get(key);
				if (prop instanceof Simple) {
					((ScaSimpleProperty) newProp).setValue(value);
				} else if (prop instanceof SimpleSequence) {
					((ScaSimpleSequenceProperty) newProp).setValue((Object[]) value);
				} else if (prop instanceof Struct) {
					setStructValue((ScaStructProperty) newProp, (Map< ?, ? >) value);
				} else if (prop instanceof StructSequence) {
					for (Object obj: (List< ? >) value) {
						setStructValue(((ScaStructSequenceProperty) prop).createScaStructProperty(), (Map< ?, ?>) obj);
					}
				}
				if (!newProp.isDefaultValue() && newProp.getDefinition() != null
						&& newProp.getDefinition().isKind(PropertyConfigurationType.CONFIGURE, PropertyConfigurationType.EXECPARAM)) {
					newProp.setId((String) key);
					assemblyConfig.add(newProp.getProperty());
				}
			}
		}
			
		final LocalApplicationFactory factory = new LocalApplicationFactory(implMap, localSca, mode, launch,
			assemblyExec.toArray(new DataType[assemblyExec.size()]), assemblyConfig.toArray(new DataType[assemblyConfig.size()]));
		final SimpleDateFormat dateFormat = new SimpleDateFormat("DDD_HHmmssSSS");
		try {
			final LocalScaWaveform app = factory.create(sad, name + "_" + dateFormat.format(new Date()), monitor);
			if (start) {
				final StartJob job = new StartJob(app.getName(), app);
				job.schedule();
			}
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		}
	}

	private ScaAbstractProperty< ? > makeScaProperty(final ScaFactory factory, final AbstractProperty oldProp) {
		ScaAbstractProperty< ? > newProp = null;
		if (oldProp instanceof Simple) {
			newProp = factory.createScaSimpleProperty();
			((ScaSimpleProperty) newProp).setDefinition((Simple) oldProp);
		} else if (oldProp instanceof SimpleSequence) {
			newProp = factory.createScaSimpleSequenceProperty();
			((ScaSimpleSequenceProperty) newProp).setDefinition((SimpleSequence) oldProp);
		} else if (oldProp instanceof Struct) {
			newProp = factory.createScaStructProperty();
			((ScaStructProperty) newProp).setDefinition((Struct) oldProp);
		} else if (oldProp instanceof StructSequence) {
			newProp = factory.createScaStructSequenceProperty();
			((ScaStructSequenceProperty) newProp).setDefinition((StructSequence) oldProp);
		} else {
			return null;
		}
		newProp.fromAny(oldProp.toAny());
		return newProp;
	}
	
	private void setStructValue(ScaStructProperty struct, Map< ?, ?> valMap) {
		for (ScaSimpleProperty simp: struct.getSimples()) {
			if (valMap.containsKey(simp.getId())) {
				simp.setValue(valMap.get(simp.getId()));
			}
		}
	}
	
}
