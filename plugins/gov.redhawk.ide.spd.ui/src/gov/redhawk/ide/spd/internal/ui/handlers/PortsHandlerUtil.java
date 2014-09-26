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
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * @since 6.0
 */
public class PortsHandlerUtil {

	public static final String ADD_COMMAND = "gov.redhawk.ide.spd.ui.command.addPort";
	public static final String EDIT_COMMAND = "gov.redhawk.ide.spd.ui.command.editPort";
	public static final String REMOVE_COMMAND = "gov.redhawk.ide.spd.ui.command.removePorts";
	private static final EStructuralFeature[] INTERFACES_PATH = new EStructuralFeature[] { SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
	        SpdPackage.Literals.DESCRIPTOR__COMPONENT, ScdPackage.Literals.SOFTWARE_COMPONENT__INTERFACES };
	private static final EStructuralFeature[] PORTS_PATH = new EStructuralFeature[] { SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
	        SpdPackage.Literals.DESCRIPTOR__COMPONENT, ScdPackage.Literals.SOFTWARE_COMPONENT__COMPONENT_FEATURES,
	        ScdPackage.Literals.COMPONENT_FEATURES__PORTS };

	private PortsHandlerUtil() {
		//Prevent instantiation
	}

	/**
	 * Executes the specified command if it can be executed.
	 * 
	 * @param command the command
	 */
	public static void execute(final Command command, final EditingDomain editingDomain) throws CoreException {
		if (command.canExecute()) {
			editingDomain.getCommandStack().execute(command);
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to execute: " + command.getLabel(), new Exception().fillInStackTrace()));
		}
	}

	/**
	 * Gets the ports associated with the SoftwareComponent.
	 * 
	 * @return the {@link mil.jpeojtrs.sca.scd.Ports} associated with the {@link mil.jpeojtrs.sca.scd.SoftwareComponent}
	 */
	public static Ports getPorts(final SoftPkg softPkg) {
		return ScaEcoreUtils.getFeature(softPkg, PortsHandlerUtil.PORTS_PATH);
	}

	/**
	 * Gets the interfaces associated with the SoftwareComponent.
	 * 
	 * @return the {@link mil.jpeojtrs.sca.scd.Interfaces} associated with the {@link mil.jpeojtrs.sca.scd.SoftwareComponent}
	 */
	public static Interfaces getInterfaces(final SoftPkg softPkg) {
		return ScaEcoreUtils.getFeature(softPkg, PortsHandlerUtil.INTERFACES_PATH);
	}

	/**
	 * Provides convenient access to all repIds/interface pairs associated with the component.
	 * 
	 * @return a {@link Map} containing a map from repId to {@link Interface}
	 */
	public static Map<String, Interface> getInterfaceMap(final SoftPkg softPkg) {
		final Map<String, Interface> interfaceMap = new HashMap<String, Interface>();
		final Interfaces interfaces = PortsHandlerUtil.getInterfaces(softPkg);
		if (interfaces != null) {
			for (final Interface i : interfaces.getInterface()) {
				interfaceMap.put(i.getRepid(), i);
			}
		}
		return interfaceMap;
	}

	/**
	 * 
	 * @param library
	 * @param repID
	 * @return
	 */
	public static Collection<String> getInheritedInterfaces(final IdlLibrary library, final String repID) {
		final Set<String> retVal = new HashSet<String>();
		final IdlInterfaceDcl idlInter = (IdlInterfaceDcl) library.find(repID);
		//If the interface isn't present in the IdlLibrary, there's nothing to do
		if (idlInter != null) {
			//Add all the inherited interfaces first.
			for (final IdlInterfaceDcl inherited : idlInter.getInheritedInterfaces()) {
				retVal.add(inherited.getRepId());
			}
		}
		return retVal;
	}

}
