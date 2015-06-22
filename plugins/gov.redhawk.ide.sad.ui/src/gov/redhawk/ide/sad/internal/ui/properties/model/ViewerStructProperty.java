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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;

/**
 * 
 */
public class ViewerStructProperty extends ViewerProperty<Struct> {

	private List<ViewerProperty< ? >> fields = new ArrayList<ViewerProperty< ? >>();

	public ViewerStructProperty(Struct def, Object parent) {
		super(def, parent);
		for (FeatureMap.Entry field : def.getFields()) {
			if (field.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE) {
				fields.add(new ViewerSimpleProperty((Simple) field.getValue(), this));
			} else if (field.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE_SEQUENCE) {
				fields.add(new ViewerSequenceProperty((SimpleSequence) field.getValue(), this));				
			}
		}
	}

	@Override
	protected StructRef getRef() {
		return (StructRef) super.getRef();
	}

	protected AbstractPropertyRef< ? > getRef(final String refId) {
		StructRef structRef = getRef();
		if (structRef != null) {
			for (FeatureMap.Entry entry : structRef.getRefs()) {
				AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
				if (ref.getRefID().equals(refId)) {
					return ref;
				}
			}
		}
		return null;
	}

	public List<ViewerProperty< ? >> getFields() {
		return fields;
	}

	@Override
	public void setToDefault() {
	}

	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fields) {
			p.addPropertyChangeListener(listener);
		}
	}
	
	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fields) {
			p.removePropertyChangeListener(listener);
		}
	}

	@Override
	public Collection< ? > getChildren(Object object) {
		return getFields();
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getPrfValue() {
		return null;
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getConfigurationKind();
	}

	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		switch (((EObject) child).eClass().getClassifierID()) {
		case PrfPackage.SIMPLE_REF:
			return PrfPackage.Literals.STRUCT_REF__SIMPLE_REF;
		case PrfPackage.SIMPLE_SEQUENCE_REF:
			return PrfPackage.Literals.STRUCT_REF__SIMPLE_SEQUENCE_REF;
		}
		return null;
	}

	@Override
	protected Object createPeer(Object value) {
		StructRef ref = PrfFactory.eINSTANCE.createStructRef();
		ref.setRefID(getID());
		EStructuralFeature feature = getChildFeature(ref, value);
		ref.getRefs().add(feature, value);
		return ref;
	}
}
