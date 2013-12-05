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

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.java.JavaGeneratorUtils;
import gov.redhawk.ide.codegen.manual.ManualGeneratorPlugin;
import gov.redhawk.ide.cplusplus.utils.CppGeneratorUtils;
import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;

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

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class RedhawkImportUtil {
	private static String sadExtension = ".+\\.sad.xml";
	private static String spdExtension = ".+\\.spd.xml";
	private static String dcdExtension = ".+\\.dcd.xml";

	private ProjectRecord record;
	private IProgressMonitor monitor;
	private RedhawkImportWizardPage1 parent;
	private URI projectLocation;
	private String projectName;
	private int result;

	private boolean dotProjectMissing = true;
	private boolean wavedevMissing = true;
	private boolean copyFiles;

	public static String getName(IPath path) throws IOException,
			XMLStreamException {
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
				if ("deviceconfiguration".equals(element)
						|| "softwareassembly".equals(element)
						|| "softpkg".equals(element)) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement
							.getAttributes();
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
		// If there is there is no 'name' attribute then use the file name as
		// the project name
		projectName = path.toFile().getName();
		int dotIndex = projectName.indexOf('.');
		projectName = projectName.substring(0, dotIndex);
		return projectName;

	}

	public void createMissingFiles(ProjectRecord record,
			IProgressMonitor monitor, boolean copyFiles,
			RedhawkImportWizardPage1 parent) {
		this.record = record;
		this.monitor = monitor;
		this.projectName = record.projectName;
		this.copyFiles = copyFiles;
		this.parent = parent;
		this.projectLocation = record.projectSystemFile.getParentFile()
				.getAbsoluteFile().toURI();
		String type = this.record.projectSystemFile.getName();

		if (findMissingFiles() == SWT.CANCEL) {
			// User canceled import because of missing source directories
			// Don't create files
			return;
		}

		try {
			if (type.matches(sadExtension) && dotProjectMissing) {
				createDotProjectFile("SAD");
			}
			if (type.matches(dcdExtension) && dotProjectMissing) {
				createDotProjectFile("DCD");
			}
			if (type.matches(spdExtension)) {
				if (dotProjectMissing) {
					createDotProjectFile("SPD");
				}
				if (wavedevMissing) {
					createWavDevFile();
				}
			}
		} catch (CoreException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
	}

	private int findMissingFiles() {
		boolean hasSource = false;
		File[] contents = record.projectSystemFile.getParentFile().listFiles();
		for (File f : contents) {
			String name = f.getName();
			// check for source directories
			if ("cpp".equals(name) || "java".equals(name)
					|| "python".equals(name)) {
				hasSource = true;
			}

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

		if (!hasSource) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageBox dialog = new MessageBox(parent.getShell(),
							SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					dialog.setText("Error - No Source Directory");
					dialog.setMessage("Warning: \nNo source files found in the specified location.  "
							+ "\nImported projects will not function correctly");
					result = dialog.open();
				}
			});
		}
		
		result = SWT.OK;
		return result;
	}

	private IProject createDotProjectFile(String projectType) {
		try {
			IProject project = null;
			File importSource = new File(record.projectSystemFile
					.getParentFile().getAbsolutePath());

			// Build new .project files of the appropriate type
			if ("SAD".equals(projectType)) {
				if (!copyFiles) {
					project = WaveformProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = WaveformProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}
			if ("DCD".equals(projectType)) {
				if (!copyFiles) {
					project = NodeProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = NodeProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}
			if ("SPD".equals(projectType)) {
				if (!copyFiles) {
					project = ComponentProjectCreator.createEmptyProject(
							projectName, projectLocation, monitor);
				} else {
					project = ComponentProjectCreator.createEmptyProject(
							projectName, null, monitor);
				}
			}

			// Add java natures if required
			if (new File(importSource + "/java").exists()) {
				JavaGeneratorUtils.addJavaProjectNature(project, monitor);
			}

			// Add CPP natures if required
			if (new File(importSource + "/cpp").exists()) {
				MultiStatus retStatus = new MultiStatus(
						ManualGeneratorPlugin.PLUGIN_ID, IStatus.OK, "", null);
				CppGeneratorUtils.addCandCPPNatures(project,
						SubMonitor.convert(monitor), retStatus);
				CppGeneratorUtils.addManagedNature(project,
						SubMonitor.convert(monitor), retStatus, "/",
						System.out, true, null);
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
			return project;
		} catch (final CoreException e) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					IDEWorkbenchPlugin.log(e.getMessage());
					MessageBox errorDialog = new MessageBox(parent.getShell(),
							SWT.ERROR);
					errorDialog.setText("Import Failed");
					errorDialog
							.setMessage("Import Failed for the following reason: \n"
									+ e.getMessage());
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

	@SuppressWarnings({ "deprecation" })
	private void createWavDevFile() throws CoreException {
		// creates the missing wavedev file
		final SoftPkg softPkg = getSoftPkg(record.projectSystemFile
				.getAbsolutePath());
		WaveDevSettings waveDev = CodegenFactory.eINSTANCE
				.createWaveDevSettings();

		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			final ImplementationSettings settings = CodegenFactory.eINSTANCE
					.createImplementationSettings();
			final String lang = impl.getProgrammingLanguage().getName();
			// Find the code generator if specified, otherwise pick the first
			// one returned by the registry
			ICodeGeneratorDescriptor codeGenDesc = null;
			final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator
					.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
			if (codeGens.length > 0) {
				codeGenDesc = codeGens[0];
			}
			if (codeGenDesc != null) {
				final IScaComponentCodegen generator = codeGenDesc
						.getGenerator();

				// Assume that there is <name>[/].+<other> format for the
				// entrypoint
				// Pick out <name> for both the output dir and settings name
				final String lf = impl.getCode().getEntryPoint();
				final String name = lf.substring(0, lf.indexOf('/'));

				// Set the generator, settings name and output directory
				settings.setGeneratorId(generator.getClass().getCanonicalName());
				settings.setName(name);
				settings.setOutputDir(lf.substring(0, lf.lastIndexOf('/')));

				// pick the first selectable and defaultable template returned
				// by the registry
				ITemplateDesc templateDesc = null;
				final ITemplateDesc[] templates = RedhawkCodegenActivator
						.getCodeGeneratorTemplatesRegistry()
						.findTemplatesByCodegen(settings.getGeneratorId());
				for (final ITemplateDesc itd : templates) {
					if (itd.isSelectable() && !itd.notDefaultableGenerator()) {
						templateDesc = itd;
						break;
					}
				}
				// If we found the template, use it
				if (templateDesc != null) {
					// Set the properties to their default values
					for (final IPropertyDescriptor prop : templateDesc
							.getPropertyDescriptors()) {
						final Property p = CodegenFactory.eINSTANCE
								.createProperty();
						p.setId(prop.getKey());
						p.setValue(prop.getDefaultValue());
						settings.getProperties().add(p);
					}
					// Set the template
					if ("C++".equals(lang)) {
						if (record.cppImplTemplate != null) {
							settings.setTemplate(record.cppImplTemplate);
						} else {
							settings.setTemplate("redhawk.codegen.jinja.cpp.component.pull");
						}
					} else if ("Java".equals(lang)) {
						if (record.javaImplTemplate != null) {
							settings.setTemplate(record.javaImplTemplate);
						} else {
							settings.setTemplate("redhawk.codegen.jinja.java.component.pull");
						}
					} else if ("Python".equals(lang)) {
						if (record.pythonImplTemplate != null) {
							settings.setTemplate(record.pythonImplTemplate);
						} else {
							settings.setTemplate("redhawk.codegen.jinja.python.component.pull");
						}
					} else {
						settings.setTemplate(templateDesc.getId());
					}
				}
			}

			// If a java implementation is found
			if ("Java".equals(lang)) {
				boolean hasUseJni = false;
				EList<Property> properties = settings.getProperties();
				for (Property prop : properties) {
					// Validate java_package name and create a default one if
					// necessary
					if ("java_package".equals(prop.getId())) {
						if (prop.getValue() == null
								|| prop.getValue().isEmpty()) {
							prop.setValue(projectName + ".java");
						}
					}
					// Check for use_jni and populate if it is found but empty
					if ("use_jni".equals(prop.getId())) {
						hasUseJni = true;
						if (prop.getValue() == null
								|| prop.getValue().isEmpty()) {
							prop.setValue("TRUE");
						}
					}
				}
				// if use_jni is not found, build it with TRUE as default
				if (!hasUseJni) {
					final Property useJni = CodegenFactory.eINSTANCE
							.createProperty();
					useJni.setId("use_jni");
					useJni.setValue("TRUE");
					settings.getProperties().add(useJni);
				}
			}
			// Save the created settings
			waveDev.getImplSettings().put(impl.getId(), settings);
		}
		// Create the URI to the .wavedev file
		final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createPlatformResourceURI(
						softPkg.getName() + "/." + softPkg.getName()
								+ ".wavedev", false);
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final Resource res = set.createResource(uri);

		// Add the WaveDevSettings to the resource and save to disk to persist
		// the newly created WaveDevSettings
		res.getContents().add(waveDev);
		try {
			res.save(null);
		} catch (final IOException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
	}

	public SoftPkg getSoftPkg(String path) {
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = set.getResource(
				org.eclipse.emf.common.util.URI.createFileURI(path), true);
		return SoftPkg.Util.getSoftPkg(resource);
	}
}
