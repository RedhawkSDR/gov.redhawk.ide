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
package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.DecrementStartOrderFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.IncrementStartOrderFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.MarkExternalPortFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.MarkNonExternalPortFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.ReleaseComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.SetAsAssemblyControllerFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.ShowConsoleFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.TerminateComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.runtime.StartComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.runtime.StopComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.delete.SADConnectionInterfaceDeleteFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.reconnect.SADReconnectFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.update.RHDiagramUpdateFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.update.SADConnectionInterfaceUpdateFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
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
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.UpdateNoBoFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.pattern.DirectEditingFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class SADDiagramFeatureProvider extends DefaultFeatureProviderWithPatterns {

	public SADDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// Add Patterns for Domain Objects
		addPattern(new ComponentPattern());
		addConnectionPattern(new SADConnectInterfacePattern());
		addPattern(new HostCollocationPattern());
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());
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
		ICustomFeature[] ret = super.getCustomFeatures(context);
		List<ICustomFeature> retList = new ArrayList<ICustomFeature>();
		for (int i = 0; i < ret.length; i++) {
			retList.add(ret[i]);
		}

		// add zest layout feature if diagram selected
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0 && context.getPictogramElements()[0] instanceof Diagram) {
			retList.add(new LayoutDiagramFeature(this.getDiagramTypeProvider().getFeatureProvider()));
		}

		// add findBy edit feature if findByStub selected
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof FindByStub) {
				retList.add(new FindByEditFeature(this));
			}
		}

		// add runtime features, start/stop component only work in sandbox and targetSDR, not design time
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (context.getPictogramElements()[0] instanceof ContainerShape) {
				Diagram diagram = DUtil.findDiagram((ContainerShape) context.getPictogramElements()[0]);
				if (obj instanceof SadComponentInstantiation && (DUtil.isDiagramLocal(diagram) || DUtil.isDiagramTargetSdr(diagram))) {
					retList.add(new ReleaseComponentFeature(this));
					retList.add(new ShowConsoleFeature(this));
					retList.add(new StartComponentFeature(this));
					retList.add(new StopComponentFeature(this));
					retList.add(new TerminateComponentFeature(this));
				}
			}
		}

		// add external port menu item if we clicked on a port
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);

			// make sure business object is port stub
			if (obj instanceof ProvidesPortStub || obj instanceof UsesPortStub) {

				boolean mark = true;

				// get sad from diagram
				final SoftwareAssembly sad = DUtil.getDiagramSAD(this.getDiagramTypeProvider().getFeatureProvider(), this.getDiagramTypeProvider().getDiagram());

				if (sad.getExternalPorts() != null) {

					// get external ports
					EList<Port> externalPortList = sad.getExternalPorts().getPort();

					// if it's already there disable this feature
					if (obj instanceof ProvidesPortStub) {
						for (Port p : externalPortList) {
							if (p.getProvidesIndentifier().equals(((ProvidesPortStub) obj).getName())) {
								mark = false;
							}
						}
					}
					// if it's already there disable this feature
					if (obj instanceof UsesPortStub) {
						for (Port p : externalPortList) {
							if (p.getUsesIdentifier().equals(((UsesPortStub) obj).getName())) {
								mark = false;
							}
						}
					}
				}
				// add the mark feature
				if (mark) {
					retList.add(new MarkExternalPortFeature(this.getDiagramTypeProvider().getFeatureProvider()));
				} else {
					retList.add(new MarkNonExternalPortFeature(this.getDiagramTypeProvider().getFeatureProvider()));
				}
			}
		}

		// add Set As Assembly Controller menu item
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof SadComponentInstantiation && !DUtil.isDiagramLocal(DUtil.findDiagram((ContainerShape) context.getPictogramElements()[0]))) {
				retList.add(new SetAsAssemblyControllerFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}

		// add Increment Start Order menu item
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof SadComponentInstantiation && !DUtil.isDiagramLocal(DUtil.findDiagram((ContainerShape) context.getPictogramElements()[0]))) {
				retList.add(new IncrementStartOrderFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}

		// add Decrement Start Order menu item
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof SadComponentInstantiation && !DUtil.isDiagramLocal(DUtil.findDiagram((ContainerShape) context.getPictogramElements()[0]))) {
				retList.add(new DecrementStartOrderFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}

		ret = retList.toArray(ret);
		return ret;
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		// Search for shapes that we don't want the user to have move capability
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER })) {
			return new DefaultMoveShapeFeature(this) {
				public boolean canMove(IContext context) {
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
			return new RHDiagramUpdateFeature(this);
		} else if (context.getPictogramElement() instanceof Connection) {
			return new SADConnectionInterfaceUpdateFeature(this);
		}

		// hide update icon for some pictogram elements
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
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
		// Search for shapes for which we don't want the user to have the delete capability,
		// including the diagram as a whole
		if (context.getPictogramElement() instanceof Diagram
			|| context.getPictogramElement() instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE,
				RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
			return new DefaultDeleteFeature(this) {
				@Override
				public boolean canDelete(IDeleteContext context) {
					return false;
				}

				@Override
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}

		// If the element to be deleted is a connection, return the proper feature
		if (context.getPictogramElement() instanceof Connection) {
			return new SADConnectionInterfaceDeleteFeature(this);
		}

		// If the element is in the Chalkboard, it's removal will be handled by the Release and Terminate features
		if (DUtil.isDiagramLocal(getDiagramTypeProvider().getDiagram())) {
			return null;
		}

		return super.getDeleteFeature(context);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new DefaultRemoveFeature(this) {
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
			RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER })) {
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
		// Call the SADReconnectFeature if the original anchor is a uses or provides port
		Object businessObject = getBusinessObjectForPictogramElement(context.getOldAnchor());
		if (businessObject instanceof UsesPortStub || businessObject instanceof ProvidesPortStub) {
			return new SADReconnectFeature(this);
		}
		return super.getReconnectionFeature(context);
	}
}
