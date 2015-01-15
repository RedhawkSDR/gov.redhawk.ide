/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.DcdDiagramUtilHelper;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.GraphitiDcdDiagramEditor;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.DCDDiagramTypeProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import gov.redhawk.ide.internal.ui.handlers.CleanUpComponentFilesAction;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.prf.provider.PrfItemProviderAdapterFactory;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

public class GraphitiDcdMultipageEditor extends AbstractGraphitiMultiPageEditor implements ITabbedPropertySheetPageContributor, IViewerProvider {

	public static final String ID = "gov.redhawk.ide.graphiti.dcd.ui.editor.DcdEditor";

	public static final String EDITING_DOMAIN_ID = "mil.jpeojtrs.sca.dcd.diagram.EditingDomain";

	private ResourceListener nameListener;
	private GraphitiDcdDevicesPage dcdDevicesPage;

	private class ResourceListener extends AdapterImpl {
		private DeviceConfiguration dcd;
		private final Resource dcdResource;

		public ResourceListener(final Resource dcdResource) {
			this.dcdResource = dcdResource;
			if (this.dcdResource != null) {
				this.dcdResource.eAdapters().add(this);
				this.dcd = getDeviceConfiguration();
				if (this.dcd != null) {
					this.dcd.eAdapters().add(this);
					updateTitle();
				}
			}
		}

		/**
		 * Gets the soft pkg.
		 * 
		 * @return the soft pkg
		 */
		private DeviceConfiguration getDeviceConfiguration() {
			return ModelUtil.getDeviceConfiguration(this.dcdResource);
		}

