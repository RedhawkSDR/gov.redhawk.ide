/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.codegen.ui.internal.job;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.ide.codegen.jet.SadTemplateParameter;
import gov.redhawk.ide.codegen.jet.TopLevelSadRpmSpecTemplate;
import gov.redhawk.ide.codegen.jet.WaveformAdminServiceConfigIni;
import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.SaveXmlUtils;
import gov.redhawk.ide.codegen.ui.utils.DocumentationUtils;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class SadCodegenJob extends WorkspaceJob {

	private SoftwareAssembly sad;
	private IProject project;
	private List<String> filesToGenerate;

	private String diagramFileName;
	private String specFileName;
	private String iniFileName;

	public SadCodegenJob(SoftwareAssembly sad, IProject project) {
		super("Performing code generation for " + sad.getName() + "...");
		this.sad = sad;
		this.project = project;

		setUser(true);
		computeFileNames();
	}

	public void setFilesToGenerate(List<String> filesToGenerate) {
		this.filesToGenerate = filesToGenerate;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, 1 + filesToGenerate.size());

		// Get the header, if any
		String headerContent = DocumentationUtils.getHeaderContents(project);

		// Update the SAD file's header
		IStatus status = updateSadFileHeader(sad, headerContent, progress.newChild(1));
		if (!status.isOK()) {
			return status;
		}

		SadTemplateParameter params = new SadTemplateParameter(sad, headerContent);

		// Determine what files (other than the SAD file) get installed
		for (String otherFileName : Arrays.asList(diagramFileName, iniFileName)) {
			IFile otherFile = project.getFile(new Path(otherFileName));
			if (otherFile.exists() || filesToGenerate.contains(otherFileName)) {
				params.addFileToInstall(otherFileName);
			}
		}

		// Generate the RPM spec file
		if (filesToGenerate.contains(specFileName)) {
			updateSpecFile(specFileName, params, progress.newChild(1));
		}

		// Generate the INI file
		if (filesToGenerate.contains(iniFileName)) {
			updateIniFile(iniFileName, params, progress.newChild(1));
		}

		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return GenerateCode.FAMILY_GENERATE_CODE == family;
	}

	private void computeFileNames() {
		String baseName;
		if (sad.getName().indexOf('.') == -1) {
			baseName = sad.getName();
		} else {
			baseName = sad.getName().substring(sad.getName().lastIndexOf('.') + 1);
		}

		diagramFileName = baseName + SadPackage.DIAGRAM_FILE_EXTENSION;
		specFileName = sad.getName() + ".spec";
		iniFileName = sad.getName() + ".ini";
	}

	private IStatus updateSadFileHeader(SoftwareAssembly sad, String headerContent, SubMonitor progress) {
		EditingDomain editingDomain = TransactionUtil.getEditingDomain(sad);
		SadDocumentRoot docRoot = ScaEcoreUtils.getEContainerOfType(sad, SadDocumentRoot.class);

		// We make the changes in the UI thread because otherwise there are asynchronous changes that have to
		// propagate to the UI thread (so that the EMF changes can update the XML editor's page).
		RunnableWithResult<Void> runnable = new RunnableWithResult.Impl<Void>() {
			@Override
			public void run() {
				if (!DocumentationUtils.setXMLCommentHeader(editingDomain, docRoot.getMixed(), headerContent)) {
					setStatus(Status.OK_STATUS);
					return;
				}

				try {
					SaveXmlUtils.save(sad);
				} catch (CoreException e) {
					setStatus(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save changes to XML", e));
					return;
				}

				setStatus(Status.OK_STATUS);
			}
		};
		PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
		return runnable.getStatus();
	}

	private void updateSpecFile(String fileName, SadTemplateParameter params, IProgressMonitor monitor) throws CoreException {
		final TopLevelSadRpmSpecTemplate template = new TopLevelSadRpmSpecTemplate();
		final byte[] fileContents = template.generate(params).getBytes();
		write(project.getFile(fileName), fileContents, monitor);
	}

	private void updateIniFile(String fileName, SadTemplateParameter params, IProgressMonitor monitor) throws CoreException {
		final WaveformAdminServiceConfigIni template = new WaveformAdminServiceConfigIni();
		final byte[] fileContents = template.generate(params).getBytes();
		write(project.getFile(fileName), fileContents, monitor);
	}

	private void write(IFile targetFile, byte[] fileContents, IProgressMonitor monitor) throws CoreException {
		if (targetFile.exists()) {
			targetFile.setContents(new ByteArrayInputStream(fileContents), true, false, monitor);
		} else {
			targetFile.create(new ByteArrayInputStream(fileContents), true, monitor);
		}
	}
}
