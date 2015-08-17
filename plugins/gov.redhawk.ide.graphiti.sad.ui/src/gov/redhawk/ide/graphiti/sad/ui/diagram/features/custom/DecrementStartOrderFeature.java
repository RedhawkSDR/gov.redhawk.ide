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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom;

import java.math.BigInteger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class DecrementStartOrderFeature extends AbstractComponentInstantiationFeature {

	public DecrementStartOrderFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Move Start Order Earlier";
	public static final String DESCRIPTION = "Decrement the start order by 1";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Can't execute if the diagram is read-only
		if (DUtil.isDiagramReadOnly(getDiagram())) {
			return false;
		}

		// May only have 1 component instantiation selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes.length != 1) {
			return false;
		}

		// Don't allow decrement if there is not already a start order assigned
		SadComponentInstantiation compInst = (SadComponentInstantiation) DUtil.getBusinessObject(pes[0]);
		if (compInst.getStartOrder() == null) {
			return false;
		}

		// Don't allow decrement if its already the lowest start order. Note that the assembly controller is always
		// first, and may not have a start order, so we have to check the first and second instances in the ordered
		// list.
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		EList<SadComponentInstantiation> sortedComponents = sad.getComponentInstantiationsInStartOrder();
		SadComponentInstantiation firstComponent = sortedComponents.get(0);
		if (firstComponent.equals(compInst)) {
			return false;
		}
		if (sortedComponents.size() > 1 && firstComponent.getStartOrder() == null && sortedComponents.get(1).equals(compInst)) {
			return false;
		}

		// Start order must be greater than zero
		return compInst.getStartOrder().compareTo(BigInteger.ZERO) > 0;
	}

	/**
	 * Decrements the start order for the Component
	 */
	@Override
	public void execute(ICustomContext context) {
		ComponentShape componentShape = (ComponentShape) context.getPictogramElements()[0];
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// get current we are swapping start order with
		SadComponentInstantiation swapCI = ComponentPattern.getComponentInstantiationViaStartOrder(sad, ci.getStartOrder().subtract(BigInteger.ONE));

		// swap start orders, also handle assembly controller changes
		ComponentPattern.swapStartOrder(sad, getFeatureProvider(), swapCI, ci);

		// force pictogram objects to update
		updatePictogramElement(componentShape);
		updatePictogramElement(DUtil.getPictogramElementForBusinessObject(getDiagram(), swapCI, ComponentShape.class));
	}

}
