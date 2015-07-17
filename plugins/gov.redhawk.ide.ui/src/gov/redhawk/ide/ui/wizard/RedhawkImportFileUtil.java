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
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class RedhawkImportFileUtil extends RedhawkImportUtil {

	private ProjectRecord record;
	private IProgressMonitor monitor;
	private RedhawkImportWizardPage1 parent;
	private URI projectLocation;
	private String projectName;
	private int result;

	private boolean dotProjectMissing = true;
	private boolean wavedevMissing = true;
	private boolean copyFiles;

	public static String getName(IPath path) throws IOException, XMLStreamException {
		// Check for 'name' attribute in XML root
		String projectName;

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(path.toFile());
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				String element = startElement.getName().getLocalPart();
				if ("deviceconfiguration".equals(element) || "softwareassembly".equals(element) || "softpkg".equals(element)) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if ("name".equals(attribute.getName().toString())) {
							projectName = attribute.getValue();
							return projectName;
						}
					}
				}
			}
		}
		// If there is there is no 'name' attribute then use the file name as the project name
		projectName = path.toFile().getName();
		int dotIndex = projectName.indexOf('.');
		projectName = projectName.substring(0, dotIndex);
		return projectName;
	}

	public void createMissingFiles(ProjectRecord record, IProgressMonitor monitor, boolean copyFiles, RedhawkImportWizardPage1 parent) {
		this.record = record;
		this.monitor = monitor;
		this.projectName = record.projectName;
		this.copyFiles = copyFiles;
		this.parent = parent;
		this.projectLocation = record.projectSystemFile.getParentFile().getAbsoluteFile().toURI();
		String type = this.record.projectSystemFile.getName();

		if (findMissingFiles() == SWT.CANCEL) {
			// User canceled import because of missing source directories
			// Don't create files
			return;
		}

		try {
			IProject project = null;
			WaveDevSettings waveDev = null;
			if (type.matches(getSadExtension()) && dotProjectMissing) {
				project = createDotProjectFile("SAD");
			}
			if (type.matches(getDcdExtension()) && dotProjectMissing) {
				project = createDotProjectFile("DCD");
			}
			if (type.matches(getSpdExtension())) {
				if (dotProjectMissing) {
					project = createDotProjectFile("SPD");
				}
				if (wavedevMissing) {
					waveDev = createWaveDevFile();
				} else {
					File waveDevFile = null;
					File[] contents = record.projectSystemFile.getParentFile().listFiles();
					for (File file : contents) {
						String name = file.getName();
						if (name.matches(".+\\.wavedev")) {
							waveDevFile = file;
						}
					}
					if (waveDevFile != null) {
						final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
						org.eclipse.emf.common.util.URI waveDevUri = org.eclipse.emf.common.util.URI.createPlatformResourceURI(waveDevFile.getPath(), true);
						waveDev = (WaveDevSettings) set.getResource(waveDevUri, true).getEObject("/");
					}
				}

				if (dotProjectMissing || wavedevMissing) {
					generateFiles(monitor, projectLocation.toString(), project, waveDev);
				}

			}
		} catch (CoreException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
	}

	protected int findMissingFiles() {
		boolean hasSource = true;

		File[] contents = record.projectSystemFile.getParentFile().listFiles();
		if (contents != null) {
			for (File file : contents) {
				String name = file.getName();

				// TODO: Evaluate if source is present and set hasSource

				// Check for .project and .wavedev files
				if (name.matches(".+\\.project")) {
					dotProjectMissing = false;
					continue;
				}
				if (name.matches(".+\\.wavedev")) {
					wavedevMissing = false;
					continue;
				}
			}
		}

		if (!hasSource) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					dialog.setText("Error - No Source Directory");
					dialog.setMessage("Warning: \nNo source files found in the specified location.  " + "\nImported projects will not function correctly");
					result = dialog.open();
				}
			});
		}
		result = SWT.OK;
		return result;
	}

	protected IProject createDotProjectFile(String projectType) {
		try {
			IProject project = null;
			File importSource = new File(record.projectSystemFile.getParentFile().getAbsolutePath());

			// Check to see whether project is being imported from within the workspace directory hierarchy
			boolean isDefaultLocation = false;
			String defaultLocation = Platform.getLocation().toFile() + ".+";
			String source = importSource.toString();
			if (source.matches(defaultLocation)) {
				isDefaultLocation = true;
			}

			// Build new .project files of the appropriate type
			if ("SAD".equals(projectType)) {
				if (!copyFiles && !isDefaultLocation) {
					project = WaveformProjectCreator.createEmptyProject(projectName, projectLocation, monitor);
				} else {
					project = WaveformProjectCreator.createEmptyProject(projectName, null, monitor);
				}
			}
			if ("DCD".equals(projectType)) {
				if (!copyFiles && !isDefaultLocation) {
					project = NodeProjectCreator.createEmptyProject(projectName, projectLocation, monitor);
				} else {
					project = NodeProjectCreator.createEmptyProject(projectName, null, monitor);
				}
			}
			if ("SPD".equals(projectType)) {
				if (!copyFiles && !isDefaultLocation) {
					project = ComponentProjectCreator.createEmptyProject(projectName, projectLocation, monitor);
				} else {
					project = ComponentProjectCreator.createEmptyProject(projectName, null, monitor);
				}
			}

			// If selected, copy files to workspace
			if (copyFiles) {
				List< ? > filesToImport = FileSystemStructureProvider.INSTANCE.getChildren(importSource);
				ImportOperation operation = new ImportOperation(project.getFullPath(), importSource, FileSystemStructureProvider.INSTANCE, parent,
					filesToImport);
				operation.setContext(parent.getShell());
				operation.setOverwriteResources(true);
				operation.setCreateContainerStructure(false);
				operation.run(monitor);
			}
			return project;
		} catch (final CoreException e) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					IDEWorkbenchPlugin.log(e.getMessage());
					MessageBox errorDialog = new MessageBox(parent.getShell(), SWT.ERROR);
					errorDialog.setText("Import Failed");
					errorDialog.setMessage("Import Failed for the following reason: \n" + e.getMessage());
					errorDialog.open();
				}
			});
		} catch (InvocationTargetException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		} catch (InterruptedException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected WaveDevSettings createWaveDevFile() throws CoreException {
		SoftPkg softPkg = getSoftPkg(record.projectSystemFile.getAbsolutePath());
		return createWaveDevFile(record, projectName, softPkg);
	}

	public SoftPkg getSoftPkg(String path) {
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = set.getResource(org.eclipse.emf.common.util.URI.createFileURI(path), true);
		return SoftPkg.Util.getSoftPkg(resource);
	}
}
