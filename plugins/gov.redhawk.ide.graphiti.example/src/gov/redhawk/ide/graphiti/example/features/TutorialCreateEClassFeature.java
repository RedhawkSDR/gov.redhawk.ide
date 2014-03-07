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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class TutorialCreateEClassFeature extends AbstractCreateFeature {

	public TutorialCreateEClassFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "EClass", "Create EClass"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		// create EClass
		EClass newClass = EcoreFactory.eINSTANCE.createEClass();
		// Add model element to resource.
		// We add the model element to the resource of the diagram for
		// simplicity's sake. Normally, a customer would use its own
		// model persistence layer for storing the business model separately.
		getDiagram().eResource().getContents().add(newClass);

		//		Use the following instead of the above line to store the model
		//		data in a seperate file parallel to the diagram file
		//		try {
		//			try {
		//				TutorialUtil.saveToModelFile(newClass, getDiagram());
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		} catch (CoreException e) {
		//			e.printStackTrace();
		//		}

		// do the add
		addGraphicalRepresentation(context, newClass);

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		// return newly created business object(s)
		return new Object[] { newClass };
	}
}
