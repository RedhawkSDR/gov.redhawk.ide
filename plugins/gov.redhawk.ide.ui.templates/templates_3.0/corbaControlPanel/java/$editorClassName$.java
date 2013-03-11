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
package $packageName$;

import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.sca.ui.editors.AbstractScaContentEditor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * An example showing how to create a control panel.
 */
public class $editorClassName$ extends AbstractScaContentEditor {
	
	public $editorClassName$() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite main) {
		// TODO Add control panel controls here
		main.setLayout(new GridLayout(2, false));

		Group controlGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		controlGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		controlGroup.setText("Controls");
		createControlGroup(controlGroup);

		Group viewerGroup = new Group(main, SWT.SHADOW_ETCHED_IN);
		viewerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewerGroup.setText("Viewer");
		createViewer(viewerGroup);

	}

	private void createControlGroup(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		// Sample Controls
		EObject input = getInput();

		// Here we cast our input to the type we are expecting
		if (input instanceof CorbaObjWrapper< ? >) {
			CorbaObjWrapper< ? > wrapper = (CorbaObjWrapper< ? >) input;
			org.omg.CORBA.Object corbaObj = wrapper.getObj();

			// NOTE: Don't forget to Narrow First!
			Label label = new Label(parent, SWT.None);
			label.setText("IOR:");

			Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
			text.setText(corbaObj.toString());
		}

	}

	private void createViewer(Composite parent) {
		parent.setLayout(new FillLayout());
		// Sample Viewer
		TreeViewer viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AdapterFactoryContentProvider(this.getAdapterFactory()));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(this.getAdapterFactory()));
		viewer.setInput(getInput());

		getSite().setSelectionProvider(viewer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// init override necessary for WindowBuilder support
		// TODO Customize based on expected into type.  If input is incorrect throw and part init exception
		super.init(site, input);
	}
}
