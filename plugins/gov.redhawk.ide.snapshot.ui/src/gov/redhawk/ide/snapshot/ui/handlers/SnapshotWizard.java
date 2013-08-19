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
package gov.redhawk.ide.snapshot.ui.handlers;

import gov.redhawk.ide.snapshot.datareceiver.AbstractDataReceiverAttributes;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiverFactory;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotRunnable;
import gov.redhawk.ide.snapshot.ui.SnapshotActivator;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.statushandlers.StatusManager;

import BULKIO.StreamSRI;
import CF.ResourcePackage.StartError;

public class SnapshotWizard extends Wizard {

	private SnapshotWizardPage snapshotPage;
	private ArrayList<IDataReceiverFactory> receivers = new ArrayList<IDataReceiverFactory>();
	private ScaUsesPort port;
	private Object[] datalist;
	private StreamSRI sri;

	public SnapshotWizard(ScaUsesPort port) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Snapshot");
		this.port = port;
	}

	/**
	 * This constructor allows the snapshot to be used as a data dump into a file,
	 * the capture method will be set to the number of samples in the array
	 * @param port : the port the data is coming from
	 * @param datalist : an array of data collected already (Integer[], Double[], ...)
	 * @param sri : the sri of the data
	 */
	public SnapshotWizard(ScaUsesPort port, Object[] datalist, StreamSRI sri) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Snapshot");
		this.port = port;
		this.datalist = datalist;
		this.sri = sri;
	}

	@Override
	public void addPages() {

		//Checking if the Component/Waveform housing the port has been started and getting its name
		final Object container = port.eContainer();
		boolean notifyUser = false;
		String name = "";
		if (container instanceof ScaComponent) {
			if (!((ScaComponent) container).getStarted()) {
				notifyUser = true;
				name = ((ScaComponent) container).getName();
			}
		} else if (container instanceof ScaWaveform) {
			if (!((ScaWaveform) container).getStarted()) {
				notifyUser = true;
				name = ((ScaWaveform) container).getName();
			}
		}

		//if the component not started, ask the user if he or she wants to start it
		if (notifyUser) {
			MessageBox shouldStart = new MessageBox(this.getShell(), SWT.YES | SWT.NO | SWT.CANCEL);
			shouldStart.setMessage(name + " has not been started, would you like to start it now?");
			int result = shouldStart.open();
			if (result == SWT.CANCEL) {
				throw new OperationCanceledException();
			} else if (result == SWT.YES) {
				//start the component/waveform
				Job startComponent = new Job("Starting Component") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							if (container instanceof ScaComponent) {
								((ScaComponent) container).start();
							} else if (container instanceof ScaWaveform) {
								((ScaWaveform) container).start();
							}
						} catch (StartError e) {
							return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to start component/waveform", e.getCause());
						}
						return Status.OK_STATUS;
					}
				};
				startComponent.schedule();
			}
		}

		//load the extensions for data receivers
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor("gov.redhawk.ide.snapshot.datareceiver");
//		String[] types = new String[extensions.length + 1]; // +1 since we always have capture to binary/raw file  
		receivers.add(BinDataReceiver.getInstance()); // TODO:
		
