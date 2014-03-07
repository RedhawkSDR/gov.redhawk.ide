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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class TutorialAssociateDiagramEClassFeature extends AbstractCustomFeature {

	public TutorialAssociateDiagramEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "&Associate diagram"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Associate the diagram with this EClass"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length >= 1) {
			ret = true;
			for (PictogramElement pe : pes) {
				Object bo = getBusinessObjectForPictogramElement(pe);
				if (!(bo instanceof EClass)) {
					ret = false;
				}
			}
		}
		return ret;
	}

	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		EClass eClasses[] = new EClass[pes.length];
		for (int i = 0; i < eClasses.length; i++) {
			eClasses[i] = (EClass) getBusinessObjectForPictogramElement(pes[i]);
		}

		// associate selected EClass with diagram
		link(getDiagram(), eClasses);
	}

}
