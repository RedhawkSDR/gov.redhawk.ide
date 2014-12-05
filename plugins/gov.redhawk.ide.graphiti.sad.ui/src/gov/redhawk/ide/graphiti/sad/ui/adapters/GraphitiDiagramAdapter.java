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
package gov.redhawk.ide.graphiti.sad.ui.adapters;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.RHSadGxPackage;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.debug.internal.ui.GraphitiModelMap;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * This class should be added as an adapter to the Graphiti Diagram. When events occur this class is notified
 * and can make calls to ModelMap. ModelMap is the interface between the diagram and runtime state
 */
public class GraphitiDiagramAdapter extends EContentAdapter {

	private final GraphitiModelMap modelMap;

	public GraphitiDiagramAdapter(final GraphitiModelMap modelMap) {
		this.modelMap = modelMap;
	}

	/**
	 * Listen for events in Graphiti Shapes model
	 * Here we are waiting for the "started" value of ComponentShape to be set. When its set we use the modelmap to
	 * carry
	 * out the appropriate actions in the runtime environment.
	 */
	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		switch (notification.getEventType()) {
		case Notification.SET:
			if (RHSadGxPackage.COMPONENT_SHAPE__STARTED == notification.getFeatureID(RHGxPackage.class)) {
				if (notification.getNotifier() instanceof ComponentShape) {
					final ComponentShape componentShape = (ComponentShape) notification.getNotifier();
					SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
					this.modelMap.startStopComponent(ci, (Boolean) notification.getNewValue());
				}
			}
			break;
		default:
			break;
		}
	}

}
