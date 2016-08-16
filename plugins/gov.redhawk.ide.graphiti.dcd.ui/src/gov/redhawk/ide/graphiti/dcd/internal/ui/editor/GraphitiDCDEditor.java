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
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.core.graphiti.dcd.ui.editor.AbstractGraphitiDCDEditor;
import gov.redhawk.ide.dcd.internal.ui.editor.DevicesPage;
import gov.redhawk.ide.dcd.internal.ui.editor.NodeOverviewPage;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

public class GraphitiDCDEditor extends AbstractGraphitiDCDEditor {

	private ResourceListener nameListener;
	private IFormPage overviewPage;
	private IFormPage devicesPage;

	private class ResourceListener extends AdapterImpl {
		private DeviceConfiguration dcd;
		private final Resource dcdResource;

		public ResourceListener(final Resource dcdResource) {
			this.dcdResource = dcdResource;
			if (this.dcdResource != null) {
				this.dcdResource.eAdapters().add(this);
				this.dcd = DeviceConfiguration.Util.getDeviceConfiguration(this.dcdResource);
				if (this.dcd != null) {
					this.dcd.eAdapters().add(this);
					updateTitle();
				}
			}
		}

		public void dispose() {
			if (this.dcd != null) {
				this.dcd.eAdapters().remove(this);
			}
			if (this.dcdResource != null) {
				this.dcdResource.eAdapters().remove(this);
			}
		}

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
						this.dcd = DeviceConfiguration.Util.getDeviceConfiguration(this.dcdResource);
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

	public GraphitiDCDEditor() {
		super();
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

			devicesPage = createDcdDevicesPage(getMainResource());
			addPage(devicesPage);
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
				StatusManager.LOG | StatusManager.SHOW);
		}

		super.addPages();
	}

	private IFormPage createOverviewPage(final Resource dcdResource) {
		final NodeOverviewPage retVal = new NodeOverviewPage(this);
		retVal.setInput(dcdResource);
		return retVal;
	}

	private IFormPage createDcdDevicesPage(final Resource dcdResource) {
		final DevicesPage retVal = new DevicesPage(this);
		retVal.setInput(dcdResource);
		return retVal;
	}

	@Override
	protected String getDiagramContext(Resource sadResource) {
		// If the SCA file system scheme is in use that means the file is from the Target SDR.
		if (ScaFileSystemConstants.SCHEME.equals(sadResource.getURI().scheme())) {
			return DUtil.DIAGRAM_CONTEXT_TARGET_SDR;
		} else {
			return DUtil.DIAGRAM_CONTEXT_DESIGN;
		}
	}

	@Override
	public void updateTitle() {
		String name = null;
		final DeviceConfiguration dcd = getDeviceConfiguration();
		if (dcd != null) {
			name = dcd.getName();
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
		final List<Object> items = new ArrayList<Object>();
		if (overviewPage != null) {
			items.add(overviewPage);
		}
		if (getDeviceConfiguration().getPartitioning() != null) {
			items.add(getDeviceConfiguration().getPartitioning());
		}
		return items;
	}
}
