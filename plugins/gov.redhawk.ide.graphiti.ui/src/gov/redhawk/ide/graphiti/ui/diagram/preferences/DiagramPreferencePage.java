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
package gov.redhawk.ide.graphiti.ui.diagram.preferences;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 *
 */
public class DiagramPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {


	/**
	 *
	 */
	public DiagramPreferencePage() {
		super("Graphiti Diagram", FieldEditorPreferencePage.GRID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(GraphitiUIPlugin.getDefault().getPreferenceStore());
		setDescription("Settings for Graphiti Diagrams.");
	}

	@Override
	protected void createFieldEditors() {

		final Composite parent = getFieldEditorParent();
		addField(new BooleanFieldEditor(DiagramPreferenceConstants.HIDE_DETAILS, "Hide Shape Details", parent));
		addField(new BooleanFieldEditor(DiagramPreferenceConstants.HIDE_UNUSED_PORTS, "Hide Unused Ports", parent));
		
	}


}
