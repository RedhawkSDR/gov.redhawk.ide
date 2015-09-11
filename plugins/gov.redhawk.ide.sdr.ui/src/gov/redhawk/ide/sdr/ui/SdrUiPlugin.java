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
package gov.redhawk.ide.sdr.ui;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.commands.SetSdrRootCommand;
import gov.redhawk.ide.sdr.internal.ui.commands.InitIdlLibraryCommand;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;
import gov.redhawk.ide.sdr.ui.util.RefreshSdrJob;

import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @since 1.1
 */
public class SdrUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.sdr.ui";

	/**
	 * @since 3.1
	 */
	public static final String EDITING_DOMAIN_ID = SdrUiPlugin.PLUGIN_ID + ".editingDomain";

	// The shared instance
	private static SdrUiPlugin plugin;

	private SdrRoot targetSdrRoot;

	private RefreshSdrJob reloadSdrJob;

	private final IPropertyChangeListener sdrRootPrefListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (event.getProperty().equals(SdrUiPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE)
			        || event.getProperty().equals(SdrUiPreferenceConstants.TARGET_SDR_DEV_PATH)
			        || event.getProperty().equals(SdrUiPreferenceConstants.TARGET_SDR_DOM_PATH)) {
				reloadSdr();
				SdrUiPlugin.this.reloadSdrJob.schedule();
			}
		}
	};

	private TransactionalEditingDomain editingDomain;

	/**
	 * The constructor
	 */
	public SdrUiPlugin() {
	}

	private void reloadSdr() {
		final IPath targetSdrPath = getTargetSdrPath();
		URI sdrRoot = null;
		if (targetSdrPath != null) {
			sdrRoot = URI.createURI(targetSdrPath.toFile().toURI().toString());
		}
		final String domPath = getDomPath();
		final String devPath = getDevPath();

		this.editingDomain.getCommandStack().execute(new SetSdrRootCommand(this.targetSdrRoot, sdrRoot, domPath, devPath));
	}

	private void loadIdlPath() {
		final IdlLibrary library = LibraryFactory.eINSTANCE.createIdlLibrary();
		this.editingDomain.getCommandStack().execute(SetCommand.create(this.editingDomain,
		        this.targetSdrRoot,
		        SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY,
		        library));
		this.editingDomain.getCommandStack().execute(new InitIdlLibraryCommand(library));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		SdrUiPlugin.plugin = this;

		initSdr();
		getPreferenceStore().addPropertyChangeListener(this.sdrRootPrefListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		SdrUiPlugin.plugin = null;

		getPreferenceStore().removePropertyChangeListener(this.sdrRootPrefListener);
		this.targetSdrRoot = null;

		super.stop(context);
	}

	private void initSdr() throws URISyntaxException {
		// Create the root model object
		this.targetSdrRoot = SdrFactory.eINSTANCE.createSdrRoot();

		// Create the job to refresh the SDR root
		this.reloadSdrJob = new RefreshSdrJob(this.targetSdrRoot);
		this.reloadSdrJob.setSystem(true);
		this.reloadSdrJob.setUser(false);

		// Create the EditingDomain and a Resource, and place the root model object in the Resource
		this.editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(SdrUiPlugin.EDITING_DOMAIN_ID);
		final ResourceSet resourceSet = SdrUiPlugin.this.editingDomain.getResourceSet();
		final Resource sdrResource = resourceSet.createResource(URI.createURI("virtual://sdr.sdr"));
		SdrUiPlugin.this.editingDomain.getCommandStack().execute(new AddCommand(SdrUiPlugin.this.editingDomain,
			sdrResource.getContents(), SdrUiPlugin.this.targetSdrRoot));

		// Add the IDL model object and our IDL paths
		loadIdlPath();

		// Schedule the initial load of the SDR root
		final Job job = new Job("Startup Job") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				reloadSdr();
				SdrUiPlugin.this.reloadSdrJob.schedule();
				return Status.OK_STATUS;
			}

		};
		job.setUser(false);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * @returns the target SDR root. In general, you should not assume that the root has been fully loaded.
	 * @since 3.1
	 */
	public SdrRoot getTargetSdrRoot() {
		return this.targetSdrRoot;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SdrUiPlugin getDefault() {
		return SdrUiPlugin.plugin;
	}

	/**
	 * @since 3.1
	 */
	public String getDomPath() {
		String retVal = getPreferenceStore().getString(SdrUiPreferenceConstants.TARGET_SDR_DOM_PATH).trim();
		if (retVal == null) {
			retVal = "dom";
		}
		return retVal;
	}

	/**
	 * @since 3.1
	 */
	public String getDevPath() {
		String retVal = getPreferenceStore().getString(SdrUiPreferenceConstants.TARGET_SDR_DEV_PATH).trim();
		if (retVal == null) {
			retVal = "dev";
		}
		return retVal;
	}

	/**
	 * @since 3.1
	 */
	public IPath getTargetSdrDomPath() {
		if (getTargetSdrPath() == null) {
			return null;
		}
		return getTargetSdrPath().append(getDomPath());
	}

	/**
	 * @since 3.1
	 */
	public IPath getTargetSdrDevPath() {
		if (getTargetSdrPath() == null) {
			return null;
		}
		return getTargetSdrPath().append(getDevPath());
	}

	/**
	 * @since 3.1
	 */
	public IPath getTargetSdrPath() {
		String runtimePath = getPreferenceStore().getString(SdrUiPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE).trim();
		if (runtimePath.isEmpty()) {
			return null;
		}

		if (runtimePath.startsWith("${") && runtimePath.endsWith("}")) {
			final String envName = runtimePath.substring(2, runtimePath.length() - 1);
			runtimePath = System.getenv(envName);
			if (runtimePath == null) {
				return null;
			}
		}
		return new Path(runtimePath);
	}

	/**
	 * @since 3.1
	 */
	public void logError(final String error) {
		logError(error, null);
	}

	/**
	 * @since 3.1
	 */
	public void logError(String error, final Throwable throwable) {
		if (error == null && throwable != null) {
			error = throwable.getMessage();
		}
		getLog().log(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, IStatus.OK, error, throwable));
	}
}
