/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaPropertyContainer;
import gov.redhawk.sca.ui.ScaComponentFactory;
import gov.redhawk.sca.ui.properties.ScaPropertiesAdapterFactory;

import java.util.Collections;

import mil.jpeojtrs.sca.prf.util.PropertiesUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AbstractPropertyEditWizardPage<T extends ScaPropertyContainer<?, ?>> extends WizardPage {
	private final AdapterFactory adapterFactory;
	private TreeViewer viewer;
	private T propertyContainer;

	public AbstractPropertyEditWizardPage(final String pageName) {
		super(pageName, "Assign Initial Properties", null);
		this.setDescription("Provide the initial configuration");
		this.adapterFactory = new ScaPropertiesAdapterFactory();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout());
		final Composite propComposite = new Composite(main, SWT.None);
		propComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		this.viewer = ScaComponentFactory.createPropertyTable(propComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE, this.adapterFactory);
		this.viewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof ScaAbstractProperty< ? >) {
					ScaAbstractProperty< ? > prop = (ScaAbstractProperty< ? >) element;
					return PropertiesUtil.canOverride(prop.getDefinition());
				}
				return false;
			}
		});
		
		final Button resetButton = new Button(main, SWT.PUSH);
		resetButton.setText("Reset");
		resetButton.setToolTipText("Reset all the property values to default");
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final ScaAbstractProperty< ? > prop : propertyContainer.getProperties()) {
					prop.restoreDefaultValue();
				}
			}
		});
		resetButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.FILL).create());

		setControl(main);
	}
	
	public void setPropertyContainer(T propertyContainer) {
		if (this.propertyContainer == propertyContainer) {
			return;
		} else {
			if (this.propertyContainer != null) {
				this.propertyContainer.dispose();
			}
			this.propertyContainer = null;
		}
		this.propertyContainer = propertyContainer;
		if (this.propertyContainer != null) {
			this.viewer.setInput(this.propertyContainer);
		} else {
			this.viewer.setInput(Collections.emptyList());
		}
	}
	
	@Override
	public void dispose() {
		if (this.propertyContainer != null) {
			this.propertyContainer.dispose();
			this.propertyContainer = null;
		}
		super.dispose();
	}


	public T getPropertyContainer() {
		return propertyContainer;
	}
}
