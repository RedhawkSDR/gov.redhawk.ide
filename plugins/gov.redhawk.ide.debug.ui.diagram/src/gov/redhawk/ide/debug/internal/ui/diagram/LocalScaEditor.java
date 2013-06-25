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
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.ui.diagram.LocalScaDiagramPlugin;
import gov.redhawk.ide.sad.internal.ui.editor.SadEditor;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.sad.diagram.part.SadDiagramEditor;
import gov.redhawk.sca.util.Debug;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class LocalScaEditor extends SadEditor {
	private static final Debug DEBUG = new Debug(LocalScaDiagramPlugin.PLUGIN_ID, "editor");
	private ScaModelAdapter scaListener;

	private SadModelAdapter sadlistener;

	@Override
	protected void createModel() {
		super.createModel();
		
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final LocalScaWaveform waveform = localSca.getSandboxWaveform();
		if (waveform == null) {
			localSca.eAdapters().add(new AdapterImpl() {
				@Override
				public void notifyChanged(Notification msg) {
					switch (msg.getFeatureID(LocalSca.class)) {
					case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
						if (msg.getNewValue() instanceof ScaWaveform) {
							initModelMap();
							localSca.eAdapters().remove(this);
						}
						break;
					default:
						break;
					}

				}

			});
		} else {
			initModelMap();
		}



	}

	private void initModelMap() {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final LocalScaWaveform waveform = localSca.getSandboxWaveform();
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(getMainResource());
		final ModelMap modelMap = new ModelMap(this, sad, waveform);
		this.sadlistener = new SadModelAdapter(modelMap);
		sad.eAdapters().add(this.sadlistener);
		this.scaListener = new ScaModelAdapter(modelMap);
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			public void execute() {
				localSca.eAdapters().add(LocalScaEditor.this.scaListener);
			}
		});
		getEditingDomain().getCommandStack().execute(new SadModelInitializerCommand(modelMap, sad, waveform));
		getEditingDomain().getCommandStack().flush();
		
		if (LocalScaEditor.DEBUG.enabled) {
			try {
				sad.eResource().save(null);
			} catch (final IOException e) {
				LocalScaEditor.DEBUG.catching("Failed to save local diagram.", e);
			}
		}
	}

	@Override
	public void dispose() {
		if (this.sadlistener != null) {
			final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(getMainResource());
			if (sad != null) {
				sad.eAdapters().remove(this.sadlistener);
			}
			this.sadlistener = null;
		}
		if (this.scaListener != null) {
			final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
			ScaModelCommand.execute(localSca, new ScaModelCommand() {

				public void execute() {
					localSca.eAdapters().remove(LocalScaEditor.this.scaListener);
				}
			});
			this.scaListener = null;
		}
		super.dispose();
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		doSaveAs();
	}

	@Override
	protected void addPages() {
		// Only creates the other pages if there is something that can be edited
		//
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
		    && !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource sadResource = getMainResource();

				final SadDiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput input = createDiagramInput(sadResource);
				pageIndex = addPage(editor, input);
				setPageText(pageIndex, "Diagram");

				getEditingDomain().getCommandStack().removeCommandStackListener(getCommandStackListener());

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				    StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				    StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				    StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}

	@Override
	public List<Object> getOutlineItems() {
		return Collections.emptyList();
	}

	@Override
	protected SadDiagramEditor createDiagramEditor() {
		return new SandboxDiagramEditor(this);
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

	@Override
	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public String getTitle() {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final LocalScaWaveform waveform = localSca.getSandboxWaveform();
		String name = "Chalkboard";
		if (waveform != null && waveform.getName() != null) {
			name = waveform.getName();
		}
		return name;
	}

	@Override
	public String getTitleToolTip() {
		return "";
	}
}
