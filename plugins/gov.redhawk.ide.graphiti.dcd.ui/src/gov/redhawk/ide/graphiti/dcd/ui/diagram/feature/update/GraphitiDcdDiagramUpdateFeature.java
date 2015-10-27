/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.update;

import gov.redhawk.ide.graphiti.ui.diagram.features.update.AbstractDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class GraphitiDcdDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public GraphitiDcdDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected List<EObject> getObjectsToAdd(Diagram diagram) {
		List<EObject> addedObjects = new ArrayList<EObject>();
		DeviceConfiguration dcd = DUtil.getDiagramDCD(diagram);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			for (DcdComponentInstantiation instantiation : placement.getComponentInstantiation()) {
				if (!hasExistingShape(diagram, instantiation)) {
					addedObjects.add(instantiation);
				}
			}
		}
		return addedObjects;
	}

	@Override
	protected List<ConnectInterface< ? , ? , ? >> getModelConnections(Diagram diagram) {
		DeviceConfiguration dcd = DUtil.getDiagramDCD(diagram);
		List<ConnectInterface < ? , ? , ? >> connections = new ArrayList<ConnectInterface< ? , ? , ? >>();
		if (dcd != null && dcd.getConnections() != null) {
			connections.addAll(dcd.getConnections().getConnectInterface());
		}
		return connections;
	}

	/**
	 * Updates the Diagram to reflect the underlying business model
	 * Make sure all elements in dcd model (hosts/components/findby) are accounted for as
	 * children of diagram, if they aren't then add them, if they are then check to see if
	 * they need to be updated, if they exist in the diagram yet not in the model, remove them
	 * @param context
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	@Deprecated
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {
		if (!performUpdate) {
			// Match return type; this method should return IReason, but does not
			IReason reason = updateNeeded(context);
			return new Reason(reason.toBoolean(), reason.getText());
		} else {
			if (update(context)) {
				return new Reason(true, "Update successful");
			} else {
				return new Reason(false, "No updates required");
			}
		}
	}

}
