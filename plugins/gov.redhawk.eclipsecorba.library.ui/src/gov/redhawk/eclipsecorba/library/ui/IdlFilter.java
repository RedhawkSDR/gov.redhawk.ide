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
package gov.redhawk.eclipsecorba.library.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.idl.IdlPackage;
import gov.redhawk.eclipsecorba.idl.Module;
import gov.redhawk.eclipsecorba.library.RepositoryModule;

/**
 * @since 1.2
 */
public enum IdlFilter {

	/**
	 * Pass through all IDLs
	 */
	ALL,

	/**
	 * Pass through all IDLs, provided they have a containing module
	 */
	ALL_WITH_MODULE,

	/**
	 * Only select REDHAWK-provided IDLs
	 */
	REDHAWK,

	/**
	 * Only select REDHAWK IDLs appropriate for ports
	 */
	PORTS;

	private static Set<String> redhawkModules;

	private static Set<String> cfIntfs;

	static {
		redhawkModules = new HashSet<String>();
		redhawkModules.add("CF");
		redhawkModules.add("BULKIO");
		redhawkModules.add("BURSTIO");
		redhawkModules.add("FRONTEND");

		cfIntfs = new HashSet<String>();
		Collections.addAll(cfIntfs, "AggregateDevice", "AggregateExecutableDevice", "AggregateLoadableDevice", "AggregatePlainDevice", "Application", "Device",
			"ExecutableDevice", "FileManager", "LifeCycle", "LoadableDevice", "Logging", "LogEventConsumer", "LogConfiguration", "PortSupplier",
			"PropertyEmitter", "PropertySet", "Resource", "TestableObject");
	}

	/**
	 * Gets a {@link ViewerFilter} appropriate to pass through IDLs that match this IdlFilter.
	 * @return The filter
	 */
	public ViewerFilter getFilter() {
		switch (this) {
		case ALL:
			return new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					return element instanceof IdlInterfaceDcl || element instanceof RepositoryModule || element instanceof IdlRepositoryPendingUpdateAdapter;
				}
			};
		case ALL_WITH_MODULE:
			return new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof IdlInterfaceDcl) {
						IdlInterfaceDcl intf = (IdlInterfaceDcl) element;
						return intf.eContainer().eClass().getClassifierID() == IdlPackage.MODULE;
					}
					return element instanceof RepositoryModule || element instanceof IdlRepositoryPendingUpdateAdapter;
				}
			};
		case REDHAWK:
			return new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof RepositoryModule) {
						RepositoryModule module = (RepositoryModule) element;
						return redhawkModules.contains(module.getName());
					}
					if (element instanceof IdlInterfaceDcl) {
						IdlInterfaceDcl intf = (IdlInterfaceDcl) element;
						return intf.eContainer().eClass().getClassifierID() == IdlPackage.MODULE;
					}
					return element instanceof IdlRepositoryPendingUpdateAdapter;
				}
			};
		case PORTS:
			return new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof RepositoryModule) {
						RepositoryModule module = (RepositoryModule) element;
						return redhawkModules.contains(module.getName());
					}
					if (element instanceof IdlInterfaceDcl) {
						IdlInterfaceDcl intf = (IdlInterfaceDcl) element;
						if (intf.eContainer().eClass().getClassifierID() != IdlPackage.MODULE) {
							return false;
						}
						String moduleName = ((Module) intf.eContainer()).getName();
						if ("BULKIO".equals(moduleName)) {
							return intf.getName().startsWith("data") && !"dataChar".equals(intf.getName());
						} else if ("CF".equals(moduleName)) {
							return cfIntfs.contains(intf.getName());
						}
						return true;
					}
					return element instanceof IdlRepositoryPendingUpdateAdapter;
				}
			};
		default:
			return null;
		}
	}

}
