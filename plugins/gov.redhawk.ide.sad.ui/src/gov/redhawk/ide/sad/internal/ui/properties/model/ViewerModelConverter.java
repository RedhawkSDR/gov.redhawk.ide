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
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SadPartitioning;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.WorkbenchJob;

public class ViewerModelConverter {

	private SoftwareAssembly sad;
	private List<ViewerComponent> viewerModel = new ArrayList<ViewerComponent>();

	private EContentAdapter sadListener = new EContentAdapter() {
		@Override
		public void notifyChanged(org.eclipse.emf.common.notify.Notification notification) {
			super.notifyChanged(notification);
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
		}
	};
	private IViewerPropertyChangeListener propertyListener = new IViewerPropertyChangeListener() {

		@Override
		public void valueChanged(ViewerProperty< ? > source) {
			handleViewerPropValueChanged(source);
		}

		@Override
		public void externalIDChanged(ViewerProperty< ? > source) {
			handleViewerPropExternalIDChanged(source);
		}
	};
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

	private void handleViewerPropValueChanged(ViewerProperty< ? > source) {
		Command command = null;
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sad);
		if (source instanceof ViewerSimpleProperty) {
			ViewerSimpleProperty simpleProp = (ViewerSimpleProperty) source;
			String newValue = simpleProp.getValue();
			Object parent = source.getParent();
			if (parent instanceof ViewerComponent) {
				ViewerComponent comp = (ViewerComponent) parent;
				SadComponentInstantiation inst = comp.getComponentInstantiation();
				SimpleRef ref = (SimpleRef) getRef(inst, simpleProp);
				ComponentProperties properties = inst.getComponentProperties();
				if (ref == null) {
					ref = createRef(simpleProp.getDefinition(), newValue);
					if (properties == null) {
						properties = PartitioningFactory.eINSTANCE.createComponentProperties();
						properties.getSimpleRef().add(ref);
						command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
					} else {
						command = AddCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF, ref);
					}
				} else {
					if (newValue != null) {
						command = SetCommand.create(domain, ref, PrfPackage.Literals.SIMPLE_REF__VALUE, newValue);
					} else {
						if (properties.getProperties().size() == 1) {
							command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, null);
						} else {
							command = RemoveCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF, ref);
						}
					}
				}
			} else if (parent instanceof ViewerStructProperty) {
				ViewerStructProperty structProp = (ViewerStructProperty) simpleProp.getParent();
				if (structProp.getParent() instanceof ViewerComponent) {
					ViewerComponent comp = (ViewerComponent) structProp.getParent();
					SadComponentInstantiation inst = comp.getComponentInstantiation();
					StructRef structRef = (StructRef) getRef(inst, structProp);
					ComponentProperties properties = inst.getComponentProperties();
					if (structRef != null) {
						SimpleRef ref = null;
						for (SimpleRef r : structRef.getSimpleRef()) {
							if (PluginUtil.equals(r.getRefID(), simpleProp.getID())) {
								ref = r;
								break;
							}
						}
						if (ref == null) {
							ref = createRef(simpleProp.getDefinition(), newValue);
							command = AddCommand.create(domain, structRef, PrfPackage.Literals.STRUCT_REF__SIMPLE_REF, ref);
						} else {
							if (newValue == null) {
								if (structRef.getSimpleRef().size() == 1) {
									if (properties.getProperties().size() == 1) {
										command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES,
											null);
									} else {
										command = RemoveCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF,
											structRef);
									}
								} else {
									command = RemoveCommand.create(domain, structRef, PrfPackage.Literals.STRUCT_REF__SIMPLE_REF, ref);
								}
							} else {
								command = SetCommand.create(domain, ref, PrfPackage.Literals.SIMPLE_REF__VALUE, newValue);
							}
						}
					} else {
						structRef = createRef(structProp.getDefinition());
						structRef.getSimpleRef().add(createRef(simpleProp.getDefinition(), newValue));
						if (properties == null) {
							properties = PartitioningFactory.eINSTANCE.createComponentProperties();
							properties.getStructRef().add(structRef);
							command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
						} else {
							command = AddCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF, structRef);
						}
					}
				} else {
					throw new UnsupportedOperationException();
				}
			} else {
				throw new UnsupportedOperationException();
			}
		} else if (source instanceof ViewerSequenceProperty) {
			ViewerSequenceProperty seqProp = (ViewerSequenceProperty) source;
			Object parent = source.getParent();
			if (parent instanceof ViewerComponent) {
				ViewerComponent comp = (ViewerComponent) parent;
				List<String> newValues = seqProp.getValues();
				SadComponentInstantiation inst = comp.getComponentInstantiation();
				ComponentProperties properties = inst.getComponentProperties();
				SimpleSequenceRef ref = (SimpleSequenceRef) getRef(inst, seqProp);
				if (ref == null) {
					ref = createRef(seqProp, newValues);
					if (properties == null) {
						properties = PartitioningFactory.eINSTANCE.createComponentProperties();
						properties.getSimpleSequenceRef().add(ref);
						command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
					} else {
						command = AddCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF, ref);
					}
				} else {
					if (newValues != null && !newValues.isEmpty()) {
						Values values = PrfFactory.eINSTANCE.createValues();
						values.getValue().addAll(newValues);
						command = SetCommand.create(domain, ref, PrfPackage.Literals.SIMPLE_SEQUENCE__VALUES, values);
					} else {
						if (properties.getProperties().size() == 1) {
							command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, null);
						} else {
							command = RemoveCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF, ref);
						}
					}
				}
			} else {
				throw new UnsupportedOperationException();
			}
		} else if (source instanceof ViewerStructSequenceProperty) {
			ViewerStructSequenceProperty structSeq = (ViewerStructSequenceProperty) source;
			Object parent = source.getParent();
			if (parent instanceof ViewerComponent) {
				ViewerComponent comp = (ViewerComponent) parent;
				SadComponentInstantiation inst = comp.getComponentInstantiation();
				ComponentProperties properties = inst.getComponentProperties();
				StructSequenceRef ref = (StructSequenceRef) getRef(inst, structSeq);
				if (ref == null) {
					ref = createRef(structSeq);
					if (properties == null) {
						properties = PartitioningFactory.eINSTANCE.createComponentProperties();
						properties.getStructSequenceRef().add(ref);
						command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
					} else {
						command = AddCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF, ref);
					}
				} else {
					if (!structSeq.getSimples().isEmpty() && structSeq.getSimples().get(0).getValues() != null) {
						ref = createRef(structSeq);
						CompoundCommand replace = new CompoundCommand();
						replace.append(RemoveCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF, ref));
						replace.append(AddCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF, ref));
						command = replace;
					} else {
						if (properties.getProperties().size() == 1) {
							command = SetCommand.create(domain, inst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, null);
						} else {
							command = RemoveCommand.create(domain, properties, PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF, ref);
						}
					}
				}
			} else {
				throw new UnsupportedOperationException();
			}
		} else {
			throw new UnsupportedOperationException();
		}

		if (command != null) {
			domain.getCommandStack().execute(command);
		}
	}

	private StructSequenceRef createRef(ViewerStructSequenceProperty structSeq) {
		if (structSeq == null) {
			return null;
		}
		StructSequenceRef retVal = PrfFactory.eINSTANCE.createStructSequenceRef();
		retVal.setRefID(structSeq.getID());
		int numStructs = structSeq.getSimples().get(0).getValues().size();
		for (int i = 0; i < numStructs; i++) {
			StructValue value = PrfFactory.eINSTANCE.createStructValue();
			for (ViewerStructSequenceSimpleProperty simple : structSeq.getSimples()) {
				if (simple.getValues() != null) {
					SimpleRef simpleRef = PrfFactory.eINSTANCE.createSimpleRef();
					simpleRef.setRefID(simple.getID());
					simpleRef.setValue(simple.getValues().get(i));
					value.getSimpleRef().add(simpleRef);
				}
			}
			retVal.getStructValue().add(value);
		}
		return retVal;
	}

	private SimpleSequenceRef createRef(ViewerSequenceProperty seqProp, List<String> newValues) {
		SimpleSequenceRef retVal = PrfFactory.eINSTANCE.createSimpleSequenceRef();
		retVal.setRefID(seqProp.getID());
		retVal.setValues(PrfFactory.eINSTANCE.createValues());
		retVal.getValues().getValue().addAll(newValues);
		return retVal;
	}

	private StructRef createRef(Struct definition) {
		StructRef retVal = PrfFactory.eINSTANCE.createStructRef();
		retVal.setRefID(definition.getId());
		return retVal;
	}

	private SimpleRef createRef(Simple def, String newValue) {
		SimpleRef retVal = PrfFactory.eINSTANCE.createSimpleRef();
		retVal.setRefID(def.getId());
		retVal.setValue(newValue);
		return retVal;
	}

	private void handleExternalPropIDChanged(Notification notification) {
		ExternalProperty externalProp = (ExternalProperty) notification.getNotifier();
		for (ViewerComponent comp : this.viewerModel) {
			if (externalProp.getCompRefID().equals(comp.getComponentInstantiation().getId())) {
				for (ViewerProperty< ? > prop : comp.getProperties()) {
					if (externalProp.getPropID().equals(prop.getID())) {
						prop.setExternalID(externalProp.getExternalPropID());
						refresh(prop);
						return;
					}
				}
			}
		}
	}

	private boolean matches(ExternalProperty externalProp, ViewerProperty< ? > prop) {
		if (prop.getParent() instanceof ViewerComponent) {
			ViewerComponent comp = (ViewerComponent) prop.getParent();
			return comp.getComponentInstantiation().getId().equals(externalProp.getCompRefID()) && prop.getID().equals(externalProp.getPropID());
		}
		return false;
	}

	private void handleExternalPropertiesPropertiesChanged(Notification notification) {
		ExternalProperties externalProperties = (ExternalProperties) notification.getNotifier();
		updateExternalProperties(externalProperties);

	}

	protected void updateExternalProperties(ExternalProperties externalProperties) {
		for (ViewerComponent comp : this.viewerModel) {
			for (ViewerProperty< ? > prop : comp.getProperties()) {
				ExternalProperty externalProp = null;
				if (externalProperties != null) {
					for (ExternalProperty p : externalProperties.getProperties()) {
						if (matches(p, prop)) {
							externalProp = p;
							break;
						}
					}
				}
				if (externalProp != null) {
					if (externalProp.getExternalPropID() != null) {
						prop.setExternalID(externalProp.getExternalPropID());
					} else {
						prop.setExternalID(externalProp.getPropID());
					}
				} else {
					prop.setExternalID(null);
				}
			}
		}
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
		for (ViewerComponent c : this.viewerModel) {
			for (ViewerProperty< ? > p : c.getProperties()) {
				updateProperty(c.getComponentInstantiation(), p);
			}
		}
		refresh();
	}

	private void updateProperty(SadComponentInstantiation inst, ViewerProperty< ? > p) {
		AbstractPropertyRef< ? > propRef = getRef(inst, p);
		if (p instanceof ViewerSimpleProperty && propRef instanceof SimpleRef) {
			((ViewerSimpleProperty) p).setValue((SimpleRef) propRef);
		} else if (p instanceof ViewerSequenceProperty && propRef instanceof SimpleSequenceRef) {
			((ViewerSequenceProperty) p).setValues((SimpleSequenceRef) propRef);
		} else if (p instanceof ViewerStructProperty && propRef instanceof StructRef) {
			((ViewerStructProperty) p).setValue((StructRef) propRef);
		} else if (p instanceof ViewerStructSequenceProperty && propRef instanceof StructSequenceRef) {
			((ViewerStructSequenceProperty) p).setValue((StructSequenceRef) propRef);
		}
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
		if (this.sad == sad) {
			return;
		}
		if (this.sad != null) {
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

	protected void handleViewerPropExternalIDChanged(ViewerProperty< ? > source) {
		ViewerProperty< ? > prop = source;
		String newValue = source.getExternalID();
		if (prop.getParent() instanceof ViewerComponent) {
			SadComponentInstantiation compInst = prop.getComponentInstantiation();
			Command command = null;
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sad);
			ExternalProperties properties = sad.getExternalProperties();
			if (properties == null) {
				properties = SadFactory.eINSTANCE.createExternalProperties();
				ExternalProperty newProp = SadFactory.eINSTANCE.createExternalProperty();
				newProp.setCompRefID(compInst.getId());
				newProp.setPropID(prop.getID());
				if (!newProp.getPropID().equals(newValue)) {
					newProp.setExternalPropID(newValue);
				}
				properties.getProperties().add(newProp);
				command = SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, properties);
			} else {
				ExternalProperty externalProp = getExternalProperty(prop);
				if (externalProp == null && newValue != null) {
					ExternalProperty newProp = SadFactory.eINSTANCE.createExternalProperty();
					newProp.setCompRefID(compInst.getId());
					newProp.setPropID(prop.getID());
					if (!newProp.getPropID().equals(newValue)) {
						newProp.setExternalPropID(newValue);
					}
					command = AddCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, newProp);
				} else {
					if (newValue == null) {
						if (properties.getProperties().size() == 1) {
							command = SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, null);
						} else {
							command = RemoveCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, externalProp);
						}
					} else {
						if (newValue.equals(prop.getID())) {
							command = SetCommand.create(domain, externalProp, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, null);
						} else {
							command = SetCommand.create(domain, externalProp, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, newValue);
						}
					}
				}
			}
			if (command != null) {
				domain.getCommandStack().execute(command);
			}
		}
	}

	private ExternalProperty getExternalProperty(ViewerProperty< ? > prop) {
		ExternalProperties externalProperties = sad.getExternalProperties();
		if (externalProperties != null) {
			for (ExternalProperty p : externalProperties.getProperties()) {
				if (p.getCompRefID().equals(prop.getComponentInstantiation().getId()) && p.getPropID().equals(prop.getID())) {
					return p;
				}
			}
		}
		return null;
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
