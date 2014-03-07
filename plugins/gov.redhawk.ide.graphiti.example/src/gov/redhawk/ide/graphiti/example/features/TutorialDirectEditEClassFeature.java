/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.features;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class TutorialDirectEditEClassFeature extends AbstractDirectEditingFeature {

	public TutorialDirectEditEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	public int getEditingType() {
		// there are several possible editor-types supported:
		// text-field, checkbox, color-chooser, combobox, ...
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// support direct editing, if it is a EClass, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof EClass && ga instanceof Text) {
			// EClass eClass = (EClass) bo;
			// additionally the flag isFrozen must be false
			// return !eClass.isFrozen();
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
		PictogramElement pe = context.getPictogramElement();
		EClass eClass = (EClass) getBusinessObjectForPictogramElement(pe);
		return eClass.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1)
			return "Please enter any text as class name."; //$NON-NLS-1$
		if (value.contains(" ")) //$NON-NLS-1$
			return "Spaces are not allowed in class names."; //$NON-NLS-1$
		if (value.contains("\n")) //$NON-NLS-1$
			return "Line breakes are not allowed in class names."; //$NON-NLS-1$

		// null means, that the value is valid
		return null;
	}

	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the EClass
		PictogramElement pe = context.getPictogramElement();
		EClass eClass = (EClass) getBusinessObjectForPictogramElement(pe);
		eClass.setName(value);

		// Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of Graphiti
		// (currently in discussion)

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the EClass
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
