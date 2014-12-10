/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;

public abstract class AbstractGraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	/**
	 * @param dtp
	 */
	public AbstractGraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		
		// Add Find By patterns
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());
	}

}
