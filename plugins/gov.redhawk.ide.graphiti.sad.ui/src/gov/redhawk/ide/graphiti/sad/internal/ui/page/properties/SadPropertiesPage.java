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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties;

import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model.SadPropertiesAdapterFactory;
import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model.SadProperty;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerEditAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SadPropertiesPage extends ScaFormPage {

	private static final String TOOLBAR_ID = "gov.redhawk.ide.sad.internal.ui.editor.properties.toolbar";
	private PropertiesViewer viewer;
	private IAction expandAllAction = new Action() {
		{
			setToolTipText("Expand All");
			final ImageDescriptor expandAllImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(SADUIGraphitiPlugin.PLUGIN_ID,
				"icons/full/obj16/expandall.gif");
			setImageDescriptor(expandAllImageDescriptor);
		}

		@Override
		public void run() {
			viewer.expandAll();
		}
	};
	private IAction collapseAllAction = new Action() {
		{
			setToolTipText("Collapse All");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
			setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
		}

		@Override
		public void run() {
			viewer.collapseAll();
		}
	};

	private SadPropertiesAdapterFactory adapterFactory = null;

	public SadPropertiesPage(SCAFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public SadPropertiesPage(SCAFormEditor editor, String id, String title, boolean newStyleHeader) {
		super(editor, id, title, newStyleHeader);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (viewer != null) {
			viewer.dispose();
			viewer = null;
		}
		if (adapterFactory != null) {
			adapterFactory.dispose();
			adapterFactory = null;
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.setText("Properties");
		createViewer(managedForm, managedForm.getForm().getBody());
		addActions(managedForm.getForm().getToolBarManager());
		viewer.getControl().setFocus();
		super.createFormContent(managedForm);

		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + SadPropertiesPage.TOOLBAR_ID);
		manager.update(true);
	}

	private void addActions(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator("Action"));
		toolBarManager.add(expandAllAction);
		toolBarManager.add(collapseAllAction);
		if (viewer != null) {
			IAction configureAction = viewer.getCustomizeAction();
			toolBarManager.add(configureAction);
		}
	}

	private void createViewer(IManagedForm managedForm, Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		viewer = new PropertiesViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		adapterFactory = new SadPropertiesAdapterFactory();
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		PropertiesViewerLabelProvider labelProvider = new PropertiesViewerLabelProvider(viewer);
		viewer.setLabelProvider(labelProvider);
		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof SadProperty) {
					if (parentElement instanceof SadProperty) {
						// Props that are children of props are members, and hence are always shown
						return true;
					}

					// Things you can override + anything that could be an external property
					AbstractProperty propDef = ((SadProperty) element).getDefinition();
					return (propDef == null) || PropertiesUtil.canOverride(propDef)
						|| propDef.isKind(PropertyConfigurationType.PROPERTY, PropertyConfigurationType.CONFIGURE);
				} else if (element instanceof SadComponentInstantiation) {
					return true;
				}
				return false;
			}
		});

		Resource resource = getInput();
		if (resource != null) {
			SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(getInput());
			viewer.setInput(sad);
		}

		PropertiesViewerEditingSupport editingSupport = new PropertiesViewerEditingSupport();
		viewer.setXViewerEditAdapter(new XViewerEditAdapter(editingSupport, editingSupport));
	}

	@Override
	protected void refresh(Resource resource) {
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		if (viewer != null) {
			viewer.setInput(sad);
		}
	}

	@Override
	public void updateFormSelection() {
		super.updateFormSelection();
		viewer.refresh();
		viewer.expandToLevel(2);
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	@Override
	protected Control getFocusControl() {
		if (viewer != null) {
			return viewer.getControl();
		}
		return super.getFocusControl();
	}

}
