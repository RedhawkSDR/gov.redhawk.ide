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

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSimpleProperty;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
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
			if (selObject instanceof ViewerSimpleProperty) {
				ViewerSimpleProperty prop = (ViewerSimpleProperty) selObject;
				String value = null;
				if (c instanceof Combo) {
					Combo combo = (Combo) c;
					value = combo.getText();
				} else if (c instanceof Text) {
					Text text = (Text) c;
					value = text.getText();
				}
				
				if (value == null || PluginUtil.equals(prop.getValue(), value)) {
					return;
				}
				
				SadComponentInstantiation compInst = prop.getComponentInstantiation();
				Command command;
				ComponentProperties properties = compInst.getComponentProperties();
				if (properties != null) {
					
				} else {
					
				}
			}
		}
	}

}
