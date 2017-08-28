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
package gov.redhawk.ide.sad.ui.wizard;

import java.util.List;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
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
 * @since 4.0
 */
public class ScaWaveformProjectAssemblyControllerWizardPage extends WizardPage {

	private TreeViewer treeViewer;
	private static final int TABLE_HEIGHT_HINT = 150;

	public ScaWaveformProjectAssemblyControllerWizardPage(final String pageName) {
		super(pageName);
		setTitle("Select Assembly Controller for Waveform");
		this.setPageComplete(true);
	}

	@Override
	public void createControl(final Composite parent) {
		// The top-level composite for this page
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Top Heading
		final Label directionsLabel = new Label(composite, SWT.NONE);
		directionsLabel.setText("Select the Component that you want to be the Assembly Controller for your Waveform:");
		GridDataFactory.generate(directionsLabel, 2, 1);

		this.treeViewer = new TreeViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.treeViewer.setContentProvider(new SdrNavigatorContentProvider());
		this.treeViewer.setLabelProvider(new SdrNavigatorLabelProvider());
		this.treeViewer.setComparator(new SdrViewerSorter());

		final GridData data = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		data.heightHint = ScaWaveformProjectAssemblyControllerWizardPage.TABLE_HEIGHT_HINT;
		this.treeViewer.getControl().setLayoutData(data);

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
						treeViewer.setInput(sdrRoot.getComponentsContainer());
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

	public SoftPkg getAssemblyController() {
		return (SoftPkg) ((IStructuredSelection) this.treeViewer.getSelection()).getFirstElement();
	}

	@Override
	public boolean isPageComplete() {
		if (treeViewer.getSelection().isEmpty()) {
			return super.isPageComplete();
		}

		Object selection = ((TreeSelection) treeViewer.getSelection()).getFirstElement();
		if (selection instanceof SoftPkg) {
			return super.isPageComplete();
		}
		return false;
	}

	/**
	 * @deprecated {@link ScaWaveformProjectAssemblyControllerWizardPage} now collects the list of available components
	 * internally
	 */
	@Deprecated
	public void setComponents(final List<SoftPkg> components) {
		return;
	}
}
