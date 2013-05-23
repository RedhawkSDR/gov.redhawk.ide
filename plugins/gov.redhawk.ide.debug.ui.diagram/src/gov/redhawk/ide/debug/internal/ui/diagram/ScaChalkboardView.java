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
  // BEGIN GENERATED CODE

package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.ide.debug.internal.ui.LocalScaElementFactory;
import gov.redhawk.model.sca.util.ScaTransactionEditingDomainFactory;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class ScaChalkboardView extends ViewPart {

	private LocalScaEditor diagram;
	private ViewEditorSite editorSite;
	private IEditorInput editorInput;

	IEditingDomainProvider editingDomainProvider = new IEditingDomainProvider() {
		ScaTransactionEditingDomainFactory factory = new ScaTransactionEditingDomainFactory();

		public EditingDomain getEditingDomain() {
			return factory.createEditingDomain();
		}
	};
	
	public ScaChalkboardView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		editorSite = new ViewEditorSite(this.getSite());
		//editorInput = new URIEditorInput(ScaDebugPlugin.getInstance().getLocalSca().eResource().getURI());
		editorInput = LocalScaElementFactory.getLocalScaInput();
		diagram = new LocalScaEditor();
		try {
			diagram.init(editorSite, editorInput);
			diagram.createPartControl(parent);
		} catch (PartInitException e) {
			// TODO show the error in the window

			// BEGIN DEBUG CODE
			e.printStackTrace();
			// END DEBUG CODE
		}

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IEditingDomainProvider.class) {
			return editingDomainProvider;
		} else {
			return super.getAdapter(adapter);
		}
	}

	class ViewEditorSite implements IEditorSite {

		IWorkbenchPartSite site;

		IActionBars bars;

		IEditorActionBarContributor nullActionBarContributor = new IEditorActionBarContributor() {

			public void setActiveEditor(IEditorPart targetEditor) {
			}

			public void init(IActionBars bars, IWorkbenchPage page) {
				ViewEditorSite.this.bars = bars;
			}

			public void dispose() {
			}
		};

		public ViewEditorSite(IWorkbenchPartSite site) {
			this.site = site;
		}

		// Methods not found in IWorkbenchPartSite
		public IEditorActionBarContributor getActionBarContributor() {
			return nullActionBarContributor;
		}

		public IActionBars getActionBars() {
			return bars;
		}

		// Methods found in IWorkbenchPartSite
		public String getId() {
			return site.getId();
		}

		public String getPluginId() {
			return site.getPluginId();
		}

		public String getRegisteredName() {
			return site.getRegisteredName();
		}

		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
			site.registerContextMenu(menuId, menuManager, selectionProvider);
		}

		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
			site.registerContextMenu(menuManager, selectionProvider);
		}

		public IKeyBindingService getKeyBindingService() {
			return site.getKeyBindingService();
		}

		public IWorkbenchPart getPart() {
			return site.getPart();
		}

		public IWorkbenchPage getPage() {
			return site.getPage();
		}

		public ISelectionProvider getSelectionProvider() {
			return site.getSelectionProvider();
		}

		public Shell getShell() {
			return site.getShell();
		}

		public IWorkbenchWindow getWorkbenchWindow() {
			return site.getWorkbenchWindow();
		}

		public void setSelectionProvider(ISelectionProvider provider) {
			site.setSelectionProvider(provider);
		}

		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			return site.getAdapter(adapter);
		}

		public Object getService(@SuppressWarnings("rawtypes") Class api) {
			return site.getService(api);
		}

		public boolean hasService(@SuppressWarnings("rawtypes") Class api) {
			return site.hasService(api);
		}

		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			site.registerContextMenu(menuManager, selectionProvider);
		}

		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			site.registerContextMenu(menuId, menuManager, selectionProvider);
		}

	}
}
