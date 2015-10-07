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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;
import org.omg.CORBA.BAD_OPERATION;

import CF.DataType;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class PortMonitorDecoratorProvider implements IDecoratorProvider {

	private static final IColorConstant COLOR_OK = IColorConstant.GREEN;
	private static final IColorConstant COLOR_WARNING_1 = IColorConstant.YELLOW;
	private static final IColorConstant COLOR_WARNING_2 = new ColorConstant(255, 170, 0);
	private static final IColorConstant COLOR_WARNING_3 = new ColorConstant(255, 85, 0);
	private static final IColorConstant COLOR_ERROR = IColorConstant.RED;

	private static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		if ((pe instanceof AnchorContainer) && !((AnchorContainer) pe).getAnchors().isEmpty()) {
			ProvidesPortStub portStub = getProvidesPort(pe);
			if (portStub != null) {
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(pe, RHContainerShape.class);
				if (portStub.getProvides() != null && componentShape != null) {
					String portName = portStub.getProvides().getName();
					IDecorator decorator = getMonitoredPortDecorator(componentShape, portName);
					if (decorator != null) {
						return new IDecorator[] { decorator };
					}
				}
			}
		} else if (pe instanceof Connection){
			ConnectInterface< ? , ? , ? > connectInterface = (ConnectInterface< ?, ?, ? >) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (connectInterface != null) {
				Connection connection = (Connection) pe;
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(connection.getStart(), RHContainerShape.class);
				String styleId = componentShape.getConnectionMap().get(connectInterface.getId());
				if (styleId != null) {
					IColorConstant color = getMonitorColor(styleId);
					IColorDecorator decorator = new ColorDecorator(color, color);
					return new IDecorator[] { decorator };
				}
			}
		}
		return NO_DECORATORS;
	}

	private ProvidesPortStub getProvidesPort(PictogramElement pe) {
		ProvidesPortStub portStub = null;
		for (EObject object : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(pe)) {
			if (object instanceof ProvidesPortStub) {
				if (portStub == null) {
					portStub = (ProvidesPortStub) object;
				} else {
					// More than one provides port is linked with the PictogramElement, so it must be a super port
					return null;
				}
			}
		}
		return portStub;
	}

	protected IColorConstant getMonitorColor(String styleId) {
		if (styleId != null) {
			if (StyleUtil.PORT_STYLE_OK.equals(styleId) || StyleUtil.CONNECTION_OK.equals(styleId)) {
				return StyleUtil.COLOR_OK;
			} else if (StyleUtil.PORT_STYLE_WARN4.equals(styleId) || StyleUtil.CONNECTION_ERROR.equals(styleId)) {
				return StyleUtil.COLOR_ERROR;
			} else {
				return StyleUtil.COLOR_WARN;
			}
		}
		return null;
	}

	protected IColorConstant getMonitorColor(BULKIO.PortStatistics statistics) {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		double queueDepthWarningLevel = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_LEVEL) / 100;
		double queueDepthIncrement = (1.0 - queueDepthWarningLevel) / 4;
		double lastFlushResetTime = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY);
		double lastFlush = getLastFlushTime(statistics.keywords);

		if (lastFlush < lastFlushResetTime || (lastFlush != Double.MAX_VALUE && lastFlushResetTime < 0)) {
			// If last flush reset time is set to -1, never reset the color if a flush has occurred
			return COLOR_ERROR;
		} else if (statistics.averageQueueDepth < queueDepthWarningLevel) {
			return COLOR_OK;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + queueDepthIncrement)) {
			return COLOR_WARNING_1;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + 2 * queueDepthIncrement)) {
			return COLOR_WARNING_2;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + 3 * queueDepthIncrement)) {
			return COLOR_WARNING_3;
		} else {
			return COLOR_ERROR;
		}
	}

	protected IDecorator getMonitoredPortDecorator(RHContainerShape componentShape, String portName) {
		BULKIO.PortStatistics statistics = componentShape.getPortStates().get(portName);
		if (statistics != null) {
			return new ColorDecorator(null, getMonitorColor(statistics));
		}
		return null;
	}

	private double getLastFlushTime(DataType[] keywords) {
		if (keywords != null) {
			for (DataType keyword : keywords) {
				if ("timeSinceLastFlush".equals(keyword.id)) {
					try {
						return keyword.value.extract_double();
					} catch (BAD_OPERATION e) {
						StatusManager.getManager().handle(
							new Status(Status.WARNING, GraphitiUIPlugin.PLUGIN_ID, "Expected double value for timeSinceLastFlush keyword (TCKind was " + keyword.value.type().kind() + ")", e));
					}
				}
			}
		}
		return Double.MAX_VALUE;
	}
}