		public void dispose() {
			if (this.dcd != null) {
				this.dcd.eAdapters().remove(this);
			}
			if (this.dcdResource != null) {
				this.dcdResource.eAdapters().remove(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getNotifier() instanceof Resource) {
				switch (msg.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
					if (this.dcd != null) {
						this.dcd.eAdapters().remove(this);
						this.dcd = null;
					}
					if (this.dcdResource.isLoaded()) {
						this.dcd = getDeviceConfiguration();
						if (this.dcd != null) {
							this.dcd.eAdapters().add(this);

							updateTitle();
						}
					}
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof DeviceConfiguration) {
				final int featureID = msg.getFeatureID(DeviceConfiguration.class);

				if (featureID == DcdPackage.DEVICE_CONFIGURATION__NAME) {
					if (msg.getEventType() == Notification.SET) {
						updateTitle();
					}
				}
			}
		}
	}

	public GraphitiDcdMultipageEditor() {
		super();
	}

	/**
	 * This sets the selection into whichever viewer is active.
	 */
	@Override
	public void setSelectionToViewer(final Collection< ? > collection) {
		final Collection< ? > theSelection = collection;
		// Make sure it's okay.
		//
		if (theSelection != null && !theSelection.isEmpty()) {
			// I don't know if this should be run this deferred
			// because we might have to give the editor a chance to process the
			// viewer update events
			// and hence to update the views first.
			//
			//
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					//
					if (getViewer() != null) {
						getViewer().setSelection(new StructuredSelection(theSelection.toArray()), true);
					}
				}
			};
			runnable.run();
		}
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class key) {
		if (key.equals(IGotoMarker.class)) {
			return this;
		} else {
			return super.getAdapter(key);
		}
	}



	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help
	 * fill the context menus with contributions from the Edit menu.
	 */
	@Override
	public void menuAboutToShow(final IMenuManager menuManager) {
		((IMenuListener) getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
	}

	@Override
	public void dispose() {

		if (this.nameListener != null) {
			this.nameListener.dispose();
			this.nameListener = null;
		}
		super.dispose();
	}

	@Override
	public String getTitle() {
		String name = null;
		final DeviceConfiguration dcd = getDeviceConfiguration();
		if (dcd != null) {
			name = dcd.getName();
			if (name == null) {
				name = getEditorInput().getName();
			}
		}
		if (name != null) {
			return name;
		} else {
			return "";
		}
	}

	private DeviceConfiguration getDeviceConfiguration() {
		return ModelUtil.getDeviceConfiguration(getMainResource());
	}

	@Override
	protected void addPages() {
		// Only creates the other pages if there is something that can be edited
		//
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource dcdResource = getMainResource();

				addNameListener(dcdResource);

				final IFormPage overviewPage = createOverviewPage(dcdResource);
				setOverviewPage(overviewPage);
				this.addPage(overviewPage);

				dcdDevicesPage = new GraphitiDcdDevicesPage(this);
				dcdDevicesPage.setInput(dcdResource);
				addPage(dcdDevicesPage);

				// This is the page for the graphical diagram viewer
				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput diagramInput = createDiagramInput(dcdResource);
				pageIndex = addPage(editor, diagramInput);
				setPageText(pageIndex, "Diagram");

				// TODO: Do we need to set layout like in SAD Diagram?
				// set layout for target-sdr editors
				// DUtil.layout(editor);

				IEditorPart textEditor = createTextEditor();
				setTextEditor(textEditor);
				if (textEditor != null) {
					final int dcdSourcePageNum = addPage(-1, textEditor, getEditorInput(), dcdResource);
					this.setPageText(dcdSourcePageNum, getEditorInput().getName());
				}

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add pages.", e));
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add pages.", e));
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add pages.", e));
			}
		}
	}

	protected void addNameListener(final Resource dcdResource) {
		this.nameListener = new ResourceListener(dcdResource);
	}
	
	protected IEditorInput createDiagramInput(final Resource dcdResource) throws IOException, CoreException {
		final URI diagramURI = DUtil.getDiagramResourceURI(DcdDiagramUtilHelper.INSTANCE, dcdResource);

		DUtil.initializeDiagramResource(DcdDiagramUtilHelper.INSTANCE, DCDDiagramTypeProvider.DIAGRAM_TYPE_ID, DCDDiagramTypeProvider.PROVIDER_ID, diagramURI,
			dcdResource);

		Resource diagramResource = getEditingDomain().getResourceSet().getResource(diagramURI, true);

		// load diagram from resource
		final Diagram diagram = (Diagram) diagramResource.getContents().get(0);

		// load dcd from resource
		final DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(dcdResource);

		// link diagram with DeviceConfiguration
		TransactionalCommandStack stack = (TransactionalCommandStack) getEditingDomain().getCommandStack();
		stack.execute(new RecordingCommand((TransactionalEditingDomain) getEditingDomain()) {
			@Override
			protected void doExecute() {
				// Evade dirty check - we DON'T want this action to mark the editor as dirty
				GraphitiDcdMultipageEditor.this.setDirtyAllowed(false);

				// set property specifying diagram context (design, local, domain)
				Graphiti.getPeService().setPropertyValue(diagram, DUtil.DIAGRAM_CONTEXT, getDiagramContext(dcdResource));

				// link diagram and dcd
				PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
				link.getBusinessObjects().add(dcd);
				diagram.setLink(link);
			}
		});

		// return editor input from diagram with sad diagram type
		return DiagramEditorInput.createEditorInput(diagram, DCDDiagramTypeProvider.PROVIDER_ID);
	}

	protected DiagramEditor createDiagramEditor() {
		GraphitiDcdDiagramEditor d = new GraphitiDcdDiagramEditor((TransactionalEditingDomain) getEditingDomain());
		return d;
	}

	protected IFormPage createOverviewPage(final Resource dcdResource) {
		final GraphitiDcdOverviewPage retVal = new GraphitiDcdOverviewPage(this);
		retVal.setInput(dcdResource);
		return retVal;
	}
	
	protected IFormPage createDcdDevicesPage(final Resource dcdResource) {
		final GraphitiDcdDevicesPage retVal = new GraphitiDcdDevicesPage(this);
		retVal.setInput(dcdResource);
		return retVal;
	}

	@Override
	public String getEditingDomainId() {
		return GraphitiDcdMultipageEditor.EDITING_DOMAIN_ID;
	}

	@Override
	protected AdapterFactory getSpecificAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		// TODO: How to handle this?
//		final DcdItemProviderAdapterFactoryAdapter dcdAdapter = new DcdItemProviderAdapterFactoryAdapter();
//		dcdAdapter.setComponentPlacementAdapter(new DevicesSectionComponentPlacementItemProvider(dcdAdapter));
//		factory.addAdapterFactory(dcdAdapter);
		factory.addAdapterFactory(new DcdItemProviderAdapterFactory());
		factory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());
		factory.addAdapterFactory(new ScdItemProviderAdapterFactory());
		factory.addAdapterFactory(new PrfItemProviderAdapterFactory());
		return factory;
	}

	@Override
	public List<Object> getOutlineItems() {
		final List<Object> myList = new ArrayList<Object>();
		myList.add(getOverviewPage());
		myList.add(getDeviceConfiguration().getPartitioning());
		return myList;
	}

	@Override
	public boolean isPersisted(final Resource resource) {
		if (resource == null || resource.getURI() == null) {
			return false;
		}
		if (getDeviceConfiguration() == null || getDeviceConfiguration().eResource() == null) {
			return false;
		}
		return resource.getURI().equals(getDeviceConfiguration().eResource().getURI()) && super.isPersisted(resource);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();
		cleanAction.setRoot(getDeviceConfiguration());
		cleanAction.run();

		super.doSave(monitor);
	}
}
