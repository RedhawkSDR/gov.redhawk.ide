/**
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
 * 
 */
package $packageName$;

import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.observables.SCAObservables;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class $compositeName$ extends Composite {

	
	private $resourceClassName$ input;
%if viewer 
	private ScaItemProviderAdapterFactory adapterFactory = new ScaItemProviderAdapterFactory();
	private TreeViewer viewer;
%endif

%if propertyStubs
	private EMFDataBindingContext context;
	private class PropertyField {
$propertyFields$
	}
	private PropertyField fields = new PropertyField();
%endif

	public $compositeName$(Composite parent, int style) {
		super(parent, style);
		createPartControl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(final Composite main) {
		main.setLayout(new GridLayout(2, false));

%if propertyStubs
		Group controlGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		controlGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		controlGroup.setText("Controls");
		createControlGroup(controlGroup);
%endif

%if viewer
		Group viewerGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		viewerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewerGroup.setText("Viewer");
		createViewer(viewerGroup);
%endif
	}

%if propertyStubs
	private void createControlGroup(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		Label label;
		
$controlGroupCode$
	}
%endif

%if viewer
	/**
	 * TODO: Sample use of Viewer and adapter Factories, safe to delete this method
	 */
	private void createViewer(Composite parent) {
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		parent.setLayout(layout);
		// Sample Viewer
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		if (getInput() != null) {
			viewer.setInput(getInput());
		}
	}
%endif

	
	@Override
	public void dispose() {
%if propertyStubs
		if (this.context != null) {
			context.dispose();
			context = null;
		}
%endif
%if viewer 
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
%endif
		super.dispose();
	}

	public void setInput($resourceClassName$ input) {
		this.input = input;
%if propertyStubs
		if (this.context != null) {
			context.dispose();
			context = null;
		}
		if (this.input != null) {
			context = new EMFDataBindingContext();
			addBindings();
		}
%endif
		
%if viewer
		viewer.setInput(input);
% endif
	}

%if propertyStubs
	private void addBindings() {
		ScaSimpleProperty simpleProp;
$bindingsCode$
	}
%endif

	public $resourceClassName$ getInput() {
		return input;
	}

}
