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

package gov.redhawk.ide.sad.graphiti.debug.internal.ui;

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

public class MultiPageScaChalkboardView extends ViewPart {

	private LocalGraphitiSadMultiPageScaEditor diagram;
	private ViewEditorSite editorSite;
	private IEditorInput editorInput;

	IEditingDomainProvider editingDomainProvider = new IEditingDomainProvider() {
		ScaTransactionEditingDomainFactory factory = new ScaTransactionEditingDomainFactory();

		@Override
		public EditingDomain getEditingDomain() {
			return factory.createEditingDomain();
		}
	};
	
	public MultiPageScaChalkboardView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		editorSite = new ViewEditorSite(this.getSite());
		//editorInput = new URIEditorInput(ScaDebugPlugin.getInstance().getLocalSca().eResource().getURI());
		editorInput = LocalScaElementFactory.getLocalScaInput();
		diagram = new LocalGraphitiSadMultiPageScaEditor();
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
	
	@Override
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

			@Override
			public void setActiveEditor(IEditorPart targetEditor) {
			}

			@Override
			public void init(IActionBars bars, IWorkbenchPage page) {
				ViewEditorSite.this.bars = bars;
			}

			@Override
			public void dispose() {
			}
		};

		public ViewEditorSite(IWorkbenchPartSite site) {
			this.site = site;
		}

		// Methods not found in IWorkbenchPartSite
		@Override
		public IEditorActionBarContributor getActionBarContributor() {
			return nullActionBarContributor;
		}

		@Override
		public IActionBars getActionBars() {
			return bars;
		}

		// Methods found in IWorkbenchPartSite
		@Override
		public String getId() {
			return site.getId();
		}

		@Override
		public String getPluginId() {
			return site.getPluginId();
		}

		@Override
		public String getRegisteredName() {
			return site.getRegisteredName();
		}

		@Override
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
			site.registerContextMenu(menuId, menuManager, selectionProvider);
		}

		@Override
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
			site.registerContextMenu(menuManager, selectionProvider);
		}

		@Override
		@Deprecated
		public IKeyBindingService getKeyBindingService() {
			return site.getKeyBindingService();
		}

		@Override
		public IWorkbenchPart getPart() {
			return site.getPart();
		}

		@Override
		public IWorkbenchPage getPage() {
			return site.getPage();
		}

		@Override
		public ISelectionProvider getSelectionProvider() {
			return site.getSelectionProvider();
		}

		@Override
		public Shell getShell() {
			return site.getShell();
		}

		@Override
		public IWorkbenchWindow getWorkbenchWindow() {
			return site.getWorkbenchWindow();
		}

		@Override
		public void setSelectionProvider(ISelectionProvider provider) {
			site.setSelectionProvider(provider);
		}

		@Override
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			return site.getAdapter(adapter);
		}

		@Override
		public Object getService(@SuppressWarnings("rawtypes") Class api) {
			return site.getService(api);
		}

		@Override
		public boolean hasService(@SuppressWarnings("rawtypes") Class api) {
			return site.hasService(api);
		}

		@Override
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			site.registerContextMenu(menuManager, selectionProvider);
		}

		@Override
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			site.registerContextMenu(menuId, menuManager, selectionProvider);
		}

	}
}
