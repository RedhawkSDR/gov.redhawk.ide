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


import gov.redhawk.model.sca.IRefreshable;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDevice;
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
public class $editorClassName$ extends AbstractScaContentEditor<ScaDevice> {
	private TreeViewer viewer;
	private Text text;

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
		
		Group viewerGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		viewerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewerGroup.setText("Viewer");
		createViewer(viewerGroup);
		
	}

	private void createControlGroup(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		// Sample Controls
		EObject input = getInput();
		
		Label label = new Label(parent, SWT.None);
		label.setText("Component:");
		
		text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		// Here we cast our input to the type we are expecting
		if (input instanceof ScaDevice) {
			ScaDevice device = (ScaDevice) input;
			Resource resource = (Resource) device.getObj();
			text.setText(resource.identifier());
		}
		
	}

	private void createViewer(Composite parent) {
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		parent.setLayout(layout);
		// Sample Viewer
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AdapterFactoryContentProvider(this.getAdapterFactory()));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(this.getAdapterFactory()));
		viewer.setInput(getInput());
		
		if (getSite() != null) {
			getSite().setSelectionProvider(viewer);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// init override necessary for WindowBuilder support
		// TODO Customize based on expected into type.  If input is incorrect throw and part init exception
		super.init(site, input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<ScaDevice> getInputType() {
		return ScaDevice.class;
	}

	/**
	 * This is used to update the input when the control panel lives in a
	 * property tab.
	 * @param selection the selected element, better be a ScaComponent object
	 */
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (!sel.isEmpty()) {
				if (sel.getFirstElement() instanceof ScaDevice) {
					ScaDevice device = (ScaDevice) sel.getFirstElement();
					viewer.setInput(device);
					if (text != null) {
						Resource resource = (Resource) device.getObj();
						text.setText(resource.identifier());
					}
				}
			}
		}
    }

	/**
	 * This is used to refresh the control panel when it lives in a property tab.
	 */
	public void refresh() {
		Job refreshJob = new Job("Refresh Device") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
			    try {
	                ((IRefreshable) viewer.getInput()).refresh(monitor, RefreshDepth.CHILDREN);
				    return Status.OK_STATUS;
                } catch (InterruptedException e) {
	                return Status.CANCEL_STATUS;
                }
			}
		};
		refreshJob.setSystem(true);
		refreshJob.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				new UIJob("Refresh TreeViewer") {
					public IStatus runInUIThread(final IProgressMonitor monitor) {
						if (!viewer.getControl().isDisposed()) {
							viewer.refresh();
						}
						return Status.OK_STATUS;
					}
				}.schedule();
			}
		});
		refreshJob.schedule();
    }
}
