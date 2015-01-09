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

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.graphiti.dcd.internal.ui.DcdGraphitiModelAdapter;
import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMap;
import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMapInitializerCommand;
import gov.redhawk.ide.graphiti.dcd.internal.ui.ScaGraphitiModelAdapter;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.adapters.GraphitiDcdDiagramAdapter;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.GraphitiDcdDiagramEditor;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import gov.redhawk.sca.util.Debug;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

@SuppressWarnings("restriction")
public class GraphitiDcdSandboxEditor extends GraphitiDcdMultipageEditor {
	public static final String EDITOR_ID = "gov.redhawk.ide.graphiti.dcd.ui.editor.DcdSandbox";
	private static final Debug DEBUG = new Debug(DCDUIGraphitiPlugin.PLUGIN_ID, "editor");

	private ScaDeviceManager deviceManager;
	private boolean isLocalSca;
	private Resource mainResource;
	private ScaGraphitiModelAdapter scaListener;
	private DcdGraphitiModelAdapter dcdListener;
	private GraphitiDcdDiagramAdapter graphitiDiagramListener;
	private DeviceConfiguration dcd;
	private GraphitiDcdModelMap modelMap;

	public GraphitiDcdSandboxEditor() {
	}

	@Override
	public String getDiagramContext(Resource dcdResource) {
		return DUtil.DIAGRAM_CONTEXT_LOCAL;
	}

