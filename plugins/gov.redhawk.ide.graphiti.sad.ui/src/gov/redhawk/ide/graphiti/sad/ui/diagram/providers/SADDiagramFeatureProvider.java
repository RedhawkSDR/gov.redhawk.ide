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
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.UpdateNoBoFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DirectEditingFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.DecrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.IncrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkNonExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.SetAsAssemblyControllerFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.UsesDeviceEditFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.UsesFrontEndDeviceEditFeature;
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
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.CollapseAllShapesFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.CollapseShapeFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.DisabledDeleteFeatureWrapper;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ExpandAllShapesFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ExpandShapeFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ShowConsoleFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StartFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.StopFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.TerminateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.remove.FastRemoveFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiFeatureProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
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

	// the text we double click is nested inside of the pictogram element that links to our business object
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}
		IDirectEditingFeature ret = null;
		for (IPattern pattern : this.getPatterns()) {
			if (checkPattern(pattern, getBusinessObjectForPictogramElement(DUtil.findContainerShapeParentWithProperty(context.getPictogramElement(),
				RHContainerShapeImpl.SHAPE_OUTER_CONTAINER)))) {
				IPattern chosenPattern = null;
				IDirectEditingFeature f = new DirectEditingFeatureForPattern(this, pattern);
				if (checkFeatureAndContext(f, context)) {
					if (ret == null) {
						ret = f;
						chosenPattern = pattern;
					} else {
						traceWarning("getDirectEditingFeature", pattern, chosenPattern); //$NON-NLS-1$
					}
				}
			}
		}

		if (ret == null) {
			ret = getDirectEditingFeatureAdditional(context);
		}

		return ret;
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

		if (pes[0] instanceof Diagram) {
			// Diagram features
			retList.add(new LayoutDiagramFeature(this));
			retList.add(new ExpandAllShapesFeature(this));
			retList.add(new CollapseAllShapesFeature(this));
		} else if (pes[0] instanceof RHContainerShape) {
			// Our standard shape features
			retList.add(new ExpandShapeFeature(this));
			retList.add(new CollapseShapeFeature(this));

			if (businessObject instanceof FindByStub) {
				// findby features
				retList.add(new FindByEditFeature(this));
			} else if (businessObject instanceof UsesDeviceStub) {
				// usesdevice features
				if (UsesDeviceFrontEndTunerPattern.isFrontEndDevice(((UsesDeviceStub) businessObject).getUsesDevice())) {
					retList.add(new UsesFrontEndDeviceEditFeature(this));
				} else {
					retList.add(new UsesDeviceEditFeature(this));
				}
			} else if (businessObject instanceof SadComponentInstantiation) {
				// Component features
				if (DUtil.isDiagramRuntime(diagram)) {
					// Runtime-only component features
					retList.add(new StartFeature(this));
					retList.add(new StopFeature(this));
					retList.add(new ShowConsoleFeature(this));
					retList.add(new LogLevelFeature(this));

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
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		// Search for shapes that we don't want the user to have move capability
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER,
			RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE })) {
			return new DefaultMoveShapeFeature(this) {
				@Override
				public boolean canMoveShape(IMoveShapeContext context) {
					return false;
				}
			};
		}

		return super.getMoveShapeFeature(context);
	}

	// NOTE - create features are handled directly by patterns
	@Override
	public ICreateFeature[] getCreateFeatures() {
		return super.getCreateFeatures();
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		if (context.getPictogramElement() instanceof Diagram) {
			return new GraphitiWaveformDiagramUpdateFeature(this);
		} else if (context.getPictogramElement() instanceof Connection) {
			return new SADConnectionInterfaceUpdateFeature(this);
		}

		// hide update icon for some pictogram elements
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE,
			RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE})) {
			return new UpdateNoBoFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
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

		// Search for shapes for which we don't want the user to have the delete capability,
		// including the diagram as a whole
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram || pe instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(context,
				new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER,
					RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER,
					RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
					RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE,
					RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
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
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		ICreateConnectionFeature[] connectionFeatures = getCreateConnectionFeatures();
		return connectionFeatures;
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		// Search for shapes that we don't want the user to have resize capability
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER,
			RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE})) {
			return new DefaultResizeShapeFeature(this) {
				public boolean canResizeShape(IResizeShapeContext context) {
					return false;
				}
			};
		}

		return super.getResizeShapeFeature(context);
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
