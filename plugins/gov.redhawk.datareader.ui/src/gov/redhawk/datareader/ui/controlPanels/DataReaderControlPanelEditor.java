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

import gov.redhawk.model.sca.IRefreshable;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.sca.ui.editors.AbstractScaContentEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.progress.UIJob;

import CF.Resource;

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
