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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.DecrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.IncrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkNonExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.SetAsAssemblyControllerFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.delete.SADConnectionInterfaceDeleteFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.delete.ComponentReleaseFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.reconnect.SADReconnectFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.update.GraphitiWaveformDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.update.SADConnectionInterfaceUpdateFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDevicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.DisabledDeleteFeatureWrapper;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.EditLogConfigFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ShowConsoleFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StartFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StopFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.TailLogFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.TerminateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.remove.FastRemoveFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiFeatureProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class SADDiagramFeatureProvider extends AbstractGraphitiFeatureProvider {

	public SADDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// Add Patterns for Domain Objects
		addPattern(new ComponentPattern());
		addConnectionPattern(new SADConnectInterfacePattern());
		addPattern(new HostCollocationPattern());
		addPattern(new UsesDeviceFrontEndTunerPattern());
		addPattern(new UsesDevicePattern());
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		ICustomFeature[] parentCustomFeatures = super.getCustomFeatures(context);
		List<ICustomFeature> retList = new ArrayList<ICustomFeature>(Arrays.asList(parentCustomFeatures));

		Diagram diagram = getDiagramTypeProvider().getDiagram();
		PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length == 0) {
			return retList.toArray(new ICustomFeature[retList.size()]);
		}
		Object businessObject = DUtil.getBusinessObject(pes[0]);

		if (pes[0] instanceof RHContainerShape) {
			if (businessObject instanceof SadComponentInstantiation) {
				// Component features
				if (DUtil.isDiagramRuntime(diagram)) {
					// Runtime-only component features
					retList.add(new StartFeature(this));
					retList.add(new StopFeature(this));
					retList.add(new ShowConsoleFeature(this));
					retList.add(new LogLevelFeature(this));
					retList.add(new EditLogConfigFeature(this));
					retList.add(new TailLogFeature(this));

					// Don't add ability to remove components from Graphiti Waveform Explorer
					if (!DUtil.isDiagramExplorer(diagram)) {
						retList.add(new TerminateFeature(this));
					}
				} else {
					// Design-time-only component features
					retList.add(new SetAsAssemblyControllerFeature(this));
					retList.add(new IncrementStartOrderFeature(this));
					retList.add(new DecrementStartOrderFeature(this));
				}
			}
		}

		// add external port menu item if we clicked on a port
		if (!DUtil.isDiagramRuntime(diagram)) {
			// make sure business object is port stub and container is a component
			EObject obj = (EObject) businessObject;
			if ((obj instanceof ProvidesPortStub || obj instanceof UsesPortStub) && obj.eContainer() instanceof SadComponentInstantiation) {
				boolean mark = true;

				// get sad from diagram
				final SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);

				if (sad.getExternalPorts() != null) {

					// get external ports
					EList<Port> externalPortList = sad.getExternalPorts().getPort();

					// if it's already there disable this feature
					if (obj instanceof ProvidesPortStub) {
						for (Port p : externalPortList) {
							if (((ProvidesPortStub) obj).getName().equals(p.getProvidesIdentifier())
								&& ((ProvidesPortStub) obj).eContainer().equals(p.getComponentInstantiationRef().getInstantiation())) {
								mark = false;
							}
						}
					}
					// if it's already there disable this feature
					if (obj instanceof UsesPortStub) {
						for (Port p : externalPortList) {
							if (((UsesPortStub) obj).getName().equals(p.getUsesIdentifier())
								&& ((UsesPortStub) obj).eContainer().equals(p.getComponentInstantiationRef().getInstantiation())) {
								mark = false;
							}
						}
					}
				}
				// add the mark feature
				if (mark) {
					retList.add(new MarkExternalPortFeature(this));
				} else {
					retList.add(new MarkNonExternalPortFeature(this));
				}
			}
		}

		return retList.toArray(new ICustomFeature[retList.size()]);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		if (context.getPictogramElement() instanceof Diagram) {
			return new GraphitiWaveformDiagramUpdateFeature(this);
		} else if (context.getPictogramElement() instanceof Connection) {
			return new SADConnectionInterfaceUpdateFeature(this);
		}

		return super.getUpdateFeature(context);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		// Don't show delete for the waveform explorer
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		if (DUtil.isDiagramExplorer(diagram)) {
			return null;
		}

		// Check for shapes for which we don't want the user to have the delete capability,
		// including the diagram as a whole
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram || pe instanceof FixPointAnchor) {
			return null;
		}

		// If the element to be deleted is a connection, return the proper feature
		if (pe instanceof Connection) {
			return new SADConnectionInterfaceDeleteFeature(this);
		}

		// If the element is in the Chalkboard, it's removal will be handled by the Release and Terminate features
		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			if (pe instanceof ComponentShape) {
				return new ComponentReleaseFeature(this);
			}
			return null;
		}

		// Use parent class logic, but disable the result if read-only
		IDeleteFeature deleteFeature = super.getDeleteFeature(context);
		if (deleteFeature != null && DUtil.isDiagramReadOnly(diagram)) {
			deleteFeature = new DisabledDeleteFeatureWrapper(deleteFeature);
		}
		return deleteFeature;
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new FastRemoveFeature(this) {
			// overriding the method below causes Remove to NOT show up in context menus but still allows
			// us to getRemoveFeature and execute it.
			public boolean isAvailable(IContext context) {
				return false;
			}
		};
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {

		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}

		return super.getLayoutFeature(context);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			// We don't currently support reconnect actions for runtime
			return null;
		}

		Object businessObject = getBusinessObjectForPictogramElement(context.getOldAnchor());
		if (businessObject instanceof UsesPortStub || businessObject instanceof ProvidesPortStub || businessObject instanceof ComponentSupportedInterfaceStub) {
			return new SADReconnectFeature(this);
		}
		return super.getReconnectionFeature(context);
	}
}
