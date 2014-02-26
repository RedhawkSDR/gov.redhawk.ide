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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.util;

import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrRoot;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * @since 8.2
 */
public class SdrPluginLoader {

	public static SdrRoot getSdrRoot(String pluginID, String path) throws URISyntaxException, IOException {
		final java.net.URI rootPath = FileLocator.toFileURL(FileLocator.find(Platform.getBundle(pluginID), new Path(path), null)).toURI();
		return SdrPluginLoader.getSdrRoot(URI.createURI(rootPath.toString()));
	}

	public static synchronized SdrRoot getSdrRoot(final URI sdrRootPath) {
		Assert.isNotNull(sdrRootPath);
		final TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		final ResourceSet resourceSet = editingDomain.getResourceSet();
		final SdrRoot sdrRoot = SdrFactory.eINSTANCE.createSdrRoot();
		final Resource sdrResource = resourceSet.createResource(URI.createURI("virtual://sdr.sdr"));
		sdrRoot.setSdrRoot(sdrRootPath, "dom", "dev");
		editingDomain.getCommandStack().execute(new AddCommand(editingDomain, sdrResource.getContents(), sdrRoot));
		sdrRoot.load(null);
		Assert.isNotNull(sdrRoot.getComponentsContainer(), "Failed to load Components SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getDevicesContainer(), "Failed to load Devices SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getWaveformsContainer(), "Failed to load Waveforms SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getServicesContainer(), "Failed to load Services SDR Root for path: " + sdrRootPath);
		return sdrRoot;
	}

} // SdrTests
