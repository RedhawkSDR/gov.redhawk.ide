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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.properties.model;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.graphiti.sad.internal.ui.editor.properties.XViewerCellEditor;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 *
 */
public abstract class SadPropertyImpl< T extends AbstractProperty > extends ItemProvider implements SadProperty {

	protected final T def;
	protected AbstractPropertyRef< ? > ref;
	protected ExternalProperty externalProperty;

	private Adapter adapter = null;
	private ILabelProvider labelProvider = null;

	/**
	 *
	 */
	public SadPropertyImpl(AdapterFactory adapterFactory, T def, Object parent) {
		super(adapterFactory);
		this.def = def;
		this.parent = parent;
	}

	@Override
	public void dispose() {
		if (ref != null) {
			ref.eAdapters().remove(adapter);
		}
		setExternalProperty(null);

		// Dispose of nested children (e.g., in structs and struct sequences)
		for (Object child : children) {
			((IDisposable) child).dispose();
		}
		children.clear();
	}

	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public Object getParent(Object object) {
		// Workaround for reverse tree traversal via an AdapterFactoryContentProvider, which will always call the
		// 1-argument getParent. If we return the ViewerComponent object, getParent(Object) on it will fail because
		// it's not the EObject, so this unwraps it. Property-specific code that needs to refer to the parent should
		// use the no argument version.
		if (parent instanceof SadPropertiesComponent) {
			return ((SadPropertiesComponent) parent).getComponentInstantiation();
		}
		return parent;
	}

	@Override
	public boolean canSetExternalId() {
		return (parent instanceof SadPropertiesComponent);
	}

	protected AbstractPropertyRef< ? > getValueRef() {
		return ref;
	}

	@Override
	public T getDefinition() {
		return this.def;
	}

	public XViewerCellEditor createCellEditor(Composite parent) {
		return null;
	}

	public SadPropertiesComponent getComponent() {
		if (parent instanceof SadPropertiesComponent) {
			return (SadPropertiesComponent) parent;
		} else {
			return ((SadPropertyImpl< ? >) parent).getComponent();
		}
	}

	@Override
	public SadComponentInstantiation getComponentInstantiation() {
		return getComponent().getComponentInstantiation();
	}

	@Override
	public String getExternalID() {
		if (externalProperty != null) {
			return externalProperty.getExternalPropID();
		}
		return null;
	}

	@Override
	public String getID() {
		return def.getId();
	}

	@Override
	public boolean isAssemblyControllerProperty() {
		return getComponent().isAssemblyController();
	}

	protected void setFeatureValue(EStructuralFeature feature, Object value) {
		EditingDomain editingDomain = getEditingDomain();
		Class< ? > commandClass;
		if (value == null) {
			commandClass = RemoveCommand.class;
		} else {
			commandClass = SetCommand.class;
		}
		Command command = createCommand(editingDomain, commandClass, feature, value);
		if (command != null && command.canExecute()) {
			editingDomain.getCommandStack().execute(command);
		}
	}

	protected abstract boolean isEmpty(Object value);

	@Override
	public ILabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = createLabelProvider();
		}
		return labelProvider;
	}

	protected abstract ILabelProvider createLabelProvider();

	@Override
	public void setSadValue(Object value) {
		if (value != null && isEmpty(value)) {
			value = null;
		}
		setFeatureValue(SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE, value);
	}

	@Override
	public void setExternalID(String newExternalID) {
		if (newExternalID != null) {
			newExternalID = newExternalID.trim();
			if (newExternalID.isEmpty()) {
				newExternalID = null;
			}
		}
		setFeatureValue(SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID, newExternalID);
	}

	@Override
	public Collection< ? > getKinds() {
		if (getParent() instanceof SadProperty) {
			return ((SadProperty) getParent()).getKinds();
		}
		return getKindTypes();
	}

	protected abstract Collection< ? > getKindTypes();

	public EditingDomain getEditingDomain() {
		return ((NestedItemProvider) getParent()).getEditingDomain();
	}

	protected Object getModelObject(EStructuralFeature feature) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			return ref;
		} else if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return externalProperty;
		}
		return null;
	}

	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
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
		if (modelObject == null && (commandClass == SetCommand.class)) {
			return createParentCommand(domain, feature, createModelObject(feature, value));
		}
		if (commandClass == SetCommand.class) {
			return createSetCommand(domain, modelObject, feature, value);
		} else if (commandClass == RemoveCommand.class) {
			return createRemoveCommand(domain, modelObject, feature);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createParentCommand(EditingDomain domain, EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			SoftwareAssembly sad = getComponent().getSoftwareAssembly();
			ExternalProperties properties = sad.getExternalProperties();
			if (properties != null) {
				return AddCommand.create(domain, properties, SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES, value);
			} else {
				properties = SadFactory.eINSTANCE.createExternalProperties();
				properties.getProperties().add((ExternalProperty) value);
				return SetCommand.create(domain, sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES, properties);
			}
		}
		return ((NestedItemProvider) getParent()).createAddChildCommand(domain, value, feature);
	}

	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			return SetCommand.create(domain, owner, SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createRemoveCommand(EditingDomain domain, Object object, EStructuralFeature feature) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__EXTERNAL_ID) {
			ExternalProperties properties = (ExternalProperties) ((EObject) object).eContainer();
			if (properties.getProperties().size() == 1) {
				return RemoveCommand.create(domain, properties);
			} else {
				return RemoveCommand.create(domain, object);
			}
		} else if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			NestedItemProvider parentProvider = (NestedItemProvider) getParent();
			return parentProvider.createRemoveChildCommand(domain, object, feature);
		}
		return UnexecutableCommand.INSTANCE;
	}

	public void setReference(AbstractPropertyRef< ? > reference) {
		if (ref == reference) {
			return;
		}

		unregisterAdapter(ref);
		registerAdapter(reference);
		ref = reference;
	}

	public void setExternalProperty(ExternalProperty externalProperty) {
		if (this.externalProperty == externalProperty) {
			return;
		}

		unregisterAdapter(this.externalProperty);
		registerAdapter(externalProperty);
		this.externalProperty = externalProperty;
	}

	protected Adapter getAdapter() {
		if (adapter == null) {
			adapter = createAdapter();
		}
		return adapter;
	}

	protected Adapter createAdapter() {
		return new AdapterImpl() {

			@Override
			public void notifyChanged(Notification msg) {
				SadPropertyImpl.this.notifyChanged(msg);
			}

		};
	}

	protected void notifyChanged(Notification msg) {
		if (msg.getFeature() == SadPackage.Literals.EXTERNAL_PROPERTY__EXTERNAL_PROP_ID) {
			fireNotifyChanged(new ViewerNotification(msg, this, false, true));
		}
	}

	private void registerAdapter(EObject eObject) {
		if (eObject != null) {
			eObject.eAdapters().add(getAdapter());
		}
	}

	private void unregisterAdapter(EObject eObject) {
		if (eObject != null) {
			eObject.eAdapters().remove(getAdapter());
		}
	}
}
