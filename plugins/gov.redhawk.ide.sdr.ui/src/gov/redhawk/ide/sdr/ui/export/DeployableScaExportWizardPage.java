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
package gov.redhawk.ide.sdr.ui.export;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard.DeployableScaExportWizardModel;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @since 3.1
 */
public class DeployableScaExportWizardPage extends WizardPage {

	private DeployableScaExportWizardModel model;

	protected DeployableScaExportWizardPage() {
		super("Export");
	}

	@Override
	public void createControl(final Composite parent) {
		this.model = ((DeployableScaExportWizard) getWizard()).getModel();

		final DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport.create(this, dbc);

		setTitle("Deployable SCA");
		setMessage("Export the selected projects into a form suitable for deploying into a OSSIE SDR root");

		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(1, false));

		final Composite projectSelectionGroup = new Composite(client, SWT.NULL);
		projectSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		projectSelectionGroup.setLayout(new GridLayout(2, false));

		final Label availScaProjects = new Label(projectSelectionGroup, SWT.NONE);
		availScaProjects.setText("Available SCA Projects:");
		availScaProjects.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		final CheckboxTableViewer availableProjectsViewer = new CheckboxTableViewer(new Table(projectSelectionGroup, SWT.CHECK | SWT.READ_ONLY | SWT.BORDER));
		availableProjectsViewer.setContentProvider(new ArrayContentProvider());
		availableProjectsViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return ((IProject) element).getName();
			}

		});
		final IProject[] scaProjects = getScaProjects();
		availableProjectsViewer.setInput(scaProjects);
		availableProjectsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final Composite buttonBox = new Composite(projectSelectionGroup, SWT.NULL);
		buttonBox.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		buttonBox.setLayout(new GridLayout(1, false));

		final Label selectedProjects = new Label(projectSelectionGroup, SWT.NONE);
		selectedProjects.setText(this.model.projectsToExport.size() + " of " + scaProjects.length + " selected.");
		selectedProjects.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		availableProjectsViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				selectedProjects.setText(availableProjectsViewer.getCheckedElements().length + " of " + scaProjects.length + " selected.");
			}
		});

		final Button selectAll = new Button(buttonBox, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				availableProjectsViewer.setAllChecked(true);
				DeployableScaExportWizardPage.this.model.projectsToExport.clear();
				DeployableScaExportWizardPage.this.model.projectsToExport.addAll(Arrays.asList(scaProjects));
				selectedProjects.setText(availableProjectsViewer.getCheckedElements().length + " of " + scaProjects.length + " selected.");
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// PASS
			}
		});

		final Button selectNone = new Button(buttonBox, SWT.PUSH);
		selectNone.setText("Deselect All");
		selectNone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectNone.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				availableProjectsViewer.setAllChecked(false);
				DeployableScaExportWizardPage.this.model.projectsToExport.clear();
				selectedProjects.setText(availableProjectsViewer.getCheckedElements().length + " of " + scaProjects.length + " selected.");
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// PASS
			}
		});

		final TabFolder exportCustomization = new TabFolder(client, SWT.TOP);
		exportCustomization.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		final Composite destinationComposite = new Composite(exportCustomization, SWT.NONE);
		destinationComposite.setLayout(new GridLayout(2, false));

		final Button directoryRadio = new Button(destinationComposite, SWT.RADIO);
		directoryRadio.setText("Directory:");
		directoryRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		directoryRadio.setSelection(true);

		final Text directoryText = new Text(destinationComposite, SWT.BORDER);
		directoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		directoryText.setEnabled(true);

		final Button directoryBrowse = new Button(destinationComposite, SWT.PUSH);
		directoryBrowse.setText("Browse...");
		directoryBrowse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		directoryBrowse.setEnabled(true);
		directoryBrowse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final DirectoryDialog fd = new DirectoryDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN);
				fd.setText("Destination Directory");
				fd.setFilterPath(null);
				final String selected = fd.open();
				if (selected != null) {
					directoryText.setText(selected);
					dbc.updateModels();
				}
			}

		});

		directoryRadio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean selected = directoryRadio.getSelection();
				directoryText.setEnabled(selected);
				directoryBrowse.setEnabled(selected);
			}

		});

		final Button archiveRadio = new Button(destinationComposite, SWT.RADIO);
		archiveRadio.setText("Archive file:");
		archiveRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		archiveRadio.setSelection(false);

		final Text archiveText = new Text(destinationComposite, SWT.BORDER);
		archiveText.setText("");
		archiveText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		archiveText.setEnabled(false);

		final Button archiveBrowse = new Button(destinationComposite, SWT.PUSH);
		archiveBrowse.setText("Browse...");
		archiveBrowse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		archiveBrowse.setEnabled(false);
		archiveBrowse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog fd = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.SAVE);
				fd.setText("Destination Archive");
				fd.setFilterPath(null);
				final String[] filterExt = { "*.zip" };
				fd.setFilterExtensions(filterExt);
				final String selected = fd.open();
				if (selected != null) {
					IPath zippath = new Path(selected);
					if (zippath.getFileExtension() == null) {
						zippath = zippath.addFileExtension("zip");
					}
					archiveText.setText(zippath.toString());
					dbc.updateModels();
				}
			}

		});

		archiveRadio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean selected = archiveRadio.getSelection();
				archiveText.setEnabled(selected);
				archiveBrowse.setEnabled(selected);
			}

		});

		final TabItem destinationtab = new TabItem(exportCustomization, SWT.NONE);
		destinationtab.setText("Destination");
		destinationtab.setControl(destinationComposite);

		setControl(client);

		bind(dbc, availableProjectsViewer, directoryRadio, directoryText, archiveRadio, archiveText);
	}

	private void bind(final DataBindingContext dbc, final CheckboxTableViewer availableProjectsViewer, final Button directoryRadio, final Text directoryText,
	        final Button archiveRadio, final Text archiveText) {

		// Data-Bindings
		// Setup databinding
		dbc.bindSet(ViewersObservables.observeCheckedElements(availableProjectsViewer, IProject.class), this.model.projectsToExport, null, null);

		dbc.bindValue(SWTObservables.observeSelection(directoryRadio), this.model.directoryExport, null, null);

		dbc.bindValue(SWTObservables.observeSelection(archiveRadio), this.model.archiveExport, null, null);

		dbc.bindValue(SWTObservables.observeText(directoryText, SWT.Modify), this.model.directoryDestination,
		        new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {
			        @Override
					public IStatus validate(final Object value) {
				        final String s = (String) value;
				        if (!directoryRadio.getSelection()) {
					        return ValidationStatus.ok();
				        }

				        if ((s == null) || (s.length() == 0)) {
					        return ValidationStatus.error("Enter an archive destination name.");
				        }
				        return ValidationStatus.ok();
			        }
		        }), null);

		dbc.bindValue(SWTObservables.observeText(archiveText, SWT.Modify), this.model.archiveDestination,
		        new UpdateValueStrategy().setAfterConvertValidator(new IValidator() {
			        @Override
					public IStatus validate(final Object value) {
				        final String s = (String) value;
				        if (!archiveRadio.getSelection()) {
					        return ValidationStatus.ok();
				        }

				        if ((s == null) || (s.length() == 0)) {
					        return ValidationStatus.error("Enter an archive destination name.");
				        }
				        return ValidationStatus.ok();
			        }
		        }), null);
	}

	private IProject[] getScaProjects() {
		final List<IProject> availableProjects = new ArrayList<IProject>();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (final IProject proj : root.getProjects()) {
			try {
				if (proj.isOpen() && (proj.hasNature(ScaWaveformProjectNature.ID) || proj.hasNature(ScaNodeProjectNature.ID) || proj.hasNature(ScaComponentProjectNature.ID))) {
					availableProjects.add(proj);
				}
			} catch (final CoreException e) {
				RedhawkIDEUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Unexpected error loading projects", e));
			}
		}
		return availableProjects.toArray(new IProject[availableProjects.size()]);
	}
}
