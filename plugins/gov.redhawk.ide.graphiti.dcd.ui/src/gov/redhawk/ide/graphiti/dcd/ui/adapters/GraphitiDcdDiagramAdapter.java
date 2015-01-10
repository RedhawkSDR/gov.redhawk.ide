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
package gov.redhawk.ide.graphiti.dcd.ui.adapters;

import gov.redhawk.ide.graphiti.dcd.internal.ui.GraphitiDcdModelMap;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * This class should be added as an adapter to the Graphiti Diagram. When events occur this class is notified
 * and can make calls to ModelMap. ModelMap is the interface between the diagram and runtime state
 */
public class GraphitiDcdDiagramAdapter extends EContentAdapter {

	private final GraphitiDcdModelMap modelMap;

	public GraphitiDcdDiagramAdapter(final GraphitiDcdModelMap modelMap) {
		this.modelMap = modelMap;
	}

	// TODO: This only handles starting/stopping via the Node Explorer
	// Will need to expand behavior here once we start working on the Node Chalkboard diagram

	/**
	 * Listen for events in Graphiti Shapes model
	 * Here we are waiting for the "started" value of ComponentShape to be set. When its set we use the modelmap to
	 * carry
	 * out the appropriate actions in the runtime environment.
	 */
	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		if (notification.getNotifier() instanceof RHContainerShape) {
			switch (notification.getFeatureID(RHGxPackage.class)) {
			case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
				switch (notification.getEventType()) {
				case Notification.SET:
					final RHContainerShape rhContainerShape = (RHContainerShape) notification.getNotifier();
					DcdComponentInstantiation ci = (DcdComponentInstantiation) DUtil.getBusinessObject(rhContainerShape);
					this.modelMap.startStopDevice(ci, (Boolean) notification.getNewValue());
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}
}