	@Override
	protected void createModel() {
		if (isLocalSca) {
			mainResource = getEditingDomain().getResourceSet().createResource(ScaDebugInstance.getLocalSandboxDeviceManagerURI());
			dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
			getEditingDomain().getCommandStack().execute(new ScaModelCommand() {

				@Override
				public void execute() {
					mainResource.getContents().add(dcd);
				}
			});
		} else {
			super.createModel();
			dcd = DeviceConfiguration.Util.getDeviceConfiguration(super.getMainResource());
		}
		initModelMap();
	}

	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof ScaFileStoreEditorInput) {
			// Should only get here if the input is from a domain device
			ScaFileStoreEditorInput scaInput = (ScaFileStoreEditorInput) input;
			if (scaInput.getScaObject() instanceof ScaDeviceManager) {
				deviceManager = (ScaDeviceManager) scaInput.getScaObject();
			} else {
				throw new IllegalStateException("Node Explorer opened on invalid sca input " + scaInput.getScaObject());
			}
		} else if (input instanceof URIEditorInput) {
			// Should only get here if the input is the Sandbox Device Manager
			URIEditorInput uriInput = (URIEditorInput) input;
			if (uriInput.getURI().equals(ScaDebugInstance.getLocalSandboxDeviceManagerURI())) {
				final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
				if (!ScaDebugInstance.INSTANCE.isInit()) {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					try {
						dialog.run(true, true, new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								monitor.beginTask("Starting Chalkboard...", IProgressMonitor.UNKNOWN);
								try {
									ScaDebugInstance.INSTANCE.init(monitor);
								} catch (CoreException e) {
									throw new InvocationTargetException(e);
								}
								monitor.done();
							}

						});
					} catch (InvocationTargetException e1) {
						StatusManager.getManager().handle(new Status(Status.ERROR, ScaDebugPlugin.ID, "Failed to initialize sandbox.", e1),
							StatusManager.SHOW | StatusManager.LOG);
					} catch (InterruptedException e1) {
						// PASS
					}
				}

				this.deviceManager = localSca.getSandboxDeviceManager();
				if (deviceManager == null) {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					try {
						dialog.run(true, true, new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								monitor.beginTask("Starting Sandbox...", IProgressMonitor.UNKNOWN);
								try {
									ScaDebugPlugin.getInstance().getLocalSca(monitor);
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
					this.deviceManager = localSca.getSandboxDeviceManager();
					if (deviceManager == null) {
						throw new IllegalStateException("Failed to setup sandbox, null device manager.", null);
					}
				}
			}

		} else {
			throw new IllegalStateException("Node Explorer opened on invalid input " + input);
		}

		if (ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager() == deviceManager || this.deviceManager == null) {
			isLocalSca = true;
		}

		super.setInput(input);
	}
	
	@Override
	public Resource getMainResource() {
		if (mainResource == null) {
			return super.getMainResource();
		}
		return mainResource;
	}

	private void initModelMap() {
		if (deviceManager == null) {
			throw new IllegalStateException("Can not initialize the Model Map with null local device manager");
		}

		if (dcd == null) {
			throw new IllegalStateException("Can not initialize the Model Map with null dcd");
		}

		if (!deviceManager.isSetDevices()) {

			if (Display.getCurrent() != null) {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
				try {
					dialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							try {
								CorbaUtils.invoke(new Callable<Object>() {

									@Override
									public Object call() throws Exception {
										deviceManager.refresh(monitor, RefreshDepth.FULL);
										return null;
									}

								}, monitor);
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							}
						}
					});
				} catch (InvocationTargetException e) {
					// PASS
				} catch (InterruptedException e) {
					// PASS
				}
			} else {
				try {
					deviceManager.refresh(null, RefreshDepth.FULL);
				} catch (InterruptedException e) {
					// PASS
				}
			}

		}

		modelMap = new GraphitiDcdModelMap(this, dcd, deviceManager);
		getEditingDomain().getCommandStack().execute(new GraphitiDcdModelMapInitializerCommand(modelMap, dcd, deviceManager));
		getEditingDomain().getCommandStack().flush();

		this.graphitiDiagramListener = new GraphitiDcdDiagramAdapter(modelMap);
		this.dcdListener = new DcdGraphitiModelAdapter(modelMap);
		this.scaListener = new ScaGraphitiModelAdapter(modelMap) {
			@Override
			public void notifyChanged(Notification notification) {
				super.notifyChanged(notification);
				if (notification.getNotifier() == deviceManager) {
					if (deviceManager.isDisposed() && !isDisposed()) {
						getEditorSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								if (!isDisposed()) {
									getEditorSite().getPage().closeEditor(GraphitiDcdSandboxEditor.this, false);
								}
							}

						});
					}
				}
			}
		};

		ScaModelCommand.execute(this.deviceManager, new ScaModelCommand() {

			@Override
			public void execute() {
				scaListener.addAdapter(deviceManager);
			}
		});

		dcd.eAdapters().add(this.dcdListener);

		if (GraphitiDcdSandboxEditor.DEBUG.enabled) {
			try {
				dcd.eResource().save(null);
			} catch (final IOException e) {
				GraphitiDcdSandboxEditor.DEBUG.catching("Failed to save local diagram.", e);
			}
		}
	}

	@Override
	public void dispose() {
		if (this.dcdListener != null) {
			if (dcd != null) {
				dcd.eAdapters().remove(this.dcdListener);
			}
			this.dcdListener = null;
		}

		if (this.graphitiDiagramListener != null) {
			if (this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram() != null) {
				this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram().eAdapters().remove(this.graphitiDiagramListener);
			}
			this.graphitiDiagramListener = null;
		}

		if (this.scaListener != null) {
			ScaModelCommand.execute(deviceManager, new ScaModelCommand() {

				@Override
				public void execute() {
					deviceManager.eAdapters().remove(GraphitiDcdSandboxEditor.this.scaListener);
				}
			});
			this.scaListener = null;
		}
		super.dispose();
	}

	@Override
	protected void addPages() {
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource dcdResource = getMainResource();

				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput input = createDiagramInput(dcdResource);
				pageIndex = addPage(editor, input);
				setPageText(pageIndex, "Diagram");
				setPartName("Device Manager");

				getEditingDomain().getCommandStack().removeCommandStackListener(getCommandStackListener());

				// register the graphitiDiagramListener
				this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram().eAdapters().add(graphitiDiagramListener);

				// make sure diagram elements reflect current runtime state
				this.modelMap.reflectRuntimeStatus();

				// set layout for sandbox editors
				DUtil.layout(editor);

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}
	
	@Override
	protected DiagramEditor createDiagramEditor() {
		GraphitiDcdDiagramEditor editor = new GraphitiDcdDiagramEditor((TransactionalEditingDomain) getEditingDomain());
		editor.addContext("gov.redhawk.ide.dcd.graphiti.ui.contexts.sandbox");
		return editor;
	}
}
