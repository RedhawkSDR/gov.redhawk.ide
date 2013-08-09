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
package gov.redhawk.datareader.ui.controlPanels;

import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.sca.ui.editors.AbstractScaContentEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * An example showing how to create a control panel.
 */
public class DataReaderControlPanelEditor extends AbstractScaContentEditor<ScaComponent> {

	private DataReaderComposite readerControls;

	public DataReaderControlPanelEditor() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite main) {
		main.setLayout(new FillLayout());
		readerControls = new DataReaderComposite(main, SWT.None);
		readerControls.setInput(getInput());
		//		 TODO Plot?
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		if (readerControls != null) {
			readerControls.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<ScaComponent> getInputType() {
		return ScaComponent.class;
	}

}
