/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.core.graphiti.sad.ui.editor.AbstractGraphitiSADEditor;
import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiDiagramEditor;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.overview.SadOverviewPage;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.properties.SadPropertiesPage;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.sad.ui.diagram.GraphitiSADDiagramEditor;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.SADEditorDiagramTypeProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.internal.ui.handlers.CleanUpComponentFilesAction;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

/**
 * A multi-page editor for SAD files. Includes a Graphiti diagram.
 */
public class GraphitiSADEditor extends AbstractGraphitiSADEditor {

	public static final String ID = "gov.redhawk.ide.graphiti.sad.ui.editor.presentation.SadEditorID";

	private ResourceListener nameListener;
	private IFormPage overviewPage;
	private IFormPage propertiesPage;

	private class ResourceListener extends AdapterImpl {
		private SoftwareAssembly sad;
		private final Resource sadResource;

		public ResourceListener(final Resource spdResource) {
			this.sadResource = spdResource;
			if (this.sadResource != null) {
				this.sadResource.eAdapters().add(this);
				this.sad = SoftwareAssembly.Util.getSoftwareAssembly(this.sadResource);
				if (this.sad != null) {
					this.sad.eAdapters().add(this);
					updateTitle();
				}
			}
		}

		public void dispose() {
			if (this.sad != null) {
				this.sad.eAdapters().remove(this);
			}
			if (this.sadResource != null) {
				this.sadResource.eAdapters().remove(this);
			}
		}

		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getNotifier() instanceof Resource) {
				switch (msg.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
					if (this.sad != null) {
						this.sad.eAdapters().remove(this);
						this.sad = null;
					}
					if (this.sadResource.isLoaded()) {
						this.sad = SoftwareAssembly.Util.getSoftwareAssembly(this.sadResource);
						if (this.sad != null) {
							this.sad.eAdapters().add(this);
							updateTitle();
						}
					}
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof SoftwareAssembly) {
				final int featureID = msg.getFeatureID(SoftwareAssembly.class);
				if (featureID == SadPackage.SOFTWARE_ASSEMBLY__NAME) {
					if (msg.getEventType() == Notification.SET) {
						updateTitle();
					}
				}
			}
		}
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
	protected void addPages() {
		try {
			this.nameListener = new ResourceListener(getMainResource());

			overviewPage = createOverviewPage(getMainResource());
			addPage(overviewPage);

			propertiesPage = createPropertiesPage(getMainResource());
			addPage(propertiesPage);
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
				StatusManager.LOG | StatusManager.SHOW);
		}

		super.addPages();
	}

	private IFormPage createOverviewPage(final Resource sadResource) {
		final SadOverviewPage page = new SadOverviewPage(this);
		page.setInput(sadResource);
		return page;
	}

	private IFormPage createPropertiesPage(Resource sadResource) {
		SadPropertiesPage page = new SadPropertiesPage(this, "propertiesPage", "Properties", true);
		page.setInput(sadResource);
		return page;
	}

	////////////////////////////////////////////////////
	// 1. createDiagramEditor() (#2 in super class)
	////////////////////////////////////////////////////

	@Override
	protected AbstractGraphitiDiagramEditor createDiagramEditor() {
		return new GraphitiSADDiagramEditor(getEditingDomain());
	}

	////////////////////////////////////////////////////
	// 3. createDiagramInput() (#2 in super class)
	////////////////////////////////////////////////////

	@Override
	protected String getDiagramTypeProviderID() {
		return SADEditorDiagramTypeProvider.PROVIDER_ID;
	}

	@Override
	protected String getDiagramContext() {
		// If the SCA file system scheme is in use that means the file is from the Target SDR.
		if (ScaFileSystemConstants.SCHEME.equals(getMainResource().getURI().scheme())) {
			return DUtil.DIAGRAM_CONTEXT_TARGET_SDR;
		} else {
			return DUtil.DIAGRAM_CONTEXT_DESIGN;
		}
	}

	////////////////////////////////////////////////////
	// Other
	////////////////////////////////////////////////////

	@Override
	public void updateTitle() {
		String name = null;
		final SoftwareAssembly sad = getSoftwareAssembly();
		if (sad != null) {
			name = sad.getName();
			if (name == null) {
				name = getEditorInput().getName();
			}
		}
		if (name == null) {
			name = "";
		}
		setPartName(name);
	}

	@Override
	public List<Object> getOutlineItems() {
		final List<Object> myList = new ArrayList<Object>();
		if (overviewPage != null) {
			myList.add(overviewPage);
		}
		if (getSoftwareAssembly().getPartitioning() != null) {
			myList.add(getSoftwareAssembly().getPartitioning());
		}
		return myList;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();
		cleanAction.setRoot(getSoftwareAssembly());
		cleanAction.run();

		super.doSave(monitor);
	}
}
