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
package gov.redhawk.ide.dcd.generator.newservice.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.redhawk.eclipsecorba.idl.Identifiable;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.dcd.RepIdHelper;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SupportsInterface;

public class ServiceIdlUtil {

	private ServiceIdlUtil() {
	}

	/**
	 * Gets the supportsinterface XML tags that go in the componentfeatures section of an SCD file.
	 * @param repId The parent CORBA interface's repid
	 * @return An XML fragment
	 */
	public static List<SupportsInterface> getSupportsInterfaceXMLTags(IdlLibrary library, String repId) {
		Identifiable ident = (library != null) ? library.find(repId) : null;
		if (ident != null && ident instanceof IdlInterfaceDcl) {
			IdlInterfaceDcl idlInterface = (IdlInterfaceDcl) ident;
			return getSupportsInterfaces(idlInterface);
		} else {
			// Fallback if the repid can't be found in the IDL library
			SupportsInterface intf = ScdFactory.eINSTANCE.createSupportsInterface();
			intf.setRepId(repId);
			intf.setSupportsName(RepIdHelper.getProperInterfaceName(repId));
			return Collections.singletonList(intf);
		}
	}

	/**
	 * Gets the interface XML tags that go in the interface section of an SCD file.
	 * @param repId The parent CORBA interface's repid
	 * @return An XML fragment
	 */
	public static List<Interface> getInterfaceXMLTags(IdlLibrary library, String repId) {
		Identifiable ident = (library != null) ? library.find(repId) : null;
		if (ident != null && ident instanceof IdlInterfaceDcl) {
			IdlInterfaceDcl idlInterface = (IdlInterfaceDcl) ident;
			return getInterfaces(idlInterface);
		} else {
			// Fallback if the repid can't be found in the IDL library
			Interface intf = ScdFactory.eINSTANCE.createInterface();
			intf.setRepid(repId);
			intf.setName(RepIdHelper.getProperInterfaceName(repId));
			return Collections.singletonList(intf);
		}
	}

	private static List<SupportsInterface> getSupportsInterfaces(IdlInterfaceDcl target) {
		return getSupportsInterfaces(target, new HashSet<IdlInterfaceDcl>());
	}

	private static List<SupportsInterface> getSupportsInterfaces(IdlInterfaceDcl target, Set<IdlInterfaceDcl> visitedInterfaces) {
		List<SupportsInterface> supportsInterfaces = new ArrayList<SupportsInterface>();

		SupportsInterface intf = ScdFactory.eINSTANCE.createSupportsInterface();
		intf.setRepId(target.getRepId());
		intf.setSupportsName(target.getName());
		supportsInterfaces.add(intf);

		visitedInterfaces.add(target);
		for (IdlInterfaceDcl parent : target.getInheritedInterfaces()) {
			if (!visitedInterfaces.contains(parent)) {
				supportsInterfaces.addAll(getSupportsInterfaces(parent, visitedInterfaces));
			}
		}

		return supportsInterfaces;
	}

	private static List<Interface> getInterfaces(IdlInterfaceDcl target) {
		return getInterfaces(target, new HashSet<IdlInterfaceDcl>());
	}

	private static List<Interface> getInterfaces(IdlInterfaceDcl target, Set<IdlInterfaceDcl> visitedInterfaces) {
		List<Interface> interfaces = new ArrayList<Interface>();

		Interface intf = ScdFactory.eINSTANCE.createInterface();
		intf.setRepid(target.getRepId());
		intf.setName(target.getName());
		for (IdlInterfaceDcl parent : target.getInheritedInterfaces()) {
			InheritsInterface parentIntf = ScdFactory.eINSTANCE.createInheritsInterface();
			parentIntf.setRepid(parent.getRepId());
			intf.getInheritsInterfaces().add(parentIntf);
		}
		interfaces.add(intf);

		visitedInterfaces.add(target);
		for (IdlInterfaceDcl parent : target.getInheritedInterfaces()) {
			if (!visitedInterfaces.contains(parent)) {
				interfaces.addAll(getInterfaces(parent, visitedInterfaces));
			}
		}

		return interfaces;
	}
}
