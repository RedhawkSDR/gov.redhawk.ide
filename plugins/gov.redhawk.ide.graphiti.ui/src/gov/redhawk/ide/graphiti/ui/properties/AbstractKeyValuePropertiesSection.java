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
package gov.redhawk.ide.graphiti.ui.properties;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import gov.redhawk.core.graphiti.ui.properties.AbstractPropertiesSection;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.partitioning.Requires;

/**
 * Abstract class for property sections designed to display 0-to-many Key/Value pairings
 */
public abstract class AbstractKeyValuePropertiesSection extends AbstractPropertiesSection {

	/** Maintain reference to the actively selected EObject so we can remove our content adapter when necessary **/
	private EObject activeEObj;

	private AbstractKeyValuePropertiesComposite sectionComposite;

	private Button addButton;
	private Button removeButton;

	/** Flag that can be set so that GUI driven changes don't create a loop with our adapter **/
	private boolean userEdit = false;

	/**
	 * EContentAdapter to listen for changes in the model element. Includes logic to avoid cyclical updates caused by UI
	 * edits
	 */
	private EContentAdapter contentAdapter = new EContentAdapter() {

		@Override
		public void notifyChanged(Notification notification) {
			super.notifyChanged(notification);
			if (userEdit) {
				return;
			}

			if (!getTreeViewer().getTree().isDisposed()) {
				getTreeViewer().setInput(getEObject());
			}
		}

		@Override
		protected void addAdapter(Notifier notifier) {
			if (notifier instanceof Requirements || notifier instanceof Requires) {
				super.addAdapter(notifier);
			}
		}
	};

	/**
	 * Return a {@link AbstractKeyValuePropertiesComposite} that will display the key/value pairings of the input
	 * @param parent - Parent composite
	 * @param style - Defaults to: SWT.None
	 * @param treeStyle - Defaults to: SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION
	 * @return
	 */
	protected abstract AbstractKeyValuePropertiesComposite getTreeComposite(Composite parent, int style, int treeStyle);

	/**
	 * Add a new underlying element to the input source
	 */
	protected abstract Command createAddCommand();

	/**
	 * Remove the selected element from the input source
	 */
	protected abstract Command createRemoveCommand();

	/**
	 * @return String to be used as part of the add/remove button tooltips
	 */
	protected abstract String getToolTipSuffix();

	@Override
	public void createControls(Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		int treeStyle = getWidgetFactory().getOrientation() | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION;

		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// Composite containing buttons used to add/remove requires elements
		createButtonComposite(parent);

		// Composite containing the requirements table
		sectionComposite = getTreeComposite(parent, SWT.None, treeStyle);

		// Add a selection change listener to handle button enablement
		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateControls();
			}
		});
	}

	/**
	 * Create the composite that will contain buttons for adding/removing {@link Requires} elements
	 */
	private void createButtonComposite(Composite parent) {
		Composite actionComposite = new Composite(parent, SWT.NONE);
		actionComposite.setLayout(new GridLayout());
		actionComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).create());
		actionComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		Image addImage = null;
		Image removeImage = null;
		try {
			URL url = FileLocator.find(GraphitiUIPlugin.getDefault().getBundle(), new Path("$nl$/icons/obj16/add.gif"), null);
			addImage = new Image(Display.getCurrent(), url.openStream());
			url = FileLocator.find(GraphitiUIPlugin.getDefault().getBundle(), new Path("$nl$/icons/obj16/remove.gif"), null);
			removeImage = new Image(Display.getCurrent(), url.openStream());
		} catch (IOException e) {
			// PASS
		}

		addButton = new Button(actionComposite, SWT.PUSH);
		addButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		addButton.setToolTipText("Add " + getToolTipSuffix());
		addButton.setImage(addImage);

		removeButton = new Button(actionComposite, SWT.PUSH);
		removeButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		removeButton.setToolTipText("Remove " + getToolTipSuffix());
		removeButton.setImage(removeImage);

		// Add button selection listeners
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Command command = createAddCommand();

				userEdit = true;
				getEditingDomain().getCommandStack().execute(command);
				userEdit = false;
				getTreeViewer().refresh();

				// Automatically select the most recently added item
				int size = getTreeViewer().getTree().getItemCount();
				getTreeViewer().getTree().setSelection(getTreeViewer().getTree().getItem(size - 1));

				updateControls();
			}
		});

		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Command command = createRemoveCommand();

				// Table only allows a single selection, so safe to assume the item we want is at index[0]
				int selIndex = getTreeViewer().getTree().indexOf(getTreeViewer().getTree().getSelection()[0]);

				userEdit = true;
				getEditingDomain().getCommandStack().execute(command);
				userEdit = false;
				getTreeViewer().refresh();

				// Update the selection to be the preceding item if it exists, otherwise select the top item
				if (selIndex > 0) {
					TreeItem item = getTreeViewer().getTree().getItem(selIndex - 1);
					getTreeViewer().getTree().select(item);
				} else if (getTreeViewer().getTree().getItems().length > 0) {
					TreeItem item = getTreeViewer().getTree().getItem(0);
					getTreeViewer().getTree().select(item);
				}

				updateControls();
			}
		});
	}

	/**
	 * Currently just controls whether or not the remove button is enabled.
	 * Can be expanded in the future as necessary.
	 */
	private void updateControls() {
		int numItems = getTreeViewer().getTree().getItems().length;
		if (numItems == 0 || getTreeViewer().getSelection().isEmpty()) {
			removeButton.setEnabled(false);
		} else if (!removeButton.isEnabled()) {
			removeButton.setEnabled(true);
		}
	}

	public TreeViewer getTreeViewer() {
		return sectionComposite.getTreeViewer();
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (activeEObj != null && activeEObj != getEObject()) {
			contentAdapter.unsetTarget(activeEObj);
			activeEObj.eAdapters().remove(contentAdapter);
		}

		super.setInput(part, selection);

		// Update button state
		updateControls();

		if (activeEObj != getEObject()) {
			activeEObj = getEObject();
			contentAdapter.setTarget(activeEObj);
			activeEObj.eAdapters().add(contentAdapter);
		}
	}

	@Override
	public void dispose() {
		if (activeEObj != null) {
			contentAdapter.unsetTarget(activeEObj);
			activeEObj.eAdapters().remove(contentAdapter);
		}
		super.dispose();
	}

	@Override
	protected EObject unwrap(Object object) {
		if (object instanceof GraphitiShapeEditPart) {
			object = ((GraphitiShapeEditPart) object).getModel();
		}
		if (object instanceof PictogramElement) {
			return ((PictogramElement) object).getLink().getBusinessObjects().get(0);
		}
		return super.unwrap(object);
	}
}
