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
package gov.redhawk.ide.ui.wizard;

import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.ui.wizard.NonEclipseImportWizardPage.ProjectRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class NonEclipseImportUtil {
	// CHECKSTYLE:OFF
	private static String sadExtension = ".+\\.sad.xml";
	private static String spdExtension = ".+\\.spd.xml";
	private static String dcdExtension = ".+\\.dcd.xml";
	private ProjectRecord record;
	private boolean dotProjectMissing = true;
	private boolean libraryMissing;
	private boolean wavedevMissing;
	private boolean pydevProjectMissing;
	private IProgressMonitor monitor;
	private URI projectLocation;
	private String projectName;
	private boolean copyFiles;
	private NonEclipseImportWizardPage parent;

	public static String getName(IPath path) throws IOException,
			XMLStreamException {
		// Check for 'name' attribute in XML root
		String projectName = path.toFile().getName();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(path.toFile());
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				String element = startElement.getName().getLocalPart();
				if (element.equals("deviceconfiguration")
						|| element.equals("softwareassembly")
						|| element.equals("softpkg")) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement
							.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equals("name")) {
							projectName = attribute.getValue();
							return projectName;
						}
					}
				}
			}
		}
		// If there is there is no 'name' attribute then use the file name as
		// the project name
		projectName = path.toFile().getName();
		int dotIndex = projectName.indexOf('.');
		projectName = projectName.substring(0, dotIndex);
		return projectName;

	}

	public void createMissingFiles(ProjectRecord record,
			IProgressMonitor monitor, boolean copyFiles,
			NonEclipseImportWizardPage parent) {
		this.record = record;
		this.monitor = monitor;
		this.projectName = record.projectName;
		this.copyFiles = copyFiles;
		this.parent = parent;
		this.projectLocation = record.projectSystemFile.getParentFile()
				.getAbsoluteFile().toURI();
		String type = this.record.projectSystemFile.getName();
		if (type.matches(sadExtension)) {
			makeSADFiles();
		}
		if (type.matches(dcdExtension)) {
			makeDCDFiles();
		}
		if (type.matches(spdExtension)) {
			makeSPDFiles();
		}
	}

	private void makeSADFiles() {
		findMissingFiles();
		if (dotProjectMissing) {
			createDotProjectFile("SAD");
		}
	}

	private void makeDCDFiles() {
		findMissingFiles();
		if (dotProjectMissing) {
			createDotProjectFile("DCD");
		}
	}

	private void makeSPDFiles() {
		findMissingFiles();
		if (dotProjectMissing) {
			createDotProjectFile("SPD");
		}
		if (libraryMissing) {
			// TODO create this file
		}
		if (wavedevMissing) {
			// TODO create this file
		}
		if (pydevProjectMissing) {
			// TODO create this file
		}
	}

	private void findMissingFiles() {
		File[] contents = record.projectSystemFile.getParentFile().listFiles();
		for (File f : contents) {
			if (f.getName().matches(".+\\.project")) {
				dotProjectMissing = false;
				continue;
			}
			if (f.getName().matches(".+\\.library")) {
				libraryMissing = false;
				continue;
			}
			if (f.getName().matches(".+\\.wavedev")) {
				wavedevMissing = false;
				continue;
			}
			if (f.getName().matches(".+\\.pydevproject")) {
				pydevProjectMissing = false;
				continue;
			}
		}

	}

	private File createDotProjectFile(String projectType) {
		try {
			IProject project = null;
			File importSource = new File(record.projectSystemFile
					.getParentFile().getAbsolutePath());

			// Build new .project files of the appropriate type
			if (projectType.equals("SAD")) {
				if (!copyFiles) {
					project = WaveformProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = WaveformProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}
			if (projectType.equals("DCD")) {
				if (!copyFiles) {
					project = NodeProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = NodeProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}
			if (projectType.equals("SPD")) {
				if (!copyFiles) {
					project = ComponentProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = ComponentProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}

			// If "copy into" box was checked, import files into workspace
			if (copyFiles) {
				List<?> filesToImport = FileSystemStructureProvider.INSTANCE
						.getChildren(importSource);
				ImportOperation operation = new ImportOperation(
						project.getFullPath(), importSource,
						FileSystemStructureProvider.INSTANCE, parent,
						filesToImport);
				operation.setContext(parent.getShell());
				operation.setOverwriteResources(true);
				operation.setCreateContainerStructure(false);
				operation.run(monitor);
			}

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
