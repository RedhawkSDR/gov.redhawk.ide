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
package gov.redhawk.ide.dcd.generator.newservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.dcd.IdeDcdPlugin;
import gov.redhawk.ide.dcd.generator.newservice.internal.ServiceIdlUtil;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class ScdFileTemplate {

	public ScdFileTemplate() {
	}

	public String generate(GeneratorArgs args) throws CoreException {
		IdlLibrary library = args.getLibrary();
		String repId = args.getRepId();

		SoftwareComponent component = ScdFactory.eINSTANCE.createSoftwareComponent();
		component.setCorbaVersion("2.2");
		component.setRepId(args.getRepId());
		component.setComponentType(ComponentType.SERVICE.getLiteral());

		component.setComponentFeatures(ScdFactory.eINSTANCE.createComponentFeatures());
		List<SupportsInterface> supportsInterfaces = ServiceIdlUtil.getSupportsInterfaceXMLTags(library, repId);
		component.getComponentFeatures().getSupportsInterface().addAll(supportsInterfaces);
		component.getComponentFeatures().setPorts(ScdFactory.eINSTANCE.createPorts());

		component.setInterfaces(ScdFactory.eINSTANCE.createInterfaces());
		List<Interface> interfaces = ServiceIdlUtil.getInterfaceXMLTags(library, repId);
		component.getInterfaces().getInterface().addAll(interfaces);
		component.getComponentFeatures().getSupportsInterface().addAll(supportsInterfaces);

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = resourceSet.createResource(URI.createURI("mem://new.scd.xml"), ScdPackage.eCONTENT_TYPE);
		resource.getContents().add(component);
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			resource.save(buffer, null);
			return buffer.toString();
		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, IdeDcdPlugin.PLUGIN_ID, "Unable to create SCD file", e));
		}
	}
}
