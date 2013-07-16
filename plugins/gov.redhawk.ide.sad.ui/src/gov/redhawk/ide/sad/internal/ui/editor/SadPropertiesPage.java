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
package gov.redhawk.ide.sad.internal.ui.editor;

import gov.redhawk.ide.sad.internal.ui.properties.PropertiesContentProvider;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewer;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerControlFactory;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerConverter;
import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewerLabelProvider;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerControlFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerEditAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * 
 */
public class SadPropertiesPage extends ScaFormPage {

	private PropertiesViewer viewer;
	private List<ViewerComponent> properties = new ArrayList<ViewerComponent>();

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public SadPropertiesPage(SCAFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * @param editor
	 * @param id
	 * @param title
	 * @param newStyleHeader
	 */
	public SadPropertiesPage(SCAFormEditor editor, String id, String title, boolean newStyleHeader) {
		super(editor, id, title, newStyleHeader);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.setText("Properties");
		createViewer(managedForm, managedForm.getForm().getBody());
		super.createFormContent(managedForm);
	}

	private void createViewer(IManagedForm managedForm, Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		viewer = new PropertiesViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewer.setContentProvider(new PropertiesContentProvider());
		viewer.setLabelProvider(new PropertiesViewerLabelProvider(viewer));
		
		XViewerControlFactory cFactory = new PropertiesViewerControlFactory();
		XViewerConverter converter = new PropertiesViewerConverter();
		viewer.setXViewerEditAdapter(new XViewerEditAdapter(cFactory,
				converter));
		
		viewer.setInput(properties);
		viewer.expandToLevel(2);
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ui.editor.ScaFormPage#refresh(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	protected void refresh(Resource resource) {
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		properties.clear();
		for (SadComponentInstantiation inst : sad.getAllComponentInstantiations()) {
			properties.add(new ViewerComponent(inst));
		}
		if (viewer != null) {
			viewer.setInput(properties);
			viewer.expandToLevel(2);
		}
	}

}
