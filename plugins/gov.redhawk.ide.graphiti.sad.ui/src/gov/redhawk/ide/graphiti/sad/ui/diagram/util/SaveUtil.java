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
package gov.redhawk.ide.graphiti.sad.ui.diagram.util;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.sca.ScaPlugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import mil.jpeojtrs.sca.profile.ProfilePackage;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

public class SaveUtil { // SUPPRESS CHECKSTYLE INLINE

	/**
	 * Returns the SoftwareAssembly model for the diagram. Each diagram should be accompanied by a .sad.xml file
	 * within the same directory as the diagram, otherwise a new .sad.xml file is created and populated with a
	 * SoftwareAssembly model
	 * @param d
	 * @return
	 */
	public static SoftwareAssembly getModelFileInStoredResource(final Diagram d) {
		SoftwareAssembly sa = null;
		SadDocumentRoot docRoot = null;
		URI uri = d.eResource().getURI();

		// TODO:bwhoff2 move hard coded extension
		uri = uri.trimFragment().trimFileExtension().appendFileExtension("sad.xml");
		ResourceSet rSet = d.eResource().getResourceSet();
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		// IResource file = workspaceRoot.findMember(uri.toPlatformString(true));
		File file = workspaceRoot.findMember(uri.toPlatformString(true)).getLocation().toFile();

		if (file == null || !file.exists()) {
			// create docroot and SoftwareAssembly container
			docRoot = SadFactory.eINSTANCE.createSadDocumentRoot();
			sa = SadFactory.eINSTANCE.createSoftwareAssembly();
			docRoot.setSoftwareassembly(sa);
			// create resource and add docroot and softwareAssembly
			Resource createResource = rSet.createResource(uri);
			createResource.getContents().add(docRoot);
			try {
				createResource.save(new HashMap<Object, Object>());
			} catch (IOException e) {
				ErrorDialog.openError(Display.getDefault().getActiveShell(), "Error", "Problem opening the Software Assembly configuration file",
					new org.eclipse.core.runtime.Status(IStatus.ERROR, SADUIGraphitiPlugin.getDefault().getBundle().getSymbolicName(),
						"Problem opening the Software Assembly file"));
				return null;
			}
			createResource.setTrackingModification(true);
		} else {
			// load the .sad.xml resource
			// if the result set already knows about the software assembly file, simply return that
			// NOTE: this section is required, simply using loadConfigViaEMF() does not seem to work properly even
			// though
			// it makes sense in theory
			TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);

			URI uri2 = URI.createURI(file.toURI().toString());
			Resource r = domain.getResourceSet().getResource(uri2, false);
			if (r != null) {
				docRoot = (SadDocumentRoot) r.getContents().get(0);
				if (docRoot != null) {
					return docRoot.getSoftwareassembly();
				}
			}

			// load the software assembly directly from the .sad.xml file
			// TODO: bwhoff2 see my other version of this code, this portion might not be necessary
//			try{
//				sa = Transformer
//			}
		}

		return sa;
	}

	public static void saveModelFile(SadDocumentRoot docRoot, String modelFileUri) throws IOException {

		// get existing model file
		URI uri = URI.createURI(modelFileUri);
		File modelFile = new File(uri.toFileString());

		// if file doesn't exist, create it
		if (!modelFile.exists()) {
			modelFile.createNewFile();
		}

		// serialize
		File tempFile = new File(modelFileUri);

		// create a resourceset and register the default factory
		ResourceSet set = new ResourceSetImpl();

		// ensure that the model package is registered even though we won't be using this variable
		@SuppressWarnings("unused")
		ProfilePackage pkg = ProfilePackage.eINSTANCE;

		URI tempUri = URI.createURI(tempFile.toURI().toString());
		Resource r = set.createResource(tempUri);

		// add the modelFile to the resource, if we don't add all the subcomponents there is a dangling resource issue
		r.getContents().add(docRoot);

		OutputStream out = null;
		try {
			// writing to the zip file
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			r.save(out, null);
		} finally {
			try {
				out.close();
			} catch (FileNotFoundException e) {
				// PASS
				// TODO: catch exception
			}
		}

	}

}
