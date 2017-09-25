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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.custom;

import java.math.BigInteger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.core.graphiti.dcd.ui.utils.DCDUtils;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class IncrementStartOrderFeature extends AbstractComponentInstantiationFeature {

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
	
	@Override
	public boolean isAvailable(IContext context) {
		if (!super.isAvailable(context)) {
			return false;
		}

		ICustomContext customContext = (ICustomContext) context;
		PictogramElement[] pes = customContext.getPictogramElements();
		DcdComponentInstantiation compInst = (DcdComponentInstantiation) DUtil.getBusinessObject(pes[0]);
		if (compInst.getStartOrder() == null) {
			return false;
		}

		return super.isAvailable(context);
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

		// Don't allow increment if there is not already a start order assigned
		DcdComponentInstantiation compInst = (DcdComponentInstantiation) DUtil.getBusinessObject(pes[0]);
		if (compInst.getStartOrder() == null) {
			return false;
		}

		// Don't allow increment if its already the highest
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		EList<DcdComponentInstantiation> sortedComponents = dcd.getComponentInstantiationsInStartOrder();
		for (int i = sortedComponents.size() - 1; i >= 0; i--) {
			if (sortedComponents.get(i).getStartOrder() != null) {
				return !sortedComponents.get(i).equals(compInst);
			}
		}

		return true;
	}

	/**
	 * Decrements the start order for the Component
	 */
	@Override
	public void execute(ICustomContext context) {
		RHContainerShape containerShape = (RHContainerShape) context.getPictogramElements()[0];
		final DcdComponentInstantiation ci = (DcdComponentInstantiation) DUtil.getBusinessObject(containerShape);

		// get sad from diagram
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

		// get current we are swapping start order with
		final DcdComponentInstantiation swapCI = DCDUtils.getComponentInstantiationViaStartOrder(dcd, ci.getStartOrder().add(BigInteger.ONE));

		// swap start orders, also handle assembly controller changes
		DCDUtils.swapStartOrder(dcd, getFeatureProvider(), ci, swapCI);

		// force pictogram objects to update
		updatePictogramElement(containerShape);
		updatePictogramElement(DUtil.getPictogramElementForBusinessObject(getDiagram(), swapCI, RHContainerShape.class));
	}

}
