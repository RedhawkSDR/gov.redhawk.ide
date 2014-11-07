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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.math.BigInteger;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class IncrementStartOrderFeature extends AbstractCustomFeature {

	public IncrementStartOrderFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Move Start Order Later";
	public static final String DESCRIPTION = "Increment the start order by 1";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Returns true if linked business object is SadComponentInstantiation and
	 * Start Order is not the highest in the Software Assembly
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof SadComponentInstantiation) {
				// its a component
				SadComponentInstantiation ci = (SadComponentInstantiation) obj;

				// get sad from diagram
				final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

				// don't allow increment if there is not already a start order assigned
				if (ci.getStartOrder() == null) {
					return false;
				}

				// don't allow increment if its already the highest (components with start orders of null are removed from this assessment)
				EList<SadComponentInstantiation> sortedComponents = sad.getComponentInstantiationsInStartOrder();
				for (int i = 0; i < sortedComponents.size(); i++) {
					if (sortedComponents.get(i).getStartOrder() == null) {
						sortedComponents.remove(i);
						i--;
					}
				}
				if (sortedComponents.get(sortedComponents.size() - 1).equals(ci)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Decrements the start order for the Component
	 */
	@Override
	public void execute(ICustomContext context) {
		ComponentShape componentShape = (ComponentShape) context.getPictogramElements()[0];
		final SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// get current we are swapping start order with
		final SadComponentInstantiation swapCI = ComponentPattern.getComponentInstantiationViaStartOrder(sad, ci.getStartOrder().add(BigInteger.ONE));

		// swap start orders, also handle assembly controller changes
		ComponentPattern.swapStartOrder(sad, getFeatureProvider(), ci, swapCI);
	}

}