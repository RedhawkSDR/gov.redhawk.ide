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
package gov.redhawk.ide.graphiti.dcd.ui.project.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorContentProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorLabelProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrViewerSorter;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

/**
 * @since 2.0
 */
public class ScaNodeProjectDevicesWizardPage extends WizardPage {

	private TreeViewer devicesTreeViewer;
	private TreeViewer servicesTreeViewer;

	/**
	 * @since 1.2
	 */
	public ScaNodeProjectDevicesWizardPage(final String pageName) {
		super(pageName);
		setTitle("Select Devices and/or Services for Node");
		this.setPageComplete(true);
	}

	/**
	 * @deprecated {@link ScaNodeProjectDevicesWizardPage} now collects the list of available devices
	 * internally
	 */
	@Deprecated
	public ScaNodeProjectDevicesWizardPage(final String pageName, final SoftPkg[] devices) {
		this(pageName);
	}

	@Override
	public void createControl(final Composite parent) {
		// The top-level composite for this page
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createDevicesComposite(composite);
		createServicesComposite(composite);

		setControl(composite);
	}

	private void createDevicesComposite(Composite composite) {
		final Composite deviceComposite = new Composite(composite, SWT.NONE);
		deviceComposite.setLayout(new GridLayout(1, false));
		deviceComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		final Label directionsLabel = new Label(deviceComposite, SWT.NONE);
		directionsLabel.setText("Select one or more device to include in this node:");
		GridDataFactory.generate(directionsLabel, 1, 1);

		this.devicesTreeViewer = new TreeViewer(deviceComposite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		createSelectionComposite(deviceComposite, "devices", this.devicesTreeViewer);
	}

	private void createServicesComposite(Composite composite) {
		final Composite serviceComposite = new Composite(composite, SWT.NONE);
		serviceComposite.setLayout(new GridLayout(1, false));
		serviceComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		final Label directionsLabel = new Label(serviceComposite, SWT.NONE);
		directionsLabel.setText("Select one or more service to include in this node:");
		GridDataFactory.generate(directionsLabel, 1, 1);

		this.servicesTreeViewer = new TreeViewer(serviceComposite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		createSelectionComposite(serviceComposite, "services", this.servicesTreeViewer);
	}

	private void createSelectionComposite(Composite composite, final String type, final TreeViewer viewer) {
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		viewer.setContentProvider(new SdrNavigatorContentProvider());
		viewer.setLabelProvider(new SdrNavigatorLabelProvider());
		viewer.setComparator(new SdrViewerSorter());

		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());

		final WorkspaceJob job = new WorkspaceJob("Load SdrRoot") {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
				sdrRoot.load(null);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if ("devices".equals(type)) {
							viewer.setInput(sdrRoot.getDevicesContainer());
						} else {
							viewer.setInput(sdrRoot.getServicesContainer());
						}
					}
				});
				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.schedule();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getWizard().getContainer().updateButtons();
			}
		});
	}

	@Override
	public void setVisible(final boolean visible) {
		this.setPageComplete(true);
		super.setVisible(visible);
	}

	/**
	 * @deprecated use {@link #getNodeElements()} instead, which also returns selected services
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public SoftPkg[] getNodeDevices() {
		List<SoftPkg> softPkgs = new ArrayList<SoftPkg>();
		StructuredSelection selection = (StructuredSelection) devicesTreeViewer.getSelection();
		for (Iterator<SoftPkg> iterator = selection.iterator(); iterator.hasNext();) {
			softPkgs.add(iterator.next());
		}
		return softPkgs.toArray(new SoftPkg[0]);
	}

	/**
	 * @since 1.3
	 */
	@SuppressWarnings("unchecked")
	public SoftPkg[] getNodeElements() {
		List<SoftPkg> softPkgs = new ArrayList<SoftPkg>();
		StructuredSelection devicesSelection = (StructuredSelection) devicesTreeViewer.getSelection();
		for (Iterator<SoftPkg> iterator = devicesSelection.iterator(); iterator.hasNext();) {
			softPkgs.add(iterator.next());
		}

		StructuredSelection serviceSelection = (StructuredSelection) servicesTreeViewer.getSelection();
		for (Iterator<SoftPkg> iterator = serviceSelection.iterator(); iterator.hasNext();) {
			softPkgs.add(iterator.next());
		}

		return softPkgs.toArray(new SoftPkg[0]);
	}

	@Override
	public boolean isPageComplete() {
		StructuredSelection selection = (StructuredSelection) devicesTreeViewer.getSelection();
		for (Iterator< ? > iterator = selection.iterator(); iterator.hasNext();) {
			if (!(iterator.next() instanceof SoftPkg)) {
				return false;
			}
		}

		return super.isPageComplete();
	}

	/**
	 * @deprecated {@link ScaNodeProjectDevicesWizardPage} now collects the list of available devices
	 * internally
	 */
	@Deprecated
	public void setDevices(Collection<SoftPkg> devices) {
		return;
	}
}
