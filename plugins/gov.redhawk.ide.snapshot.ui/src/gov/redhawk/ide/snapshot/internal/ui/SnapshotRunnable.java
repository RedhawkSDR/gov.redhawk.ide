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
package gov.redhawk.ide.snapshot.internal.ui;

import gov.redhawk.bulkio.util.BulkIOType;

import gov.redhawk.ide.snapshot.datareceiver.AbstractDataReceiverAttributes;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;
import gov.redhawk.ide.snapshot.ui.handlers.SnapshotSettings;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.OrbSession;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.StreamSRI;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public class SnapshotRunnable implements IRunnableWithProgress {

	private SnapshotSettings settings;
	private ScaUsesPort port;
	private File startFile;
	private String[][] outputFiles;
	private AbstractDataReceiverAttributes recAttributes = null;
	/**Storage for data provided to the snapshot at start up, will not connect to the port
	 * if this is not null*/
	private Object[] datalist = null;
	/**Storage for the sri provided to the snapshot at start up*/
	private StreamSRI sri = null;
	private IDataReceiver.CaptureMethod processingMethod;

	public SnapshotRunnable(SnapshotSettings settings, ScaUsesPort port, Object[] datalist, StreamSRI sri, ArrayList<AbstractDataReceiverAttributes> receivers) {
		this(settings, port, receivers);
		this.datalist = datalist;
		this.sri = sri;
	}

	public SnapshotRunnable(SnapshotSettings settings, ScaUsesPort port, ArrayList<AbstractDataReceiverAttributes> receivers) {
		this.settings = settings;
		this.port = port;
		this.outputFiles = null;
		this.processingMethod = IDataReceiver.CaptureMethod.stringToValue(settings.getCaptureType());

		//get the data receiver attributes associated with the file type selected
		for (int i = 0; i < receivers.size(); i++) {
			if (receivers.get(i).getReceiverName().equals(settings.getFileType())) {
				this.recAttributes = receivers.get(i);
				break;
			}
		}

		//check the the number of samples is greater than 0
		if (settings.getSamples() <= 0) {
			throw new IllegalArgumentException("The number of samples must be greater than 0");
		}

		//check that the appropriate attributes could be found
		if (recAttributes == null) {
			throw new IllegalArgumentException("The file type is not recognized");
		}

		//check that fileName is not blank
		if (settings.getFileName() == null | settings.getFileName() == "") {
			throw new IllegalArgumentException("The file name must be specified");
		}

		//add the file type extension to the file name if it was not added, defaulting to the 
		//	first supported type
		settings.setFileType((settings.getFileType() == null | settings.getFileType() == "") ? settings.getSupportedTypes()[0] : settings.getFileType());
		if (!settings.getFileName().endsWith(recAttributes.getReceiverExtensions()[0])) {
			settings.setFileName(settings.getFileName() + recAttributes.getReceiverExtensions()[0]);
		}

		//instantiating startFile
		if (settings.getSaveToWorkspace()) {
			//ensuring the filePath is not blank
			if (settings.getFilePath() == null | settings.getFilePath() == "") {
				throw new IllegalArgumentException("The file must be saved in a project\nif it is to be saved to the workspace");
			}

			//concatenating the workspace file path, the path relative to the workspace, and the 
			//file name into startFile
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(settings.getFilePath());
			startFile = new File(resource.getLocation().toFile(), settings.getFileName());
		} else {
			startFile = new File(settings.getFileName());
		}

		if (!startFile.getParentFile().exists()) {
			throw new IllegalArgumentException("The file must be saved in an existing folder");
		}
	}

	/**
	 * This function checks to see if there are any files of the format 
	 * [file name][number].[file type] or [filename].[file type] as these files are at 
	 * risk of being overwritten
	 * @return true if there are files with the specified file format, false otherwise
	 */
	public boolean checkForSimilarFiles() {
		File fileDir = startFile.getParentFile(); //directory to hold the output file location
		boolean similarFiles = false; //whether or not there are file of a similar name

		if (settings.getFileType() == null) {
			throw new IllegalArgumentException("The File Type should be set to something");
		}
		String search = startFile.getName().substring(0, startFile.getName().lastIndexOf(recAttributes.getReceiverExtensions()[0]));
		if (datalist == null) {
			search = search + "[0-9]*";
		}
		search = search + "(";
		String[] extensions = recAttributes.getReceiverExtensions();
		for (int i = 0; i < extensions.length - 1; i++) {
			search += extensions[i] + "|";
		}
		search += extensions[extensions.length - 1] + ")";
		String[] siblingFiles = fileDir.list();

		//search for the similar files
		if (siblingFiles != null && siblingFiles.length > 0) {
			for (int i = 0; i < siblingFiles.length; i++) {
				if (siblingFiles[i].matches(search)) {
					similarFiles = true;
					break;
				}
			}
		}
		return similarFiles;
	}

	public String[][] getOutputFiles() {
		return outputFiles;
	}

	public String[] getOutputExtensions() {
		return this.recAttributes.getReceiverExtensions();
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		BulkIOType type;
		try {
			type = BulkIOType.getType(port.getRepid());
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		if (this.datalist != null) {
			try {
				this.outputFiles = recAttributes.writeFile(startFile, datalist, type, false, sri);
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			}
			return;
		}
		final OrbSession session = OrbSession.createSession();
		org.omg.CORBA.Object ref = null;
		IDataReceiver receiver = null;
		String connectionId = null;
		try {
			POA poa = session.getPOA();
			receiver = recAttributes.dataReceiverFactory(startFile, (long) settings.getSamples(), settings.getSamples(), type, false, this.processingMethod);
			ref = type.createRef(poa, receiver);
			connectionId = "snapshot_" + System.currentTimeMillis();
			port.connectPort(ref, connectionId);
			receiver.processSamples(monitor);
			outputFiles = receiver.getOutputFiles();
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		} catch (ServantNotActive e) {
			throw new InvocationTargetException(e);
		} catch (WrongPolicy e) {
			throw new InvocationTargetException(e);
		} catch (InvalidPort e) {
			throw new InvocationTargetException(e);
		} catch (OccupiedPort e) {
			throw new InvocationTargetException(e);
		} catch (NullPointerException e) {
			throw new InvocationTargetException(e);
		} finally {
			if (connectionId != null) {
				try {
					port.disconnectPort(connectionId);
				} catch (InvalidPort e) {
					// PASS
				} catch (SystemException e) {
					// PASS
				}
			}
			if (ref != null) {
				ref._release();
				ref = null;
			}
			if (receiver != null) {
				try {
					receiver.dispose();
				} catch (IOException e) {
					// PASS
				}
				receiver = null;
			}
			session.dispose();
		}

	}
}
