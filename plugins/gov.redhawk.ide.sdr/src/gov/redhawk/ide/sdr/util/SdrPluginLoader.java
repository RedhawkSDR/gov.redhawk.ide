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

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.commands.SetSdrRootCommand;

/**
 * This class is used primarily by tests to load an {@link SdrRoot} model object for an SDRROOT located within a bundle.
 * @since 8.2
 */
public class SdrPluginLoader {

	/**
	 * Gets an {@link SdrRoot} model for an SDRROOT inside a bundle.
	 * @param pluginID The bundle ID
	 * @param path A path inside the bundle
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static SdrRoot getSdrRoot(String pluginID, String path) throws URISyntaxException, IOException {
		final java.net.URI rootPath = FileLocator.toFileURL(FileLocator.find(Platform.getBundle(pluginID), new Path(path), null)).toURI();
		return SdrPluginLoader.getSdrRoot(URI.createURI(rootPath.toString()));
	}

	/**
	 * Gets an {@link SdrRoot} model for a given URI.
	 * @param sdrRootPath The URI of the SDRROOT
	 * @return
	 */
	public static synchronized SdrRoot getSdrRoot(final URI sdrRootPath) {
		Assert.isNotNull(sdrRootPath);
		final TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		final ResourceSet resourceSet = editingDomain.getResourceSet();
		final Resource sdrResource = resourceSet.createResource(URI.createURI("virtual://sdr.sdr"));
		final SdrRoot sdrRoot = SdrFactory.eINSTANCE.createSdrRoot();
		editingDomain.getCommandStack().execute(new AddCommand(editingDomain, sdrResource.getContents(), sdrRoot));
		editingDomain.getCommandStack().execute(new SetSdrRootCommand(sdrRoot, sdrRootPath, "dom", "dev"));
		sdrRoot.load(new NullProgressMonitor());
		Assert.isNotNull(sdrRoot.getComponentsContainer(), "Failed to load Components SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getDevicesContainer(), "Failed to load Devices SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getWaveformsContainer(), "Failed to load Waveforms SDR Root for path: " + sdrRootPath);
		Assert.isNotNull(sdrRoot.getServicesContainer(), "Failed to load Services SDR Root for path: " + sdrRootPath);
		return sdrRoot;
	}

} // SdrTests
