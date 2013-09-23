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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import gov.redhawk.ide.sdr.SdrRoot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @since 1.2
 */
public class LaunchDeviceManagerDialog extends CheckedTreeSelectionDialog {

	private ComboViewer debugViewer;

	private Label label;

	private static HashMap<DeviceConfiguration, Integer> debugMap = new HashMap<DeviceConfiguration, Integer>();

	private final String[] debugLevels = new String[] { "Fatal", "Error", "Warn", "Info", "Debug", "Trace" };

	public LaunchDeviceManagerDialog(final Shell parentShell, final AdapterFactory adapterFactory) {
		super(parentShell, LaunchDeviceManagerDialog.getLabelProvider(adapterFactory), LaunchDeviceManagerDialog.getContentProvider(adapterFactory));
		this.setTitle("Launch Device Manager");
		this.setMessage("Select Device Manager(s) to Launch");
		setComparator(new ViewerComparator());
	}

	private static ILabelProvider getLabelProvider(final AdapterFactory adapterFactory) {
		return new AdapterFactoryLabelProvider(adapterFactory);
	}

	private static ITreeContentProvider getContentProvider(final AdapterFactory adapterFactory) {
		return new ITreeContentProvider() {

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public boolean hasChildren(final Object element) {
				return false;
			}

			@Override
			public Object getParent(final Object element) {
				return null;
			}

			@Override
			public Object[] getChildren(final Object parentElement) {
				if (parentElement instanceof SdrRoot) {
					for (final Object devConfig : ((SdrRoot) parentElement).getNodesContainer().getNodes().toArray()) {
						LaunchDeviceManagerDialog.debugMap.put((DeviceConfiguration) devConfig, 3); // SUPPRESS CHECKSTYLE MagicNumber
					}

					return ((SdrRoot) parentElement).getNodesContainer().getNodes().toArray();
				}
				return Collections.emptyList().toArray();
			}
		};
	}

	@Override
	protected Composite createSelectionButtons(final Composite parent) {
		final Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(2, true));
		final Control controls = super.createSelectionButtons(root);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite subContainer = new Composite(root, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(2, false);

		subContainer.setLayout(gridLayout);

		this.label = new Label(subContainer, SWT.NULL);
		this.label.setText("Debug Level: ");

		this.debugViewer = new ComboViewer(subContainer, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN | SWT.BORDER);
		this.debugViewer.setContentProvider(new ArrayContentProvider());
		this.debugViewer.setInput(this.debugLevels);
		this.debugViewer.setSelection(new StructuredSelection("Info"));
		this.debugViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				final String choice = (String) sel.getFirstElement();
				final IStructuredSelection treeSelection = (IStructuredSelection) getTreeViewer().getSelection();
				final DeviceConfiguration device = (DeviceConfiguration) treeSelection.getFirstElement();

				LaunchDeviceManagerDialog.debugMap.put(device, Arrays.asList(LaunchDeviceManagerDialog.this.debugLevels).indexOf(choice));
			}
		});

		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				final DeviceConfiguration choice = (DeviceConfiguration) sel.getFirstElement();

				if (LaunchDeviceManagerDialog.debugMap.containsKey(choice)) {
					LaunchDeviceManagerDialog.this.debugViewer.setSelection(new StructuredSelection(
					        LaunchDeviceManagerDialog.this.debugLevels[LaunchDeviceManagerDialog.debugMap.get(choice)]));
				}
			}
		});

		return root;
	}

	@Override
	protected void okPressed() {
		final HashMap<DeviceConfiguration, Integer> tempMap = new HashMap<DeviceConfiguration, Integer>();

		for (final Object obj : getTreeViewer().getCheckedElements()) {
			final DeviceConfiguration device = (DeviceConfiguration) obj;
			tempMap.put(device, LaunchDeviceManagerDialog.debugMap.get(device));
		}

		LaunchDeviceManagerDialog.debugMap.clear();
		LaunchDeviceManagerDialog.debugMap = tempMap;

		super.okPressed();
	}

	@Override
	public Object[] getResult() {
		return new Object[] { LaunchDeviceManagerDialog.debugMap };
	}
}
