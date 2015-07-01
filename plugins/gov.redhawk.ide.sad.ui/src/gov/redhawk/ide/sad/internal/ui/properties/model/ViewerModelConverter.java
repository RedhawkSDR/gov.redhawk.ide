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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewer;
import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SadPartitioning;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.WorkbenchJob;

public class ViewerModelConverter {

	private SoftwareAssembly sad;
	private List<ViewerComponent> viewerModel = new ArrayList<ViewerComponent>();

	private EContentAdapter sadListener = new EContentAdapter() {
		@Override
		public void notifyChanged(org.eclipse.emf.common.notify.Notification notification) {
			super.notifyChanged(notification);
			if (handlingchange) {
				return;
			}
			handlingchange = true;
			try {
				if (notification.isTouch()) {
					return;
				}
				if (notification.getNotifier() instanceof SadComponentInstantiation) {
					switch (notification.getFeatureID(SadComponentInstantiation.class)) {
					case SadPackage.SAD_COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES:
						handleComponentInstComponentPropertiesChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof ComponentProperties) {
					switch (notification.getFeatureID(ComponentProperties.class)) {
					case PartitioningPackage.COMPONENT_PROPERTIES__SIMPLE_REF:
						handlePropertiesSimpleRefChanged(notification);
						break;
					case PartitioningPackage.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF:
						handlePropertiesSimpleSequenceRefChanged(notification);
						break;
					case PartitioningPackage.COMPONENT_PROPERTIES__STRUCT_REF:
						handlePropertiesStructRefChanged(notification);
						break;
					case PartitioningPackage.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF:
						handlePropertiesStructSequenceRefChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof SimpleRef) {
					switch (notification.getFeatureID(SimpleRef.class)) {
					case PrfPackage.SIMPLE_REF__VALUE:
						handleSimpleRefValueChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof SimpleSequenceRef) {
					switch (notification.getFeatureID(SimpleSequenceRef.class)) {
					case PrfPackage.SIMPLE_SEQUENCE_REF__VALUES:
						handleSimpleSequenceRefValuesChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof StructRef) {
					switch (notification.getFeatureID(StructRef.class)) {
					case PrfPackage.STRUCT_REF__SIMPLE_REF:
						handleStructRefSimpleRefChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof StructSequenceRef) {
					switch (notification.getFeatureID(StructSequenceRef.class)) {
					case PrfPackage.STRUCT_SEQUENCE__STRUCT_VALUE:
						handleStructSeqStructValueChanged(notification);
						break;
					default:
						break;
					}

				} else if (notification.getNotifier() instanceof SoftwareAssembly) {
					switch (notification.getFeatureID(SoftwareAssembly.class)) {
					case SadPackage.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES:
						handleSoftwareAssemblyExternalPropertiesChanged(notification);
						break;
					case SadPackage.SOFTWARE_ASSEMBLY__PARTITIONING:
						handleSoftwareAssemblyPartitioningChanged(notification);
						break;
					case SadPackage.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER:
						handleAssemblyControlerUpdate(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof AssemblyController) {
					switch (notification.getFeatureID(AssemblyController.class)) {
					case SadPackage.ASSEMBLY_CONTROLLER__COMPONENT_INSTANTIATION_REF:
						handleAssemblyControlerUpdate(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof SadComponentInstantiationRef) {
					switch (notification.getFeatureID(SadComponentInstantiationRef.class)) {
					case SadPackage.SAD_COMPONENT_INSTANTIATION_REF__REFID:
						handleAssemblyControlerUpdate(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof SadPartitioning) {
					switch (notification.getFeatureID(SadPartitioning.class)) {
					case SadPackage.SAD_PARTITIONING__HOST_COLLOCATION:
						handlePartitioningHostCollocationChanged(notification);
						break;
					case SadPackage.SAD_PARTITIONING__COMPONENT_PLACEMENT:
						handlePartitioningComponentPlacementChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof HostCollocation) {
					switch (notification.getFeatureID(HostCollocation.class)) {
					case SadPackage.HOST_COLLOCATION__COMPONENT_PLACEMENT:
						handleHostCollocationComponentPlacementChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof SadComponentPlacement) {
					switch (notification.getFeatureID(SadComponentPlacement.class)) {
					case SadPackage.SAD_COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION:
						handleComponentPlacementComponentInstantiationChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof ExternalProperties) {
					switch (notification.getFeatureID(ExternalProperties.class)) {
					case SadPackage.EXTERNAL_PROPERTIES__PROPERTIES:
						handleExternalPropertiesPropertiesChanged(notification);
						break;
					default:
						break;
					}
				} else if (notification.getNotifier() instanceof ExternalProperty) {
					switch (notification.getFeatureID(ExternalProperty.class)) {
					case SadPackage.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID:
						handleExternalPropIDChanged(notification);
						break;
					default:
						break;
					}

				}
			} finally {
				handlingchange = false;
			}
		}
	};
	private IViewerPropertyChangeListener propertyListener = new IViewerPropertyChangeListener() {

		@Override
		public void valueChanged(ViewerProperty< ? > source) {
			viewer.refresh(source);
		}

		@Override
		public void externalIDChanged(ViewerProperty< ? > source) {
			viewer.refresh(source);
		}
	};
	private boolean handlingchange = false;
	private PropertiesViewer viewer;
	private WorkbenchJob refreshJob = new WorkbenchJob("Refresh Viewer") {
		{
			setUser(false);
			setSystem(true);
		}

		@Override
		public boolean shouldSchedule() {
			return super.shouldSchedule() && viewer != null;
		}

		@Override
		public boolean shouldRun() {
			return super.shouldRun() && viewer != null;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (viewer != null) {
				viewer.refresh();
			}
			return Status.OK_STATUS;
		}
	};

	public void setViewer(PropertiesViewer viewer) {
		this.viewer = viewer;
	}

	private void handleAssemblyControlerUpdate(Notification notification) {
		refresh();
	}

	private void handleComponentPlacementComponentInstantiationChanged(Notification notification) {
		setupModel();
	}

	private void handleHostCollocationComponentPlacementChanged(Notification notification) {
		setupModel();
	}

	private void handlePartitioningComponentPlacementChanged(Notification notification) {
		setupModel();
	}

	private void handlePartitioningHostCollocationChanged(Notification notification) {
		setupModel();
	}

	private void handleSoftwareAssemblyPartitioningChanged(Notification notification) {
		setupModel();
	}

	public PropertiesViewer getViewer() {
		return viewer;
	}

	private void handleExternalPropIDChanged(Notification notification) {
	}

	private void handleExternalPropertiesPropertiesChanged(Notification notification) {
		ExternalProperties externalProperties = (ExternalProperties) notification.getNotifier();
		updateExternalProperties(externalProperties);

	}

	protected void updateExternalProperties(ExternalProperties externalProperties) {
	}

	private void handleSoftwareAssemblyExternalPropertiesChanged(Notification notification) {
		updateExternalProperties(sad.getExternalProperties());
	}

	private void handleStructSeqStructValueChanged(Notification notification) {
		updateProperties();
	}

	private void handleStructRefSimpleRefChanged(Notification notification) {
		updateProperties();
	}

	private void handleSimpleSequenceRefValuesChanged(Notification notification) {
		updateProperties();
	}

	private void handleSimpleRefValueChanged(Notification notification) {
		updateProperties();
	}

	private void handlePropertiesStructSequenceRefChanged(Notification notification) {
		updateProperties();
	}

	private void handlePropertiesStructRefChanged(Notification notification) {
		updateProperties();
	}

	private void handlePropertiesSimpleSequenceRefChanged(Notification notification) {
		updateProperties();
	}

	private void handlePropertiesSimpleRefChanged(Notification notification) {
		updateProperties();
	}

	private void handleComponentInstComponentPropertiesChanged(Notification notification) {
		updateProperties();
	}

	private void updateProperties() {
		// TODO: Only refresh updated properties
		refresh();
	}

	public static AbstractPropertyRef< ? > getRef(SadComponentInstantiation inst, ViewerProperty< ? > p) {
		ComponentProperties properties = inst.getComponentProperties();
		if (properties != null) {
			for (ValueListIterator<Object> i = properties.getProperties().valueListIterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof AbstractPropertyRef< ? >) {
					AbstractPropertyRef< ? > propRef = (AbstractPropertyRef< ? >) obj;
					if (PluginUtil.equals(propRef.getRefID(), p.getID())) {
						return propRef;
					}
				}
			}
		}
		return null;
	}

	public void setSoftwareAssembly(SoftwareAssembly sad) {
		if (this.sad == sad || sad == null) {
			return;
		}
		if (this.sad != null && this.sad.eAdapters() != null) {
			//			sadListener.unsetTarget(this.sad);
			sad.eAdapters().remove(sadListener);
		}
		this.sad = sad;
		setupModel();
		if (this.sad != null) {
			sad.eAdapters().add(sadListener);
			//			sadListener.setTarget(sad);
		}
	}

	private void setupModel() {
		viewerModel.clear();
		for (SadComponentInstantiation inst : sad.getAllComponentInstantiations()) {
			ViewerComponent comp = new ViewerComponent(inst);
			comp.addPropertyChangeListener(propertyListener);
			viewerModel.add(comp);
		}
		refresh();
	}

	private void refresh() {
		if (Display.getCurrent() != null) {
			if (viewer != null) {
				viewer.refresh();
			}
		} else if (viewer != null) {
			refreshJob.schedule();
		}
	}

	private void refresh(Object obj) {
		if (viewer != null) {
			viewer.refresh(obj);
		} else if (viewer != null) {
			refreshJob.schedule();
		}
	}

	public List<ViewerComponent> getViewerModel() {
		return viewerModel;
	}
}
