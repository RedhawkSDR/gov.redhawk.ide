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

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.update.AbstractDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class GraphitiDcdDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public GraphitiDcdDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
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
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {

		boolean updateStatus = false;

		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram d = (Diagram) pe;

			// get dcd from diagram
			final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

			// model devices // TODO: Need to do this for services too
			List<DcdComponentInstantiation> componentInstantiations = new ArrayList<DcdComponentInstantiation>();
			if (dcd != null && dcd.getPartitioning() != null && dcd.getPartitioning().getComponentPlacement() != null) {
				// Get list of componentInstantiations from model
				for (DcdComponentPlacement p : dcd.getPartitioning().getComponentPlacement()) {
					Collections.addAll(componentInstantiations,
						(DcdComponentInstantiation[]) p.getComponentInstantiation().toArray(new DcdComponentInstantiation[0]));
				}
			}
			
			// shape devices
			// TODO: Need to update this to look for services
			List<DeviceShape> deviceShapes = DevicePattern.getAllDeviceShapes(d);
			for (Iterator<DeviceShape> iter = deviceShapes.iterator(); iter.hasNext();) {
				if (!(iter.next().eContainer() instanceof Diagram)) {
					iter.remove();
				}
			}

			// model connections
			List<DcdConnectInterface> dcdConnectInterfaces = new ArrayList<DcdConnectInterface>();
			if (dcd != null && dcd.getConnections() != null && dcd.getConnections().getConnectInterface() != null) {
				// Get list of DcdConnectInterfaces from model
				Collections.addAll(dcdConnectInterfaces, (DcdConnectInterface[]) dcd.getConnections().getConnectInterface().toArray(new DcdConnectInterface[0]));
			}
			// remove invalid model connections
			removeInvalidConnections(dcdConnectInterfaces);

			// shape connections
			List<Connection> connections = new ArrayList<Connection>();
			Collections.addAll(connections, (Connection[]) d.getConnections().toArray(new Connection[0]));

			// If inconsistencies are found remove all objects of that type and redraw
			// we must do this because the diagram uses indexed lists to refer to components in the dcd file.
			if (performUpdate) {
				updateStatus = true;

				List<PictogramElement> pesToRemove = new ArrayList<PictogramElement>(); // gather all shapes to remove
				List<Object> objsToAdd = new ArrayList<Object>(); // gather all model object to add

				// If inconsistencies found, redraw diagram elements based on model objects
				boolean layoutNeeded = false;
				if (deviceShapes.size() != componentInstantiations.size() || !devicesResolved(deviceShapes)) {
					Collections.addAll(pesToRemove, (PictogramElement[]) deviceShapes.toArray(new PictogramElement[0]));
					Collections.addAll(objsToAdd, (Object[]) componentInstantiations.toArray(new Object[0]));
					layoutNeeded = true;
				}

				// Easiest just to remove and redraw connections every time
				Collections.addAll(pesToRemove, (PictogramElement[]) connections.toArray(new PictogramElement[0]));

				if (!pesToRemove.isEmpty()) {
					// remove shapes from diagram
					for (PictogramElement peToRemove : pesToRemove) {
						// remove shape
						RemoveContext rc = new RemoveContext(peToRemove);
						IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(rc);
						if (removeFeature != null) {
							removeFeature.remove(rc);
						}
					}
				} else {
					// update components
					super.update(context);
				}

				// add shapes to diagram
				if (!objsToAdd.isEmpty()) {
					for (Object objToAdd : objsToAdd) {
						DUtil.addShapeViaFeature(getFeatureProvider(), getDiagram(), objToAdd);
					}
				}

				// add connections to diagram
				addConnections(dcdConnectInterfaces, getDiagram(), getFeatureProvider());

				if (layoutNeeded) {
					LayoutDiagramFeature layoutFeature = new LayoutDiagramFeature(getFeatureProvider());
					layoutFeature.execute(null);
				}
			} else {
				if (deviceShapes.size() != componentInstantiations.size() || !devicesResolved(deviceShapes)) {
					return new Reason(true, "The dcd.xml file and diagram component objects do not match.  Reload the diagram from the xml file.");
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/** Checks if componentShape has lost its reference to the model object */
	private boolean devicesResolved(List<DeviceShape> deviceShapes) {
		for (DeviceShape deviceShape : deviceShapes) {
			DcdComponentInstantiation dcdComponentInstantiation = (DcdComponentInstantiation) DUtil.getBusinessObject(deviceShape, DcdComponentInstantiation.class);
			if (dcdComponentInstantiation == null || dcdComponentInstantiation.getPlacement() == null || dcdComponentInstantiation.getPlacement().getComponentFileRef() == null) {
				return false;
			}
			
			if (!GraphitiUIPlugin.getDefault().getPreferenceStore().getBoolean(DiagramPreferenceConstants.HIDE_DETAILS)) {
				//applies only if we are showing the component shape details (ports)
				if (deviceShape.getProvidesPortStubs().size() > 0 && !deviceShape.getProvidesPortStubs().get(0).eContainer().equals(dcdComponentInstantiation)) {
					return false;
				} else if (deviceShape.getUsesPortStubs().size() > 0 && !deviceShape.getUsesPortStubs().get(0).eContainer().equals(dcdComponentInstantiation)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Reason updateNeeded(IUpdateContext context) {
		try {
			return internalUpdate(context, false);
		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
		}
		return null;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason reason;
		try {
			reason = internalUpdate(context, true);
			return reason.toBoolean();
		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
			e.printStackTrace(); // SUPPRESS CHECKSTYLE INLINE
		}

		return false;
	}
}
