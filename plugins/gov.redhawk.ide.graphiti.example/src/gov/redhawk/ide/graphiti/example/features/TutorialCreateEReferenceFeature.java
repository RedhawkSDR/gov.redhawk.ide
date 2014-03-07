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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class TutorialCreateEReferenceFeature extends AbstractCreateConnectionFeature {

	public TutorialCreateEReferenceFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "EReference", "Create EReference"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean canCreate(ICreateConnectionContext context) {
		// return true if both anchors belong to a EClass
		// and those EClasses are not identical
		EClass source = getEClass(context.getSourceAnchor());
		EClass target = getEClass(context.getTargetAnchor());
		if (source != null && target != null && source != target) {
			return true;
		}
		return false;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		// return true if start anchor belongs to a EClass
		if (getEClass(context.getSourceAnchor()) != null) {
			return true;
		}
		return false;
	}

	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get EClasses which should be connected
		EClass source = getEClass(context.getSourceAnchor());
		EClass target = getEClass(context.getTargetAnchor());

		if (source != null && target != null) {
			// create new business object
			EReference eReference = createEReference(source, target);

			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
			addContext.setNewObject(eReference);
			newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		}

		return newConnection;
	}

	/**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	private EClass getEClass(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
			if (obj instanceof EClass) {
				return (EClass) obj;
			}
		}
		return null;
	}

	/**
	 * Creates a EReference between two EClasses.
	 */
	private EReference createEReference(EClass source, EClass target) {
		EReference eReference = EcoreFactory.eINSTANCE.createEReference();
		eReference.setName("new EReference"); //$NON-NLS-1$
		eReference.setEType(target);
		eReference.setLowerBound(0);
		eReference.setUpperBound(1);
		source.getEStructuralFeatures().add(eReference);
		return eReference;
	}

}
