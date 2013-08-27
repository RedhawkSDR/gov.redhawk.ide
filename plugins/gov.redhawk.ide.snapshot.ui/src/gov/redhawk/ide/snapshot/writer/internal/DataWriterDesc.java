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
package gov.redhawk.ide.snapshot.writer.internal;

import gov.redhawk.ide.snapshot.writer.DefaultDataWriterSettings;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterControls;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * 
 */
public class DataWriterDesc implements IDataWriterDesc {

	private String name;
	private IConfigurationElement element;
	private String id;
	private String description;
	private boolean hasControlFactory;

	public DataWriterDesc(IConfigurationElement element) {
		this.element = element;
		this.name = element.getAttribute("name");
		this.id = element.getAttribute("id");
		this.description = element.getAttribute("description");
		this.hasControlFactory = element.getAttribute("controls") != null;
	}

	@Override
	public IDataWriter createWriter() throws CoreException {
		return (IDataWriter) this.element.createExecutableExtension("class");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public IDataWriterControls createControlFactory() throws CoreException {
		return (IDataWriterControls) element.createExecutableExtension("controls");
	}

	@Override
	public boolean hasControlFactory() {
		return hasControlFactory;
	}

	@Override
	public IDataWriterSettings createWriterSettings() throws CoreException {
		if (element.getAttribute("settings") != null) {
			return (IDataWriterSettings) element.createExecutableExtension("settings");
		}
		return new DefaultDataWriterSettings();
	}

	@Override
	public int compareTo(IDataWriterDesc o) {
		return name.compareToIgnoreCase(o.getName());
	}

}
