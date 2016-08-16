/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

import gov.redhawk.core.graphiti.dcd.ui.editor.GraphitiDeviceManagerExplorerEditor;
import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiDiagramEditor;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMapInitializerCommand;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.GraphitiDeviceManagerSandboxDiagramEditor;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * The multi-page sandbox editor for device managers ({@link ScaDeviceManager}). Includes a Graphiti diagram.
 */
public class GraphitiDeviceManagerSandboxEditor extends GraphitiDeviceManagerExplorerEditor {

	public static final String EDITOR_ID = "gov.redhawk.ide.graphiti.dcd.ui.editor.DcdSandbox";

	private Resource mainResource;
	private boolean isSandboxDeviceManager = false;

	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof URIEditorInput) {
			URIEditorInput uriInput = (URIEditorInput) input;
			if (!uriInput.getURI().equals(ScaDebugInstance.getLocalSandboxDeviceManagerURI())) {
				throw new IllegalStateException("Device manager sandbox editor opened with invalid input: " + uriInput.getURI());
			}

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				dialog.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Starting Sandbox...", IProgressMonitor.UNKNOWN);
						try {
							LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(monitor);
							setDeviceManager(localSca.getSandboxDeviceManager());
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
						monitor.done();
					}

				});
			} catch (InvocationTargetException e) {
				throw new IllegalStateException("Failed to setup sandbox", e);
			} catch (InterruptedException e) {
				throw new IllegalStateException("Sandbox setup canceled, can not load editor.");
			}

			if (getDeviceManager() == null) {
				throw new IllegalStateException("Failed to setup sandbox, null sandbox device manager.");
			}
			isSandboxDeviceManager = true;
		}

		super.setInput(input);
	}

	@Override
	public String getDiagramContext(Resource dcdResource) {
		return DUtil.DIAGRAM_CONTEXT_LOCAL;
	}

	@Override
	public Resource getMainResource() {
		return (isSandboxDeviceManager) ? mainResource : super.getMainResource();
	}

	@Override
	protected void createModel() {
		if (isSandboxDeviceManager) {
			mainResource = getEditingDomain().getResourceSet().createResource(ScaDebugInstance.getLocalSandboxDeviceManagerURI());
			final DeviceConfiguration dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
			getEditingDomain().getCommandStack().execute(new ScaModelCommand() {
				@Override
				public void execute() {
					mainResource.getContents().add(dcd);
				}
			});
		} else {
			super.createModel();
		}
	}

	@Override
	protected AbstractGraphitiDiagramEditor createDiagramEditor() {
		return new GraphitiDeviceManagerSandboxDiagramEditor(getEditingDomain());
	}

	@Override
	protected Command createModelInitializeCommand() {
		if (isSandboxDeviceManager) {
			DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(super.getMainResource());
			return new GraphitiDcdModelMapInitializerCommand(getModelMap(), dcd, (LocalScaDeviceManager) getDeviceManager());
		} else {
			return super.createModelInitializeCommand();
		}
	}
}
