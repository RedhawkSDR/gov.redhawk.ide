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
package gov.redhawk.ide.sad.graphiti.ui.adapters;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.sad.graphiti.debug.internal.ui.GraphitiModelMap;
import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.Event;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.debug.core.DebugException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
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
			if (RHGxPackage.COMPONENT_SHAPE__STARTED == notification.getFeatureID(RHGxPackage.class)) {
				if (notification.getNotifier() instanceof ComponentShape) {
					final ComponentShape componentShape = (ComponentShape) notification.getNotifier();
					SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
					this.modelMap.startStopComponent(ci, (Boolean) notification.getNewValue());
				}
			} else if (RHGxPackage.COMPONENT_SHAPE__EVENT == notification.getFeatureID(RHGxPackage.class)) {
				if (notification.getNotifier() instanceof ComponentShape) {
					final ComponentShape componentShape = (ComponentShape) notification.getNotifier();
					if (componentShape.getEvent().equals(Event.RELEASE)) {
						SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
						this.modelMap.remove(ci);
					} else if (componentShape.getEvent().equals(Event.TERMINATE)) {
						SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
						LocalLaunch localLaunch = null;

						if (ci != null && ci.eResource() != null) {
							final URI uri = ci.eResource().getURI();
							final Map<String, String> query = QueryParser.parseQuery(uri.query());
							final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
							final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
							final String myId = ci.getId();
							if (waveform != null) {
								for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
									final String scaComponentId = component.identifier();
									if (scaComponentId.startsWith(myId)) {
										if (component instanceof LocalLaunch) {
											localLaunch = (LocalLaunch) component;
										}
									}
								}
							}

							if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
								try {
									localLaunch.getLaunch().getProcesses()[0].terminate();
								} catch (DebugException e) {
									// PASS
									// TODO Seems like it would be worth pushing a notification to the error log if a
									// component fails to terminate.
								}
							}
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}

}
