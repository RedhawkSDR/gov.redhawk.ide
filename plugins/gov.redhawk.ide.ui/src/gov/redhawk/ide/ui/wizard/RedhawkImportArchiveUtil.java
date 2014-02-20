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
import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class RedhawkImportArchiveUtil extends RedhawkImportUtil {

	private ProjectRecord record;
	private IProgressMonitor monitor;
	private ILeveledImportStructureProvider structureProvider;
	private RedhawkImportWizardPage1 parent;
	private String projectName;
	private int result;
	private List<String> natures = new ArrayList<String>();
	private IProject project; // Holds the generated .project file

	private boolean dotProjectMissing = true;
	private boolean wavedevMissing = true;

	public void createMissingFiles(ProjectRecord record, IProgressMonitor monitor, RedhawkImportWizardPage1 parent,
		ILeveledImportStructureProvider structureProvider) {
		this.record = record;
		this.monitor = monitor;
		this.projectName = record.projectName;
		this.parent = parent;
		this.structureProvider = structureProvider;
		String type = structureProvider.getLabel(record.projectArchiveFile);

		if (findMissingFiles() == SWT.CANCEL) {
			// User canceled import because of missing source directories
			// Don't create files
			return;
		}

		try {
			if (type.matches(getSadExtension()) && dotProjectMissing) {
				createDotProjectFile("SAD");
			}
			if (type.matches(getDcdExtension()) && dotProjectMissing) {
				createDotProjectFile("DCD");
			}
			if (type.matches(getSpdExtension())) {
				if (dotProjectMissing) {
					project = createDotProjectFile("SPD");
				}
				if (wavedevMissing) {
					createWaveDevFile();
				}
			}
		} catch (CoreException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
	}

	protected int findMissingFiles() {
		boolean hasSource = false;

		// Find the parent directory to search for missing files in
		Object root = structureProvider.getRoot();
		Object parentDirectory = findParentInArchive(root);

		// Search for missing files, assume standard folder structure
		List< ? > children = structureProvider.getChildren(parentDirectory);
		Iterator< ? > childrenEnum = children.iterator();
		out: while (childrenEnum.hasNext()) {
			Object child = childrenEnum.next();
			String name = structureProvider.getLabel(child);
			for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
				if (assistant.handlesNature(name)) {
					hasSource = true;
					natures.add(name);
					continue out;
				}
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

	private Object findParentInArchive(Object root) {
		String path = null;
		Object parentDirectory = new Object();
		if (record.projectArchiveFile instanceof TarEntry) {
			path = ((TarEntry) record.projectArchiveFile).getName();
		} else if (record.projectArchiveFile instanceof ZipEntry) {
			path = ((ZipEntry) record.projectArchiveFile).getName();
		}
		String parentDirString = path.substring(0, path.lastIndexOf("/") + 1);

		List< ? > children = structureProvider.getChildren(root);
		Iterator< ? > childrenEnum = children.iterator();
		while (childrenEnum.hasNext()) {
			Object child = childrenEnum.next();
			if (structureProvider.isFolder(child)) {
				// If this object is the parent directory, assign it and break
				if (structureProvider.getFullPath(child).equals(parentDirString)) {
					parentDirectory = child;
					break;
				}

				// Otherwise, recurse down
				findParentInArchive(child);
			}
		}
		return parentDirectory;
	}

	protected IProject createDotProjectFile(String projectType) {
		try {
			IProject dotProject = null;

			// Build new .project files of the appropriate type
			if ("SAD".equals(projectType)) {
				dotProject = WaveformProjectCreator.createEmptyProject(projectName, null, monitor);
			}
			if ("DCD".equals(projectType)) {
				dotProject = NodeProjectCreator.createEmptyProject(projectName, null, monitor);
			}
			if ("SPD".equals(projectType)) {
				dotProject = ComponentProjectCreator.createEmptyProject(projectName, null, monitor);
			}

			setupNatures(dotProject);

			// Import files into workspace
			List< ? > fileSystemObjects = structureProvider.getChildren(record.parent);
			structureProvider.setStrip(record.level);
			ImportOperation operation = new ImportOperation(dotProject.getFullPath(), structureProvider.getRoot(), structureProvider, parent, fileSystemObjects);
			operation.setContext(parent.getShell());
			operation.run(monitor);

			return dotProject;
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

	private void setupNatures(IProject dotProject) throws CoreException {

		for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
			assistant.setupNatures(natures, dotProject, monitor);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void createWaveDevFile() throws CoreException {
		// Locate the SCA file that was just imported into workspace via the createDotProjectFile() method
		String relativePath = structureProvider.getFullPath(record.projectArchiveFile);
		IFile file = project.getFile(relativePath);

		// Build SoftPkg object
		SoftPkg softPkg = null;
		softPkg = getSoftPkg(file.getLocation().toString());

		WaveDevSettings waveDev = CodegenFactory.eINSTANCE.createWaveDevSettings();

		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			final ImplementationSettings settings = CodegenFactory.eINSTANCE.createImplementationSettings();
			final String lang = impl.getProgrammingLanguage().getName();
			// Find the code generator if specified, otherwise pick the first
			// one returned by the registry
			ICodeGeneratorDescriptor codeGenDesc = null;
			final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
			if (codeGens.length > 0) {
				codeGenDesc = codeGens[0];
			}

			if (codeGenDesc != null) {
				final IScaComponentCodegen generator = codeGenDesc.getGenerator();

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
				final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(settings.getGeneratorId());
				for (final ITemplateDesc itd : templates) {
					if (itd.isSelectable() && !itd.notDefaultableGenerator()) {
						templateDesc = itd;
						break;
					}
				}
				// If we found the template, use it
				if (templateDesc != null) {
					// Set the properties to their default values
					for (final IPropertyDescriptor prop : templateDesc.getPropertyDescriptors()) {
						final Property p = CodegenFactory.eINSTANCE.createProperty();
						p.setId(prop.getKey());
						p.setValue(prop.getDefaultValue());
						settings.getProperties().add(p);
					}
					setTemplate(settings, lang, templateDesc);
				}
			}

			setupWaveDev(settings, lang);

			// Save the created settings
			waveDev.getImplSettings().put(impl.getId(), settings);
		}
		// Create the URI to the .wavedev file
		final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createPlatformResourceURI(softPkg.getName() + "/." + softPkg.getName()
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

	private void setupWaveDev(final ImplementationSettings settings, final String lang) {
		for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
			if (assistant.handlesLanguage(lang)) {
				assistant.setupWaveDev(projectName, settings);
				break;
			}
		}
	}

	private void setTemplate(final ImplementationSettings settings, final String lang, ITemplateDesc templateDesc) throws CoreException {
		settings.setTemplate(templateDesc.getId());

		for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
			if (assistant.setTemplate(record, settings, lang, templateDesc)) {
				return;
			}
		}
	}

	@Override
	public SoftPkg getSoftPkg(String path) {
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		URI resourceURI = URI.createFileURI(path);
		Resource resource = set.getResource(resourceURI, true);
		return SoftPkg.Util.getSoftPkg(resource);
	}
}
