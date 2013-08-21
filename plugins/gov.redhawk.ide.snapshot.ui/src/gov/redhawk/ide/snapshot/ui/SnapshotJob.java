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
package gov.redhawk.ide.snapshot.ui;

import gov.redhawk.ide.snapshot.capture.IDataReceiver;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;
import gov.redhawk.sca.util.SubMonitor;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * 
 */
public class SnapshotJob extends WorkspaceJob {

	private final IDataReceiver receiver;
	private final IDataWriter writer;
	private final IDataWriterSettings writerSettings;
	private boolean openProgressOnFinish = true;

	public SnapshotJob(String name, IDataReceiver receiver) {
		super(name);
		this.receiver = receiver;
		this.writer = receiver.getDataWriter();
		this.writerSettings = this.writer.getSettings();
		setProperty(IProgressConstants.KEEP_PROPERTY, true);
		setProperty(IProgressConstants.ACTION_PROPERTY, getSnapshotResultsAction());
		if (this.writerSettings.getDestination() instanceof IFile) {
			setRule(((IFile) this.writerSettings.getDestination()).getParent());
		}
	}

	public void setOpenProgressOnFinish(boolean openProgressOnFinish) {
		this.openProgressOnFinish = openProgressOnFinish;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, getName(), 100);
		receiver.setDataWriter(writer);
		writer.setSettings(writerSettings);
		if (openProgressOnFinish) {
			//		//Open the Progress View or show instructions on how to open Progress View
			WorkbenchJob job = new WorkbenchJob("Opening Progress view...") {

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IProgressConstants.PROGRESS_VIEW_ID);
					} catch (PartInitException e) {
						return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Failed to open progress view.\n"
							+ "The Progress of the Snapshot is displayed in the Progress View\n"
							+ "which can be opened by going to Window > Show View > Other... > General > Progress", e);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		IStatus retVal = receiver.run(subMonitor.newChild(95));
		if (this.writerSettings.getDestination() instanceof IFile) {
			((IFile) this.writerSettings.getDestination()).refreshLocal(IResource.DEPTH_ONE, subMonitor.newChild(5));
		}
		subMonitor.setTaskName("Finished");
		subMonitor.done();
		return retVal;
	}

	protected Action getSnapshotResultsAction() {
		Action dispRes = new Action("View Snapshot Results") {
			@Override
			public void run() {
				MessageBox report = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OK);
				report.setText("Snapshot Results");
				StringBuffer output = new StringBuffer();
				List<File> files = writer.getOutputFileList();
				if (files != null) {
					output.append("The snapshot was written to the following files:\n" + files + "\n");
					report.setMessage(output.toString());
					report.open();
				}
			}
		};
		return dispRes;
	}

}
