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

import gov.redhawk.ide.graphiti.dcd.internal.ui.DcdGraphitiModelAdapter;
import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMap;
import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMapInitializerCommand;
import gov.redhawk.ide.graphiti.dcd.internal.ui.ScaGraphitiModelAdapter;
import gov.redhawk.ide.graphiti.dcd.ui.adapters.GraphitiDcdDiagramAdapter;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

public class GraphitiDcdExplorerEditor extends GraphitiDcdMultipageEditor {

	private ScaDeviceManager deviceManager;
	private ScaGraphitiModelAdapter scaListener;
	private DcdGraphitiModelAdapter dcdListener;
	private GraphitiDcdDiagramAdapter graphitiDiagramListener;
	private DeviceConfiguration dcd;
	private GraphitiDcdModelMap modelMap;

	public GraphitiDcdExplorerEditor() {
	}

	@Override
	public String getDiagramContext(Resource sadResource) {
		return DUtil.DIAGRAM_CONTEXT_EXPLORER;
	}

	@Override
	protected void createModel() {
		// TODO: Local Devices are currently broken in both GMF and Graphiti
//		if (isLocalSca) {
//			mainResource = getEditingDomain().getResourceSet().createResource(ScaDebugInstance.getLocalSandboxWaveformURI());
//			dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
//			getEditingDomain().getCommandStack().execute(new ScaModelCommand() {
//
//				@Override
//				public void execute() {
//					mainResource.getContents().add(dcd);
//				}
//			});
//		} else {
		super.createModel();
		dcd = DeviceConfiguration.Util.getDeviceConfiguration(super.getMainResource());
//		}

		initModelMap();
	}

	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof ScaFileStoreEditorInput) {
			ScaFileStoreEditorInput scaInput = (ScaFileStoreEditorInput) input;
			if (scaInput.getScaObject() instanceof ScaDeviceManager) {
				deviceManager = (ScaDeviceManager) scaInput.getScaObject();
			} else {
				throw new IllegalStateException("Node Explorer opened on invalid sca input " + scaInput.getScaObject());
			}
		} else {
			throw new IllegalStateException("Node Explorer opened on invalid input " + input);
		}

		// TODO: Local Devices are currently broken in both GMF and Graphiti
//		if (ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager() == deviceManager || this.deviceManager == null) {
//			isLocalSca = true;
//		}

		super.setInput(input);
	}

	// TODO: Not currently used, this may be needed if we determine that we need to be setting a LocalScaDeviceManager
	// as input, instead of a ScaDeviceManager
//	private LocalScaDeviceManager getLocalScaDeviceManager(final ScaDeviceManager remoteDeviceManager) {
//		LocalScaDeviceManager proxy = null;
//		final LocalScaDeviceManager tempDeviceManager;
//		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
//		if (remoteDeviceManager.equals(localSca.getSandboxDeviceManager())) {
//			proxy = localSca.getSandboxDeviceManager();
//			return proxy;
//		}
//
//		tempDeviceManager = ScaDebugFactory.eINSTANCE.createLocalScaDeviceManager();
//
//		final NotifyingNamingContext rootContext = localSca.getRootContext();
//		final NotifyingNamingContext context;
//		try {
//			context = LocalApplicationFactory.createWaveformContext(rootContext, remoteDeviceManager.getIdentifier());
//		} catch (CoreException e) {
//			throw new IllegalStateException("Failed to create device manager naming context", e);
//		}
//
//		tempDeviceManager.setNamingContext(context);
//		tempDeviceManager.setProfile(remoteDeviceManager.getProfile());
//		final DeviceManagerImpl impl = new DeviceManagerImpl(remoteDeviceManager.getProfile(), remoteDeviceManager.getIdentifier(),
//			remoteDeviceManager.getLabel(), tempDeviceManager, remoteDeviceManager.getFileSystem().getObj());
//		tempDeviceManager.setLocalDeviceManager(impl);
//		tempDeviceManager.setProfileURI(remoteDeviceManager.getProfileURI());
////		tempDeviceManager.setProfileObj(remoteDeviceManager.getProfileObj());
//
//		// TODO: this is probably wrong...
//		if (Display.getCurrent() != null) {
//			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
//			try {
//				dialog.run(true, true, new IRunnableWithProgress() {
//
//					@Override
//					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//						try {
//							CorbaUtils.invoke(new Callable<Object>() {
//
//								@Override
//								public Object call() throws Exception {
//									NameComponent[] name = Name.toName(impl.label());
//									context.getNamingContext().bind(name, impl.getDevMgr().getCorbaObj());
//									return null;
//								}
//							}, monitor);
//						} catch (CoreException e1) {
//							throw new InvocationTargetException(e1);
//						}
//					}
//				});
//			} catch (InvocationTargetException e) {
//				throw new IllegalStateException("Failed to bind device", e);
//			} catch (InterruptedException e) {
//				// PASS
//			}
//		} else {
//			try {
//				NameComponent[] name = Name.toName(impl.label());
//				context.bind(name, impl.getDevMgr().getCorbaObj());
//			} catch (Exception e) { // SUPPRESS CHECKSTYLE INLINE
//				throw new IllegalStateException("Failed to bind device", e);
//			}
//		}
//
//		// Create local copy to pass into the SCA model command
//		final LocalScaDeviceManager tempLocalScaDeviceManager = tempDeviceManager;
//		ScaModelCommand.execute(remoteDeviceManager, new ScaModelCommand() {
//
//			@Override
//			public void execute() {
//				remoteDeviceManager.eAdapters().add(new AdapterImpl() {
//
//					@Override
//					public void notifyChanged(Notification msg) {
//						switch (msg.getFeatureID(ScaDeviceManager.class)) {
//						case ScaPackage.SCA_DEVICE_MANAGER__DISPOSED:
//							if (msg.getNewBooleanValue()) {
//								tempLocalScaDeviceManager.dispose();
//							}
//							break;
//						default:
//							break;
//						}
//						super.notifyChanged(msg);
//					}
//				});
//
//			}
//		});
//
//		return tempDeviceManager;
//	}

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

		// TODO: Local Devices are currently broken in both GMF and Graphiti
//		if (isLocalSca) {
//			// Use the SCA Model are source to build the SAD when we are in the chalkboard since the SAD file isn't
//			// modified
//			getEditingDomain().getCommandStack().execute(new SadGraphitiModelInitializerCommand(modelMap, sad, waveform));
//		} else {
		// Use the existing DCD file as a template when initializing the modeling map
		getEditingDomain().getCommandStack().execute(new GraphitiDcdModelMapInitializerCommand(modelMap, dcd, deviceManager));
//		}
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
									getEditorSite().getPage().closeEditor(GraphitiDcdExplorerEditor.this, false);
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

		// TODO: Don't need to do this for explorer, but add it back once we get the Node Chalkboard going
//		if (GraphitiDcdExplorerEditor.DEBUG.enabled) {
//			try {
//				dcd.eResource().save(null);
//			} catch (final IOException e) {
//				GraphitiDcdExplorerEditor.DEBUG.catching("Failed to save local diagram.", e);
//			}
//		}
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
					deviceManager.eAdapters().remove(GraphitiDcdExplorerEditor.this.scaListener);
				}
			});
			this.scaListener = null;
		}
		super.dispose();
	}

	@Override
	protected void addPages() {
		super.addPages();

		// register the graphitiDiagramListener
		this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram().eAdapters().add(graphitiDiagramListener);

		// make sure diagram elements reflect current runtime state
		this.modelMap.reflectRuntimeStatus();
	}
}
