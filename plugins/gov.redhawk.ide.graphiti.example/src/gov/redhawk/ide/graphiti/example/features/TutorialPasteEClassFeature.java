/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    mwenz - Bug 339525 - Enrich paste context with location information
 *    mwenz - Bug 375533 - Problems with copy&paste in the tutorial
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

public class TutorialPasteEClassFeature extends AbstractPasteFeature {

	public TutorialPasteEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canPaste(IPasteContext context) {
		// only support pasting directly in the diagram (nothing else selected)
		PictogramElement[] pes = context.getPictogramElements();
		if (pes.length != 1 || !(pes[0] instanceof Diagram)) {
			return false;
		}

		// can paste, if clipboard is filled and all objects on the clipboard
		// are EClasses that can be resolved
		Object[] fromClipboard = getFromClipboard();
		if (fromClipboard == null || fromClipboard.length == 0) {
			return false;
		}
		for (Object object : fromClipboard) {
			if (object instanceof EObject) {
				if (!isResolvable((EObject) object)) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public void paste(IPasteContext context) {
		// we already verified, that we paste directly in the diagram
		PictogramElement[] pes = context.getPictogramElements();
		Diagram diagram = (Diagram) pes[0];
		// get the EClasses from the clipboard without copying them
		// (only copy the pictogram element, not the business object)
		// then create new pictogram elements using the add feature
		Object[] objects = getFromClipboard();
		for (Object object : objects) {
			AddContext ac = new AddContext();
			// For simplicity paste all objects at the location given in the
			// context (no stacking or similar)
			ac.setLocation(context.getX(), context.getY());
			ac.setTargetContainer(diagram);
			addGraphicalRepresentation(ac, object);
		}
	}
}
