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

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import java.util.Collection;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * 
 */
public abstract class ViewerProperty< T extends AbstractProperty > extends ViewerItemProvider {

	protected final T def;
	private ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	/**
	 * 
	 */
	public ViewerProperty(T def, Object parent) {
		this.def = def;
		this.parent = parent;
	}

	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	protected void firePropertyChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).valueChanged(this);
		}
	}

	protected void fireExternalIDChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).externalIDChanged(this);
		}
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

	public SadComponentInstantiation getComponentInstantiation() {
		Object element = getParent();
		while (element != null) {
			if (element instanceof ViewerComponent) {
				return ((ViewerComponent) element).getComponentInstantiation();
			} else if (element instanceof ViewerProperty< ? >) {
				element = ((ViewerProperty< ? >) element).getParent();
			}
		}
		return null;
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
		SadComponentInstantiation compInst = getComponentInstantiation();
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
		if (sad.getAssemblyController() != null) {
			AssemblyController assemblyController = sad.getAssemblyController();
			if (assemblyController.getComponentInstantiationRef() != null) {
				if (PluginUtil.equals(compInst.getId(), assemblyController.getComponentInstantiationRef().getRefid())) {
					return true;
				}
			}
		}
		return false;
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
			commandClass = DeleteCommand.class;
		} else {
			commandClass = SetCommand.class;
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

	@Override
	protected Object getModelObject(EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			return getValueRef();
		} else if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return getExternalProperty();
		}
		return null;
	}

	@Override
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

	@Override
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
		return super.createParentCommand(domain, feature, value);
	}

	@Override
	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return SetCommand.create(domain, owner, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}

	@Override
	protected Command createDeleteCommand(EditingDomain domain, Object object, EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			ExternalProperties properties = (ExternalProperties) ((EObject) object).eContainer();
			if (properties.getProperties().size() == 1) {
				return DeleteCommand.create(domain, properties);
			}
		}
		return super.createDeleteCommand(domain, object, feature);
	}
}
