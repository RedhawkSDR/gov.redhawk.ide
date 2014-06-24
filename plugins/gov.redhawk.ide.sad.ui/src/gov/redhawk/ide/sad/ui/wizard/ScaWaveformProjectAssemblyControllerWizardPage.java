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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.impl.SoftPkgImpl;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

/**
 * @since 4.0
 */
public class ScaWaveformProjectAssemblyControllerWizardPage extends WizardPage {

	private List<SoftPkg> components;
	private TableViewer tableViewer;
	private static final int TABLE_HEIGHT_HINT = 150;

	public ScaWaveformProjectAssemblyControllerWizardPage(final String pageName) {
		super(pageName);
		setTitle("Select Assembly Controller for Waveform");
		this.setPageComplete(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		// The top-level composite for this page
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Top Heading
		final Label directionsLabel = new Label(composite, SWT.NONE);
		directionsLabel.setText("Select the Component that you want to be the Assembly Controller for your Waveform:");
		GridDataFactory.generate(directionsLabel, 2, 1);

		this.tableViewer = new TableViewer(new Table(composite, SWT.BORDER));
		final GridData data = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		data.heightHint = ScaWaveformProjectAssemblyControllerWizardPage.TABLE_HEIGHT_HINT;
		this.tableViewer.getControl().setLayoutData(data);

		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());

		this.tableViewer.setContentProvider(new ArrayContentProvider());
		this.tableViewer.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(factory),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public String getText(final Object element) {
				if (element instanceof SoftPkgImpl) {
					final SoftPkgImpl softPkg = (SoftPkgImpl) element;
					final URI uri = softPkg.eResource().getURI();
					return softPkg.getName() + " (" + uri.path().replace(uri.lastSegment(), "") + ")";
				}

				return "";
			}

		});

		if (this.components != null) {
			this.tableViewer.setInput(this.components);
		}
		setControl(composite);
	}

	public SoftPkg getAssemblyController() {
		return (SoftPkg) ((IStructuredSelection) this.tableViewer.getSelection()).getFirstElement();
	}

	public void setComponents(final List<SoftPkg> components) {
		// Make a new ArrayList, don't mess with the passed in list
		this.components = new ArrayList<SoftPkg>(components);

		Collections.sort(this.components, new Comparator<SoftPkg>() {
			@Override
			public int compare(final SoftPkg o1, final SoftPkg o2) {
				final String s1 = o1.getName();
				final String s2 = o2.getName();

				if (s1 == null) {
					if (s2 == null) {
						return 0;
					} else {
						return 1;
					}
				} else if (s2 == null) {
					return -1;
				} else {
					return s1.compareToIgnoreCase(s2);
				}
			}
		});

		if (this.tableViewer != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!tableViewer.getControl().isDisposed()) {
						tableViewer.setInput(ScaWaveformProjectAssemblyControllerWizardPage.this.components);
					}
				}
			});
		}
	}
}