String[] types = new String[1]; // 
		types[0] = receivers.get(0).getReceiverName();
		for (int i = 0; i < extensions.length; i++) {
			try {
				Object temp = extensions[i].createExecutableExtension("class");
				if (temp instanceof IDataReceiverFactory) {
					receivers.add((AbstractDataReceiverAttributes) temp);
					types[i + 1] = ((IDataReceiverFactory) temp).getReceiverName();
				}
			} catch (CoreException e) {
				//PASS
			}
		}

		//load the capture types
		CaptureMethod[] methods = CaptureMethod.values();
		String[] captureMethods = new String[methods.length];
		for (int i = 0; i < methods.length; i++) {
			captureMethods[i] = methods[i].toString();
		}
		if (types == null || types.length == 0 || captureMethods == null || captureMethods.length == 0) {
			//snapshotPage = new SnapshotWizardPage("snapshot", null);
			MessageDialog.openError(this.getShell(), "Snapshot Error", "The snapshot wizard page could not be opened");
			throw new OperationCanceledException();
		} else {
			if (this.datalist != null) {
				long numSamples = datalist.length;
				if (this.sri.subsize > 0 && this.sri.mode == 1) {
					numSamples = datalist.length / 4;
				} else if (this.sri.subsize > 0 || this.sri.mode == 1) {
					numSamples = datalist.length / 2;
				}
				snapshotPage = new SnapshotWizardPage("snapshot", null, types, captureMethods, numSamples);
			} else {
				snapshotPage = new SnapshotWizardPage("snapshot", null, types, captureMethods);
			}
		}
		addPage(snapshotPage);
	}

	@Override
	public boolean performFinish() {
		final SnapshotSettings settings = snapshotPage.getSettings();
		Shell snapshotShell = this.snapshotPage.getShell();
		final Shell parent = this.getShell().getParent().getShell();
		final SnapshotRunnable run; //made out here to allow run to output the written files
		try {
			if (datalist != null) {
				run = new SnapshotRunnable(settings, port, datalist, sri, receivers);
			} else {
				run = new SnapshotRunnable(settings, port, receivers);
			}
		} catch (IllegalArgumentException e) {
			StatusManager.getManager().handle(
				new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to aqquire snapshot.\n" + e.getMessage(), e.getCause()), StatusManager.SHOW);
			return false;
		}
		if (settings.isConfirmOverwrite() && run.checkForSimilarFiles()) {
			MessageBox override = new MessageBox(snapshotShell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			override.setMessage("There are files in this directory that may be overwritten.\n" + "Do you want to proceed?");
			int result = override.open();
			if (result == SWT.NO) {
				return false;
			}
		}
		String componentName = "";
		Object container = port.eContainer();
		if (container instanceof ScaComponent) {
			componentName = ((ScaComponent) container).getName();
		} else if (container instanceof ScaWaveform) {
			componentName = ((ScaWaveform) container).getName();
		}

		try {
			Job runSnapshot;

			runSnapshot = new Job("Snapshot: " + componentName + " Port: " + port.getName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					ISchedulingRule rule = null;
					IResource resource = null;
					// get rule for workspace if needed
					if (!settings.isSaveToWorkspace()) {
						IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();
						resource = ResourcesPlugin.getWorkspace().getRoot().findMember(settings.getFilePath());
						rule = factory.createRule(resource);
					}
					try {
						//run the snapshot
						if (resource != null && rule != null) {
							Job.getJobManager().beginRule(rule, monitor);
							run.run(monitor);
						} else {
							run.run(monitor);
						}
					} catch (InterruptedException e) {
						return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to aqquire snapshot.", e.getCause());
					} catch (InvocationTargetException e) {
						return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to aqquire snapshot.", e.getCause());
					} finally {
						//close workspace rule
						if (rule != null) {
							Job.getJobManager().endRule(rule);
						}
					}
					if (settings.isSaveToWorkspace()) {
						try {
							ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
						} catch (CoreException e) {
							//PASS
						}
					}
					this.setProperty(IProgressConstants.KEEP_PROPERTY, true);
					this.setProperty(IProgressConstants.ACTION_PROPERTY, getSnapshotResultsAction());
					return Status.OK_STATUS;
				}

				protected Action getSnapshotResultsAction() {
					Action dispRes = new Action("View Snapshot Results") {
						@Override
						public void run() {
							MessageBox report = new MessageBox(parent, SWT.OK);
							report.setText("Snapshot Results");
							StringBuffer output = new StringBuffer();
							String[][] files = run.getOutputFiles();
							if (files != null) {
								output.append("The snapshot was written to the following files in \n" + files[0][0] + "\n");
								if (files.length <= 4) {
									for (int i = 1; i < files.length; i++) {
										for (int j = 0; j < files[i].length - 1; j++) {
											output.append(files[i][j] + "\t");
										}
										output.append(files[i][files[i].length - 1] + "\n");
									}
								} else {
									//get the extensions used by the data receiver
									String[] ext = run.getOutputExtensions();
									if (ext != null && files.length > 1 && ext.length > 0) {
										//print the base file name
										output.append("With a base file name of " + files[1][0].substring(0, files[1][0].lastIndexOf(ext[0])) + "\n");
										//print number of extensions
										for (int i = 0; i < ext.length; i++) {
											output.append(files.length - 1 + " " + ext[i] + " files \n");
										}
									}
								}
								report.setMessage(output.toString());
								report.open();
							}
						}
					};
					return dispRes;
				}
			};

			runSnapshot.schedule();
		} catch (IllegalArgumentException e) {
			StatusManager.getManager().handle(
				new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to aqquire snapshot.\n" + e.getMessage(), e.getCause()), StatusManager.SHOW);
			return false;
		}

		//Open the Progress View or show instructions on how to open Progress View
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IProgressConstants.PROGRESS_VIEW_ID);
		} catch (PartInitException e) {
			MessageBox report = new MessageBox(snapshotShell, SWT.OK);
			report.setMessage("The Progress of the Snapshot is displayed in the Progress View\n"
				+ "which can be opened by going to Window > Show View > Other... > General > Progress");
			report.open();
		}
		return true;
	}

}
