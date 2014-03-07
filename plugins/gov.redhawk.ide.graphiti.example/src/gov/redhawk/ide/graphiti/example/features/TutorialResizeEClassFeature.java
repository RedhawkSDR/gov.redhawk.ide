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
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class TutorialResizeEClassFeature extends DefaultResizeShapeFeature {

	public TutorialResizeEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		boolean canResize = super.canResizeShape(context);

		// perform further check only if move allowed by default feature
		if (canResize) {
			// don't allow resize if the class name has the length of 1
			Shape shape = context.getShape();
			Object bo = getBusinessObjectForPictogramElement(shape);
			if (bo instanceof EClass) {
				EClass c = (EClass) bo;
				if (c.getName() != null && c.getName().length() == 1) {
					canResize = false;
				}
			}
		}
		return canResize;
	}
}
