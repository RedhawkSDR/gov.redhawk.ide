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

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * 
 */
public abstract class ViewerProperty< T extends AbstractProperty > extends ViewerItemProvider {

	protected final T def;

	/**
	 * 
	 */
	public ViewerProperty(T def, Object parent) {
		this.def = def;
		this.parent = parent;
	}

	public Object getParent() {
		return parent;
	}

	protected AbstractPropertyRef< ? > getValueRef() {
		if (parent instanceof ViewerComponent) {
			return ((ViewerComponent) parent).getChildRef(this.getID());
		} else if (parent instanceof ViewerStructProperty) {
			return ((ViewerStructProperty) parent).getChildRef(this.getID());
		}
		return null;
	}

	protected ExternalProperty getExternalProperty() {
		if (parent instanceof ViewerComponent) {
			return ((ViewerComponent) parent).getExternalProperty(getID());
		}
		return null;
	}

	public T getDefinition() {
		return this.def;
	}

	public ViewerComponent getComponent() {
		if (parent instanceof ViewerComponent) {
			return (ViewerComponent) parent;
		} else {
			return ((ViewerProperty< ? >) parent).getComponent();
		}
	}

	public SadComponentInstantiation getComponentInstantiation() {
		return getComponent().getComponentInstantiation();
	}

	public String getExternalID() {
		final ExternalProperty property = getExternalProperty();
		if (property != null) {
			return property.getExternalPropID();
		}
		return null;
	}

	public String getID() {
		return def.getId();
	}

	public boolean isAssemblyControllerProperty() {
		return getComponent().isAssemblyController();
	}

	public String resolveExternalID() {
		String externalIdentifier = getExternalID();
		if (externalIdentifier != null) {
			return externalIdentifier;
		} else {
			return this.getID();
		}
	}

	protected void setFeatureValue(EStructuralFeature feature, Object value) {
		EditingDomain editingDomain = getEditingDomain();
		Class< ? > commandClass;
		if (value == null) {
			commandClass = RemoveCommand.class;
		} else {
			if (this.getModelObject(feature) == null) {
				commandClass = AddCommand.class;
			} else {
				commandClass = SetCommand.class;
			}
		}
		Command command = createCommand(editingDomain, commandClass, feature, value);
		if (command != null && command.canExecute()) {
			editingDomain.getCommandStack().execute(command);
		}
	}

	public void setSadValue(Object value) {
		setFeatureValue(ViewerPackage.Literals.SAD_PROPERTY__VALUE, value);
	}

	public void setExternalID(String newExternalID) {
		if (newExternalID != null) {
			newExternalID = newExternalID.trim();
			if (newExternalID.isEmpty()) {
				newExternalID = null;
			}
		}
		setFeatureValue(ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID, newExternalID);
	}

	public abstract Object getValue();

	public abstract String getPrfValue();

	public Collection< ? > getKinds() {
		if (getParent() instanceof ViewerProperty< ? >) {
			return ((ViewerProperty< ? >) getParent()).getKinds();
		}
		return getKindTypes();
	}

	protected abstract Collection< ? > getKindTypes();

	@Override
	public EditingDomain getEditingDomain() {
		return ((ViewerItemProvider) getParent()).getEditingDomain();
	}

	protected EStructuralFeature getChildFeature(Object object, Object child) {
		return null;
	}

	protected Object getModelObject(EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			return getValueRef();
		} else if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return getExternalProperty();
		}
		return null;
	}

	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			ExternalProperty property = SadFactory.eINSTANCE.createExternalProperty();
			SadComponentInstantiation compInst = getComponentInstantiation();
			property.setCompRefID(compInst.getId());
			property.setPropID(getID());
			property.setExternalPropID((String) value);
			return property;
		}
		return null;
	}

	protected Command createCommand(EditingDomain domain, Class< ? > commandClass, EStructuralFeature feature, Object value) {
		Object modelObject = getModelObject(feature);
		if (modelObject == null && (commandClass == AddCommand.class || commandClass == SetCommand.class)) {
			return createParentCommand(domain, feature, createModelObject(feature, value));
		}
		if (commandClass == AddCommand.class) {
			return createAddCommand(domain, modelObject, getChildFeature(modelObject, value), value);
		} else if (commandClass == SetCommand.class) {
			return createSetCommand(domain, modelObject, feature, value);
		} else if (commandClass == RemoveCommand.class) {
			return createRemoveCommand(domain, modelObject, feature);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createAddCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		return AddCommand.create(domain, owner, feature, value);
	}

	protected Command createParentCommand(EditingDomain domain, EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			SadComponentInstantiation compInst = getComponentInstantiation();
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
			ExternalProperties properties = sad.getExternalProperties();
			if (properties != null) {
				return AddCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, (ExternalProperty)value);
			} else {
				properties = SadFactory.eINSTANCE.createExternalProperties();
				properties.getProperties().add((ExternalProperty) value);
				return SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, properties);
			}
		}
		return ((NestedPropertyItemProvider)getParent()).createAddChildCommand(domain, value, feature);
	}

	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return SetCommand.create(domain, owner, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createRemoveCommand(EditingDomain domain, Object object, EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			ExternalProperties properties = (ExternalProperties) ((EObject) object).eContainer();
			if (properties.getProperties().size() == 1) {
				return RemoveCommand.create(domain, properties);
			} else {
				return RemoveCommand.create(domain, object);
			}
		} else if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			NestedPropertyItemProvider parentProvider = (NestedPropertyItemProvider) getParent();
			return parentProvider.createRemoveChildCommand(domain, object, feature);
		}
		return UnexecutableCommand.INSTANCE;
	}
}
