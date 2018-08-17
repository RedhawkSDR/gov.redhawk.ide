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
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorContentProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorLabelProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrViewerSorter;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

/**
 * @since 1.1
 */
public class ScaNodeProjectDevicesWizardPage extends WizardPage {

	private TreeViewer treeViewer;

	/**
	 * @since 1.2
	 */
	public ScaNodeProjectDevicesWizardPage(final String pageName) {
		super(pageName);
		setTitle("Select Devices for Node");
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
		composite.setLayout(new GridLayout(2, false));

		// Top Heading
		final Label directionsLabel = new Label(composite, SWT.NONE);
		directionsLabel.setText("Select devices to include in this node:");
		GridDataFactory.generate(directionsLabel, 2, 1);

		this.treeViewer = new TreeViewer(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.treeViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		this.treeViewer.setContentProvider(new SdrNavigatorContentProvider());
		this.treeViewer.setLabelProvider(new SdrNavigatorLabelProvider());
		this.treeViewer.setComparator(new SdrViewerSorter());

		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());

		final WorkspaceJob job = new WorkspaceJob("Load SdrRoot") {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final SdrRoot sdrRoot = TargetSdrRoot.getSdrRoot();
				sdrRoot.load(null);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						treeViewer.setInput(sdrRoot.getDevicesContainer());
					}
				});
				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.schedule();

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(composite);
	}

	@Override
	public void setVisible(final boolean visible) {
		this.setPageComplete(true);
		super.setVisible(visible);
	}

	@SuppressWarnings("unchecked")
	public SoftPkg[] getNodeDevices() {
		List<SoftPkg> softPkgs = new ArrayList<SoftPkg>();
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
		for (Iterator<SoftPkg> iterator = selection.iterator(); iterator.hasNext();) {
			softPkgs.add(iterator.next());
		}
		return softPkgs.toArray(new SoftPkg[0]);
	}

	@Override
	public boolean isPageComplete() {
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
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
