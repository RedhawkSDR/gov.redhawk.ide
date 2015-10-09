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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.omg.CORBA.BAD_OPERATION;

import BULKIO.PortStatistics;
import CF.DataType;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.monitor.IPortStatListener;
import gov.redhawk.monitor.MonitorPlugin;
import gov.redhawk.monitor.MonitorPortAdapter;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class PortMonitorDecoratorProvider implements IDecoratorProvider, IPortStatListener {

	private static final IColorConstant COLOR_OK = IColorConstant.GREEN;
	private static final IColorConstant COLOR_WARNING_1 = IColorConstant.YELLOW;
	private static final IColorConstant COLOR_WARNING_2 = new ColorConstant(255, 170, 0);
	private static final IColorConstant COLOR_WARNING_3 = new ColorConstant(255, 85, 0);
	private static final IColorConstant COLOR_ERROR = IColorConstant.RED;

	private static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	private IDiagramTypeProvider diagramTypeProvider;
	private MonitorPortAdapter adapter = new MonitorPortAdapter(this);

	@SuppressWarnings("serial")
	private static class ColorMap extends HashMap<String, IColorConstant> {
	}

	private Map<String, ColorMap> providesPortColors = new HashMap<String, ColorMap>();

	public PortMonitorDecoratorProvider(IDiagramTypeProvider diagramTypeProvider) {
		this.diagramTypeProvider = diagramTypeProvider;
		MonitorPlugin.getDefault().getMonitorRegistry().eAdapters().add(adapter);
	}

	public void dispose() {
		MonitorPlugin.getDefault().getMonitorRegistry().eAdapters().remove(adapter);
	}

	protected boolean isDiagramComponent(ScaComponent component) {
		return (component.getWaveform() == DUtil.getBusinessObject(diagramTypeProvider.getDiagram(), ScaWaveform.class));
	}

	protected RHContainerShape getComponentShape(ScaComponent component) {
		Diagram diagram = diagramTypeProvider.getDiagram();
		if (component.getWaveform() == DUtil.getBusinessObject(diagram, ScaWaveform.class)) {
			SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
			SadComponentInstantiation inst = sad.getComponentInstantiation(component.getInstantiationIdentifier());
			List<PictogramElement> pictograms = Graphiti.getLinkService().getPictogramElements(diagram, inst);
			if (pictograms.size() > 0) {
				return (RHContainerShape) pictograms.get(0);
			}
		}
		return null;
	}

	protected ContainerShape getProvidesPortPictogramElement(RHContainerShape component, String portName) {
		for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(component)) {
			ProvidesPortStub stub = (ProvidesPortStub) DUtil.getBusinessObject(portShape);
			if (portName.equals(stub.getName())) {
				return portShape;
			}
		}
		return null;
	}

	protected PictogramElement getProvidesPortPictogramElement(ScaComponent component, String portName) {
		RHContainerShape componentShape = getComponentShape(component);
		if (componentShape != null) {
			for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(componentShape)) {
				ProvidesPortStub stub = getProvidesPort(portShape);
				if (stub != null && portName.equals(stub.getName())) {
					return portShape.getChildren().get(0);
				}
			}
		}
		return null;
	}

	private void putProvidesColor(String instantiationId, String portName, IColorConstant color) {
		synchronized (providesPortColors) {
			ColorMap portColors = providesPortColors.get(instantiationId);
			if (portColors == null) {
				portColors = new ColorMap();
				providesPortColors.put(instantiationId, portColors);
			}
			portColors.put(portName, color);
		}
	}

	private boolean removeProvidesColor(String instantiationId, String portName) {
		synchronized (providesPortColors) {
			ColorMap portColors = providesPortColors.get(instantiationId);
			if (portColors == null) {
				return false;
			} else {
				portColors.remove(portName);
				return true;
			}
		}
	}

	private IColorConstant getProvidesColor(String instantiationId, String portName) {
		synchronized (providesPortColors) {
			ColorMap portColors = providesPortColors.get(instantiationId);
			if (portColors == null) {
				return null;
			} else {
				return portColors.get(portName);
			}
		}
	}

	@Override
	public void newStatistics(ScaPort< ? , ? > port, PortStatistics portStatistics) {
		ScaPortContainer container = port.getPortContainer();
		if (container instanceof ScaComponent) {
			ScaComponent component = (ScaComponent) container;
			if (isDiagramComponent(component)) {
				IColorConstant color = getProvidesMonitorColor(portStatistics);
				putProvidesColor(component.getInstantiationIdentifier(), port.getName(), color);
				refreshPort(component, port.getName());
			}
		}
	}

	private void refreshPort(final ScaComponent component, final String portName) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				PictogramElement pe = getProvidesPortPictogramElement(component, portName);
				if (pe != null) {
					diagramTypeProvider.getDiagramBehavior().refreshRenderingDecorators(pe);
				}
			}
		});
	}

	@Override
	public void newStatistics(ScaPort< ? , ? > port, String connectionId, PortStatistics portStatistics) {
	}

	@Override
	public void noStatistics(ScaPort< ? , ? > port) {
		ScaPortContainer container = port.getPortContainer();
		if (container instanceof ScaComponent) {
			ScaComponent component = (ScaComponent) container;
			if (isDiagramComponent(component)) {
				if (removeProvidesColor(component.getInstantiationIdentifier(), port.getName())) {
					refreshPort(component, port.getName());
				}
			}
		}
	}

	@Override
	public void noStatistics(ScaPort< ? , ? > port, String connectionId) {
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		if ((pe instanceof AnchorContainer) && !((AnchorContainer) pe).getAnchors().isEmpty()) {
			ProvidesPortStub portStub = getProvidesPort(pe);
			if (portStub != null) {
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(pe, RHContainerShape.class);
				if (portStub.getProvides() != null && componentShape != null) {
					ComponentInstantiation componentInstantiation = DUtil.getBusinessObject(componentShape, ComponentInstantiation.class);
					String portName = portStub.getProvides().getName();
					IDecorator decorator = getProvidesPortDecorator(componentInstantiation, portName);
					if (decorator != null) {
						return new IDecorator[] { decorator };
					}
				}
			}
		} else if (pe instanceof Connection) {
			ConnectInterface< ? , ? , ? > connectInterface = (ConnectInterface< ? , ? , ? >) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(
				pe);
			if (connectInterface != null) {
				Connection connection = (Connection) pe;
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(connection.getStart(), RHContainerShape.class);
				UsesPortStub portStub = connectInterface.getSource();
				if (portStub != null && portStub.getUses() != null && componentShape != null) {
					String portName = portStub.getUses().getName();
					IDecorator decorator = getConnectionDecorator(componentShape, portName, connectInterface.getId());
					if (decorator != null) {
						return new IDecorator[] { decorator };
					}
				}
			}
		}
		return PortMonitorDecoratorProvider.NO_DECORATORS;
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

	protected IDecorator getProvidesPortDecorator(ComponentInstantiation component, String portName) {
		IColorConstant color = getProvidesColor(component.getId(), portName);
		if (color != null) {
			return new ColorDecorator(null, color);
		}
		return null;
	}

	protected IDecorator getConnectionDecorator(RHContainerShape componentShape, String portName, String connectionId) {
		Map<String, BULKIO.PortStatistics> portStatistics = componentShape.getConnectionStates().get(portName);
		if (portStatistics != null) {
			BULKIO.PortStatistics statistics = portStatistics.get(connectionId);
			if (statistics != null) {
				IColorConstant color = getConnectionMonitorColor(statistics);
				return new ColorDecorator(color, color);
			}
		}
		return null;
	}

	protected IColorConstant getProvidesMonitorColor(BULKIO.PortStatistics statistics) {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		double queueDepthWarningLevel = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_LEVEL) / 100;
		double queueDepthIncrement = (1.0 - queueDepthWarningLevel) / 4;
		double lastFlushResetTime = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY);
		double lastFlush = getLastFlushTime(statistics.keywords);

		if (lastFlush < lastFlushResetTime || (lastFlush != Double.MAX_VALUE && lastFlushResetTime < 0)) {
			// If last flush reset time is set to -1, never reset the color if a flush has occurred
			return PortMonitorDecoratorProvider.COLOR_ERROR;
		} else if (statistics.averageQueueDepth < queueDepthWarningLevel) {
			return PortMonitorDecoratorProvider.COLOR_OK;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + queueDepthIncrement)) {
			return PortMonitorDecoratorProvider.COLOR_WARNING_1;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + 2 * queueDepthIncrement)) {
			return PortMonitorDecoratorProvider.COLOR_WARNING_2;
		} else if (statistics.averageQueueDepth < (queueDepthWarningLevel + 3 * queueDepthIncrement)) {
			return PortMonitorDecoratorProvider.COLOR_WARNING_3;
		} else {
			return PortMonitorDecoratorProvider.COLOR_ERROR;
		}
	}

	protected IColorConstant getConnectionMonitorColor(BULKIO.PortStatistics statistics) {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		double lastCallWarningLevel = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS);
		if (statistics.timeSinceLastCall < lastCallWarningLevel) {
			return PortMonitorDecoratorProvider.COLOR_OK;
		} else {
			return PortMonitorDecoratorProvider.COLOR_WARNING_1;
		}
	}

	private double getLastFlushTime(DataType[] keywords) {
		if (keywords != null) {
			for (DataType keyword : keywords) {
				if ("timeSinceLastFlush".equals(keyword.id)) {
					try {
						return keyword.value.extract_double();
					} catch (BAD_OPERATION e) {
						StatusManager.getManager().handle(new Status(Status.WARNING, GraphitiUIPlugin.PLUGIN_ID,
							"Expected double value for timeSinceLastFlush keyword (TCKind was " + keyword.value.type().kind() + ")", e));
					}
				}
			}
		}
		return Double.MAX_VALUE;
	}
}
