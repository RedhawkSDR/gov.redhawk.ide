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

package gov.redhawk.ide.dcd.ui.wizard;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.dcd.ui.DcdUiActivator;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.DomainManager;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * The Class NewScaDeviceProjectWizard.
 * @since 1.1
 */
public class NewScaNodeProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	protected static final long SDR_REFRESH_DELAY = 500;

	/** The node properties page. */
	private ScaNodeProjectPropertiesWizardPage nodePropertiesPage;

	/** The node properties page. */
	private ScaNodeProjectDevicesWizardPage nodeDevicesPage;

	private IFile openEditorOn;

	private IConfigurationElement fConfig;

	public NewScaNodeProjectWizard() {
		setWindowTitle("Node Project");
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean canFinish() {
		if (this.nodePropertiesPage.isCreateNewResource()) {
			return super.canFinish();
		} else {
			return this.nodePropertiesPage.isPageComplete();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().getCurrentPage().getControl().setEnabled(false);
			// Find the working sets and where the new project should be located on disk
			final IWorkingSet[] workingSets = this.nodePropertiesPage.getSelectedWorkingSets();
			final boolean isCreateNewResource = this.nodePropertiesPage.isCreateNewResource();
			final String projectName = this.nodePropertiesPage.getProjectName();
			final java.net.URI locationURI;
			if (this.nodePropertiesPage.useDefaults()) {
				locationURI = null;
			} else {
				locationURI = this.nodePropertiesPage.getLocationURI();
			}
			final String id = this.nodePropertiesPage.getID();
			final IPath existingDcdPath = this.nodePropertiesPage.getExistingResourcePath();

			final String domainManagerName = this.nodePropertiesPage.getDomain();
			final SoftPkg[] devices = this.nodeDevicesPage.getNodeDevices();

			BasicNewProjectResourceWizard.updatePerspective(this.fConfig);
			final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException {
					try {
						final SubMonitor progress = SubMonitor.convert(monitor, 4);

						// Create an empty project
						final IProject project = NodeProjectCreator.createEmptyProject(projectName, locationURI, progress.newChild(1));
						if (workingSets.length > 0) {
							PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
						}
						

						// If we're creating a new waveform (vs importing one)
						if (isCreateNewResource) {
							// Create the XML files
							NewScaNodeProjectWizard.this.openEditorOn = NodeProjectCreator.createNodeFiles(project, id, null, domainManagerName, devices,
							        progress.newChild(1));
						} else {
							openEditorOn = project.getFile("DeviceManager.dcd.xml");
							ProjectCreator.importFile(project, openEditorOn, existingDcdPath, progress.newChild(1));
							ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
							URI uri = URI.createPlatformResourceURI(openEditorOn.getFullPath().toString(), true).appendFragment(DeviceConfiguration.EOBJECT_PATH);
							DeviceConfiguration dcd = (DeviceConfiguration) resourceSet.getEObject(uri, true);
							dcd.setId(id);
							dcd.setName(project.getName());
							if (domainManagerName != null) {
								DomainManager dm = DcdFactory.eINSTANCE.createDomainManager();
								dm.setNamingService(PartitioningFactory.eINSTANCE.createNamingService());
								dm.getNamingService().setName(domainManagerName + "/" + domainManagerName);
								dcd.setDomainManager(dm);
							}
							try {
	                            dcd.eResource().save(null);
                            } catch (IOException e) {
                            	throw new CoreException(new Status(IStatus.ERROR, SadUiActivator.PLUGIN_ID, "Failed to modify SAD File."));
                            }
							openEditorOn.refreshLocal(IResource.DEPTH_ONE, null);
						}

						// Setup automatic RPM spec file generation
						CodegenUtil.addTopLevelRPMSpecBuilder(project, progress.newChild(1));
					} finally {
						monitor.done();
					}
				}
			};
			getContainer().run(false, false, op);
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ((this.openEditorOn != null) && this.openEditorOn.exists()) {
				IDE.openEditor(activePage, this.openEditorOn, true);
			}
		} catch (final InvocationTargetException x) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DcdUiActivator.PLUGIN_ID, x.getCause().getMessage(), x.getCause()),
			        StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (final InterruptedException x) {
			return false;
		} catch (final PartInitException e) {
			// If the editor cannot be opened, still close the wizard
			return true;
		} finally {
			getContainer().getCurrentPage().getControl().setEnabled(true);
		}
		return true;
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		if (!this.nodePropertiesPage.isCreateNewResource() && (page == this.nodePropertiesPage)) {
			return null;
		} else {
			return super.getNextPage(page);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench arg0, final IStructuredSelection arg1) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.nodePropertiesPage = new ScaNodeProjectPropertiesWizardPage("");
		this.nodePropertiesPage.setDescription("Choose to create a new Node or import an existing one.");
		addPage(this.nodePropertiesPage);

		this.nodeDevicesPage = new ScaNodeProjectDevicesWizardPage("");
		this.nodeDevicesPage.setDescription("Add existing Device(s) to your node.");
		addPage(this.nodeDevicesPage);

		final WorkspaceJob job = new WorkspaceJob("Load SdrRoot") {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
				sdrRoot.load(monitor);
				NewScaNodeProjectWizard.this.nodeDevicesPage.setDevices(sdrRoot.getDevicesContainer().getComponents());
				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.schedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.fConfig = config;
	}

}
