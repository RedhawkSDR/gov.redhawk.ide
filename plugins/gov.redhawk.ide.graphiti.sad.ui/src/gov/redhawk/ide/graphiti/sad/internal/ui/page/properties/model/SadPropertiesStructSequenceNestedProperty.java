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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.XViewerCellEditor;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public abstract class SadPropertiesStructSequenceNestedProperty extends ItemProvider implements SadProperty {

	private final AbstractProperty definition;
	protected ILabelProvider labelProvider;  // SUPPRESS CHECKSTYLE INLINE

	public SadPropertiesStructSequenceNestedProperty(AdapterFactory adapterFactory, AbstractProperty def, Object parent) {
		super(adapterFactory);
		this.parent = parent;
		this.definition = def;
	}

	@Override
	public SadPropertiesStructSequence getParent() {
		return (SadPropertiesStructSequence) super.getParent();
	}

	protected abstract List< ? > getRefValues(List<StructValue> values);

	@Override
	public Object getPrfValue() {
		StructSequence seq = getParent().getDefinition();
		return getRefValues(seq.getStructValue());
	}

	@Override
	public Object getSadValue() {
		StructSequenceRef structSequenceRef = getParent().getValueRef();
		if (structSequenceRef != null) {
			return getRefValues(structSequenceRef.getStructValue());
		}
		return null;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element != null) {
						return Arrays.toString(((List< ? >) element).toArray());
					}
					return "";
				}

			};
		}
		return labelProvider;
	}

	@Override
	public Collection< ? > getKinds() {
		return getParent().getKinds();
	}

	@Override
	public AbstractProperty getDefinition() {
		return definition;
	}

	@Override
	public boolean isAssemblyControllerProperty() {
		return getParent().isAssemblyControllerProperty();
	}

	@Override
	public String getExternalID() {
		return null;
	}

	@Override
	public boolean canSetExternalId() {
		return false;
	}

	@Override
	public void setExternalID(String newExternalID) {
	}

	@Override
	public void setSadValue(Object value) {
	}

	@Override
	public String getID() {
		return definition.getId();
	}

	@Override
	public SadComponentInstantiation getComponentInstantiation() {
		return getParent().getComponentInstantiation();
	}

	@Override
	public XViewerCellEditor createCellEditor(Composite parent) {
		// Edited via dialog on the struct sequence
		return null;
	}
}
