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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

import gov.redhawk.core.graphiti.sad.ui.editor.GraphitiModelMap;
import gov.redhawk.core.graphiti.sad.ui.editor.GraphitiWaveformExplorerEditor;
import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiDiagramEditor;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.debug.internal.ui.diagram.NewWaveformFromLocalWizard;
import gov.redhawk.ide.graphiti.sad.debug.internal.ui.GraphitiSADLocalModelMap;
import gov.redhawk.ide.graphiti.sad.debug.internal.ui.SadGraphitiModelInitializerCommand;
import gov.redhawk.ide.graphiti.sad.ui.diagram.GraphitiWaveformSandboxDiagramEditor;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * The multi-page sandbox editor for waveforms ({@link ScaWaveform}). Includes a Graphiti diagram.
 */
public class GraphitiWaveformSandboxEditor extends GraphitiWaveformExplorerEditor {

	public static final String EDITOR_ID = "gov.redhawk.ide.graphiti.sad.ui.editor.localMultiPageSca";

	private Resource mainResource;
	private boolean isSandboxChalkboardWaveform = false;
	private GraphitiModelMap modelMap;

	@Override
	protected GraphitiModelMap createModelMapInstance() {
		modelMap = new GraphitiSADLocalModelMap(this, getWaveform());
		return modelMap;
	}

	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof URIEditorInput) {
			URIEditorInput uriInput = (URIEditorInput) input;
			if (!uriInput.getURI().equals(ScaDebugInstance.getLocalSandboxWaveformURI())) {
				throw new IllegalStateException("Waveform sandbox editor opened with invalid input: " + uriInput.getURI());
			}

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				dialog.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Starting Sandbox...", IProgressMonitor.UNKNOWN);
						try {
							LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(monitor);
							setWaveform(localSca.getSandboxWaveform());
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}

					}
				});
			} catch (InvocationTargetException e) {
				throw new IllegalStateException("Failed to setup sandbox", e);
			} catch (InterruptedException e) {
				throw new IllegalStateException("Sandbox setup canceled, can not load editor.");
			}

			if (getWaveform() == null) {
				throw new IllegalStateException("Failed to setup sandbox, null sandbox chalkboard.");
			}
			isSandboxChalkboardWaveform = true;
		}

		super.setInput(input);
	}

	@Override
	public String getDiagramContext(Resource sadResource) {
		return DUtil.DIAGRAM_CONTEXT_LOCAL;
	}

	@Override
	public Resource getMainResource() {
		return (isSandboxChalkboardWaveform) ? mainResource : super.getMainResource();
	}

	@Override
	protected void createModel() {
		if (isSandboxChalkboardWaveform) {
			mainResource = getEditingDomain().getResourceSet().createResource(ScaDebugInstance.getLocalSandboxWaveformURI());
			final SoftwareAssembly sad = SadFactory.eINSTANCE.createSoftwareAssembly();
			getEditingDomain().getCommandStack().execute(new ScaModelCommand() {
				@Override
				public void execute() {
					mainResource.getContents().add(sad);
				}
			});
		} else {
			super.createModel();
		}
	}

	@Override
	protected AbstractGraphitiDiagramEditor createDiagramEditor() {
		return new GraphitiWaveformSandboxDiagramEditor(getEditingDomain());
	}

	@Override
	protected Command createModelInitializeCommand() {
		if (isSandboxChalkboardWaveform) {
			SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(super.getMainResource());
			return new SadGraphitiModelInitializerCommand(modelMap, sad, (LocalScaWaveform) getWaveform());
		} else {
			return super.createModelInitializeCommand();
		}
	}

	@Override
	public void doSaveAs() {
		final NewWaveformFromLocalWizard wizard = new NewWaveformFromLocalWizard(SoftwareAssembly.Util.getSoftwareAssembly(getMainResource()));
		final WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
		dialog.open();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
}
