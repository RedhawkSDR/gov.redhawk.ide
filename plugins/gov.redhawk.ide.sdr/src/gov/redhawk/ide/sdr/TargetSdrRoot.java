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
package gov.redhawk.ide.sdr;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.ide.internal.sdr.commands.InitIdlLibraryCommand;
import gov.redhawk.ide.sdr.commands.SetSdrRootCommand;
import gov.redhawk.ide.sdr.jobs.RefreshSdrJob;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import mil.jpeojtrs.sca.util.ScaUriHelpers;

public class TargetSdrRoot {

	public static final String EDITING_DOMAIN_ID = IdeSdrActivator.PLUGIN_ID + ".editingDomain";

	/**
	 * The job group for a job that refreshes the SDR root model.
	 */
	public static final Object FAMILY_REFRESH_SDR = new Object();

	private static SdrRoot sdrRoot;
	private static TransactionalEditingDomain editingDomain;
	private static RefreshSdrJob reloadSdrJob;
	private static IPreferenceChangeListener prefListener;

	private TargetSdrRoot() {
	}

	/**
	 * Lazy initialization of the SDR root model
	 */
	static {
		// Create the root model object
		sdrRoot = SdrFactory.eINSTANCE.createSdrRoot();

		// Create the job to refresh the SDR root
		reloadSdrJob = new RefreshSdrJob(sdrRoot);
		reloadSdrJob.setSystem(true);
		reloadSdrJob.setUser(false);

		// Create the EditingDomain and a Resource, and place the root model object in the Resource
		editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(EDITING_DOMAIN_ID);
		editingDomain.getResourceSet().getLoadOptions().put(ScaUriHelpers.RESOURCE_SET_LOCK, new ReentrantReadWriteLock());
		final Resource sdrResource = editingDomain.getResourceSet().createResource(URI.createURI("virtual://sdr.sdr"));
		editingDomain.getCommandStack().execute(new AddCommand(editingDomain, sdrResource.getContents(), sdrRoot));

		// Add the IDL model object and our IDL paths
		loadIdlPath();

		// Schedule the initial load of the SDR root
		final Job job = Job.create("Startup Job", monitor -> {
			setSdrRootPaths();
			scheduleRefresh();
			return Status.OK_STATUS;
		});
		job.setUser(false);
		job.setSystem(true);
		job.schedule();

		// Listen for changes to SDR root preferences
		prefListener = event -> {
			if (event.getKey().equals(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE)
				|| event.getKey().equals(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH)
				|| event.getKey().equals(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH)) {
				setSdrRootPaths();
				scheduleRefresh();
			}
		};
		InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID).addPreferenceChangeListener(prefListener);
	}

	/**
	 * Should only be called by the plug-in's stop activator
	 */
	static void shutdown() {
		InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID).removePreferenceChangeListener(prefListener);
		reloadSdrJob.cancel();
		reloadSdrJob = null;
		sdrRoot = null;
		editingDomain = null;
	}

	/**
	 * @returns The target SDR root. In general, you should not assume that the root has been fully loaded.
	 */
	public static SdrRoot getSdrRoot() {
		return sdrRoot;
	}

	public static void scheduleRefresh() {
		reloadSdrJob.schedule();
	}

	private static void loadIdlPath() {
		final IdlLibrary library = LibraryFactory.eINSTANCE.createIdlLibrary();
		editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, sdrRoot, SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY, library));
		editingDomain.getCommandStack().execute(new InitIdlLibraryCommand(library));
	}

	private static void setSdrRootPaths() {
		final IPath targetSdrPath = IdeSdrPreferences.getTargetSdrPath();
		URI sdrRootUri = null;
		if (targetSdrPath != null) {
			sdrRootUri = URI.createURI(targetSdrPath.toFile().toURI().toString());
		}
		final String domPath = IdeSdrPreferences.getDomPath();
		final String devPath = IdeSdrPreferences.getDevPath();

		editingDomain.getCommandStack().execute(new SetSdrRootCommand(sdrRoot, sdrRootUri, domPath, devPath));
	}

}
