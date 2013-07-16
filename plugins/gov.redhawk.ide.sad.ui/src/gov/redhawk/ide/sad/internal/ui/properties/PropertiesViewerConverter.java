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
package gov.redhawk.ide.sad.internal.ui.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSequenceProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSimpleProperty;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.Values;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class PropertiesViewerConverter implements XViewerConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#setInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
	public void setInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			if (selObject instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) selObject;
				if (prop.getParent() instanceof ViewerComponent) {
					Combo combo = (Combo) c;
					ExternalProperty externalProp = prop.getExternalProperty();
					if (externalProp != null) {
						if (externalProp.getExternalPropID() != null) {
							combo.setText(externalProp.getExternalPropID());
						} else {
							combo.setText(externalProp.getPropID());
						}
					}
				}
			}
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (selObject instanceof ViewerSimpleProperty) {
				ViewerSimpleProperty prop = (ViewerSimpleProperty) selObject;
				String value = prop.getValue();
				if (value == null) {
					value = "";
				}
				if (c instanceof Combo) {
					Combo combo = (Combo) c;
					combo.setText(value);
				} else if (c instanceof Text) {
					Text text = (Text) c;
					text.setText(value);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#getInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
	public void getInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			if (selObject instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) selObject;
				if (prop.getParent() instanceof ViewerComponent) {
					Combo combo = (Combo) c;
					SadComponentInstantiation compInst = prop.getComponentInstantiation();
					SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
					if (sad != null) {
						Command command = null;
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sad);
						ExternalProperties properties = sad.getExternalProperties();
						if (properties == null) {
							properties = SadFactory.eINSTANCE.createExternalProperties();
							ExternalProperty newProp = SadFactory.eINSTANCE.createExternalProperty();
							newProp.setCompRefID(compInst.getId());
							newProp.setPropID(prop.getDefinition().getId());
							if (!newProp.getPropID().equals(combo.getText())) {
								newProp.setExternalPropID(combo.getText());
							}
							properties.getProperties().add(newProp);
							command = SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, properties);
						} else {
							ExternalProperty externalProp = prop.getExternalProperty();
							if (externalProp == null) {
								if (combo.getText().trim().isEmpty()) {
									// Do nothing
									return;
								}
								ExternalProperty newProp = SadFactory.eINSTANCE.createExternalProperty();
								newProp.setCompRefID(compInst.getId());
								newProp.setPropID(prop.getDefinition().getId());
								if (!newProp.getPropID().equals(combo.getText())) {
									newProp.setExternalPropID(combo.getText());
								}
								command = AddCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, newProp);
							} else {
								if (combo.getText().trim().isEmpty()) {
									command = RemoveCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, externalProp);
								} else {
									String externalId = externalProp.getExternalPropID();
									if (combo.getText().equals(externalId)) {
										// Do nothing same id
										return;
									} else if (combo.getText().equals(prop.getDefinition().getId())) {
										command = SetCommand.create(domain, externalProp, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, null);
									} else {
										command = SetCommand.create(domain, externalProp, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID,
											combo.getText());
									}
								}
							}
						}
						if (command != null) {
							domain.getCommandStack().execute(command);
						}
					}
				}
			}
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (selObject instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) selObject;
				String value = null;
				if (c instanceof Combo) {
					Combo combo = (Combo) c;
					value = combo.getText();
				} else if (c instanceof Text) {
					Text text = (Text) c;
					value = text.getText();
				}

				if (value == null) {
					return;
				}

				SadComponentInstantiation compInst = prop.getComponentInstantiation();
				Command command = null;
				ComponentProperties properties = compInst.getComponentProperties();
				if (properties != null) {
					AbstractPropertyRef< ? > ref = getRef(prop, properties);
					if (ref != null) {
						if (value.isEmpty()) {
							if (properties.getProperties().size() > 1) {
								command = createRemoveRefCommand(ref);
							} else {
								command = createRemovePropertiesCommand(properties, compInst);
							}
						} else {
							command = createSetValueCommand(ref, prop, value);
						}
					} else {
						command = addRefCommand(ref, prop, value);
					}
				} else {
					command = createPropertiesCommand(compInst, prop, value);
				}

				if (command != null) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(compInst);
					domain.getCommandStack().execute(command);
				}
			}
		}
	}

	private Command createPropertiesCommand(SadComponentInstantiation compInst, ViewerProperty< ? > prop, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	private Command createRemovePropertiesCommand(ComponentProperties properties, SadComponentInstantiation compInst) {
		// TODO Auto-generated method stub
		return null;
	}

	private Command createRemoveRefCommand(AbstractPropertyRef< ? > ref) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(ref);
		if (ref instanceof SimpleRef) {
			if (ref.eContainer() instanceof ComponentProperties) {
				return RemoveCommand.create(domain, ref.eContainer(), PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF, ref);
			} else if (ref.eContainer() instanceof StructRef) {
				if (((StructRef) ref.eContainer()).getSimpleRef().size() == 1) {
					return createRemoveRefCommand(((StructRef) ref.eContainer()));
				}
				return RemoveCommand.create(domain, ref.eContainer(), PrfPackage.Literals.STRUCT_REF__SIMPLE_REF, ref);
			}
		} else if (ref instanceof SimpleSequenceRef) {
			return RemoveCommand.create(domain, ref.eContainer(), PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF, ref);
		} else if (ref instanceof StructRef) {
			if (ref.eContainer() instanceof ComponentProperties) {
				return RemoveCommand.create(domain, ref.eContainer(), PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF, ref);
			}
			// TODO Handle Struct Sequence
		} else if (ref instanceof StructSequenceRef) {
			return RemoveCommand.create(domain, ref.eContainer(), PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF, ref);
		}
		// TODO Auto-generated method stub
		return null;
	}

	private Command addRefCommand(AbstractPropertyRef< ? > ref, ViewerProperty< ? > prop, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	private Command createSetValueCommand(AbstractPropertyRef< ? > ref, ViewerProperty< ? > prop, String value) {
		if (prop instanceof ViewerSimpleProperty && ref instanceof SimpleRef) {
			SimpleRef simpleRef = (SimpleRef) ref;
			if (PluginUtil.equals(((ViewerSimpleProperty) prop).getValue(), value)) {
				return null;
			}
			ViewerSimpleProperty simpleProp = (ViewerSimpleProperty) prop;
			if (simpleProp.getDefinition().getType().isValueOfType(value, simpleProp.getDefinition().isComplex())) {
				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(ref);
				return SetCommand.create(domain, simpleRef, PrfPackage.Literals.SIMPLE_REF__VALUE, value);
			}
		} else if (prop instanceof ViewerSequenceProperty && ref instanceof SimpleSequenceRef) {
			SimpleSequenceRef seqRef = (SimpleSequenceRef) ref;
			ViewerSequenceProperty seqProp = (ViewerSequenceProperty) prop;
			String[] input = value.split(",");
			List<String> newValues = new ArrayList<String>(input.length);
			for (int i = 0; i < input.length; i++) {
				String newValue = input[i].trim();
				if (!seqProp.getDefinition().getType().isValueOfType(newValue, seqProp.getDefinition().isComplex())) {
					// One of the values is bad, abort
					return null;
				}
				newValues.add(newValue);
			}
			if (Arrays.equals(newValues.toArray(), seqRef.getValues().getValue().toArray())) {
				return null;
			}
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(ref);
			Values seqValues = PrfFactory.eINSTANCE.createValues();
			seqValues.getValue().addAll(newValues);
			return SetCommand.create(domain, seqRef, PrfPackage.Literals.SIMPLE_SEQUENCE__VALUES, seqValues);
		}
		return null;
	}

	private AbstractPropertyRef< ? > getRef(ViewerProperty< ? > prop, ComponentProperties properties) {
		if (prop.getParent() instanceof ViewerComponent) {
			for (ValueListIterator<Object> i = properties.getProperties().valueListIterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof AbstractPropertyRef< ? > && PluginUtil.equals(((AbstractPropertyRef< ? >) obj).getRefID(), prop.getID())) {
					return (AbstractPropertyRef< ? >) obj;
				}
			}
		} else if (prop.getParent() instanceof ViewerProperty< ? >) {
			AbstractPropertyRef< ? > parentRef = getRef((ViewerProperty< ? >) prop.getParent(), properties);
			if (prop instanceof ViewerSimpleProperty && parentRef instanceof StructRef) {
				StructRef structRef = (StructRef) parentRef;
				ViewerSimpleProperty simpleProp = (ViewerSimpleProperty) prop;
				for (SimpleRef ref : structRef.getSimpleRef()) {
					if (PluginUtil.equals(ref.getRefID(), simpleProp.getID())) {
						return ref;
					}
				}
			}
			// TODO Handle Struct Sequence Editing
		}
		// TODO Auto-generated method stub
		return null;
	}

}
