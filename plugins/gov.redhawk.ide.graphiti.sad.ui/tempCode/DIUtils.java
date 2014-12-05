/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.di;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILayoutService;

public class DIUtils {

	public static void updateDIShape(PictogramElement element) {
		BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(element, BPMNShape.class);
		if (bpmnShape == null) {
			return;
		}

		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram((Shape) element);
		Bounds bounds = bpmnShape.getBounds();

		bounds.setX(loc.getX());
		bounds.setY(loc.getY());

		GraphicsAlgorithm graphicsAlgorithm = element.getGraphicsAlgorithm();
		IDimension size = Graphiti.getGaService().calculateSize(graphicsAlgorithm);
		bounds.setHeight(size.getHeight());
		bounds.setWidth(size.getWidth());

		if (element instanceof ContainerShape) {
			EList<Shape> children = ((ContainerShape) element).getChildren();
			for (Shape shape : children) {
				if (shape instanceof ContainerShape) {
					updateDIShape(shape);
				}
			}
		}

		updateConnections(element);
	}
	
	public static void updateConnections(PictogramElement element) {
		if (element instanceof Shape) {
			EList<Anchor> anchors = ((Shape) element).getAnchors();
			
			for (Anchor anchor : anchors) {
				List<Connection> connections = Graphiti.getPeService().getAllConnections(anchor);
				for (Connection connection : connections){
					updateDIEdge(connection);
				}
				connections.size();
			}
			
			anchors.size();
		}
	}

	public static void updateDIEdge(Connection connection) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		EObject be = BusinessObjectUtil.getFirstElementOfType(connection, BaseElement.class);
		BPMNEdge edge = DIUtils.findBPMNEdge(be);
		if (edge!=null) {
			Point point = DcFactory.eINSTANCE.createPoint();

			List<Point> waypoint = edge.getWaypoint();
			waypoint.clear();

			ILocation loc;
			loc = layoutService.getLocationRelativeToDiagram(connection.getStart());
			point.setX(loc.getX());
			point.setY(loc.getY());
			waypoint.add(point);

			if (connection instanceof FreeFormConnection) {
				FreeFormConnection freeForm = (FreeFormConnection) connection;
				EList<org.eclipse.graphiti.mm.algorithms.styles.Point> bendpoints = freeForm.getBendpoints();
				for (org.eclipse.graphiti.mm.algorithms.styles.Point bp : bendpoints) {
					point = DcFactory.eINSTANCE.createPoint();
					point.setX(bp.getX());
					point.setY(bp.getY());
					waypoint.add(point);
				}
			}

			point = DcFactory.eINSTANCE.createPoint();
			loc = layoutService.getLocationRelativeToDiagram(connection.getEnd());
			point.setX(loc.getX());
			point.setY(loc.getY());
			waypoint.add(point);
		}
	}

	static void addBendPoint(FreeFormConnection freeForm, Point point) {
		freeForm.getBendpoints().add(Graphiti.getGaService().createPoint((int) point.getX(), (int) point.getY()));
	}

	public static BPMNShape createDIShape(Shape shape, BaseElement elem, int x, int y, int w, int h,
			IFeatureProvider fp, Diagram diagram) {

		EList<EObject> businessObjects = Graphiti.getLinkService().getLinkForPictogramElement(diagram)
				.getBusinessObjects();
		BPMNShape bpmnShape = null;

		for (EObject eObject : businessObjects) {
			if (eObject instanceof BPMNDiagram) {
				BPMNDiagram bpmnDiagram = (BPMNDiagram) eObject;

				bpmnShape = BpmnDiFactory.eINSTANCE.createBPMNShape();
				bpmnShape.setBpmnElement(elem);
				Bounds bounds = DcFactory.eINSTANCE.createBounds();
				bounds.setX(x);
				bounds.setY(y);
				bounds.setWidth(w);
				bounds.setHeight(h);
				bpmnShape.setBounds(bounds);

				Bpmn2Preferences.getInstance(bpmnDiagram.eResource()).applyBPMNDIDefaults(bpmnShape, null);

				addShape(bpmnShape,bpmnDiagram);
				ModelUtil.setID(bpmnShape);

				fp.link(shape, new Object[] { elem, bpmnShape });
				break;
			}
		}

		return bpmnShape;
	}

	public static void addShape(DiagramElement elem, BPMNDiagram bpmnDiagram) {
		List<DiagramElement> elements = bpmnDiagram.getPlane().getPlaneElement();
		elements.add(elem);
	}
	
	public static DiagramElement findDiagramElement(List<BPMNDiagram> diagrams, BaseElement bpmnElement) {
		for (BPMNDiagram d : diagrams) {
			BPMNPlane plane = d.getPlane();
			List<DiagramElement> planeElements = plane.getPlaneElement();
			return findPlaneElement(planeElements, bpmnElement);
		}
		return null;
	}

	public static DiagramElement findPlaneElement(List<DiagramElement> planeElements, BaseElement bpmnElement) {
		for (DiagramElement de : planeElements) {
			if (de instanceof BPMNShape) {
				if (bpmnElement == ((BPMNShape)de).getBpmnElement())
					return de;
			}
			if (de instanceof BPMNEdge) {
				if (bpmnElement == ((BPMNEdge)de).getBpmnElement())
					return de;
			}
			else if (de instanceof BPMNPlane) {
				return findPlaneElement(((BPMNPlane)de).getPlaneElement(), bpmnElement);
			}
		}
		return null;
	}

	/**
	 * Return the Graphiti Diagram for the given BPMNDiagram. If one does not exist, create it.
	 * 
	 * @param editor
	 * @param bpmnDiagram
	 * @return
	 */
	public static Diagram getOrCreateDiagram(final IDiagramBehavior editor, final BPMNDiagram bpmnDiagram) {
		// do we need to create a new Diagram or is this already in the model?
		Diagram diagram = findDiagram(editor, bpmnDiagram);
		if (diagram!=null) {
			// already exists
			return diagram;
		}

		// create a new one
		IDiagramTypeProvider dtp = editor.getDiagramContainer().getDiagramTypeProvider();
		String typeId = dtp.getDiagram().getDiagramTypeId();
		final Diagram newDiagram = Graphiti.getCreateService().createDiagram(typeId, bpmnDiagram.getName(), true);
		final IFeatureProvider featureProvider = dtp.getFeatureProvider();
		final Resource resource = dtp.getDiagram().eResource();
		TransactionalEditingDomain domain = editor.getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			protected void doExecute() {
				resource.getContents().add(newDiagram);
				newDiagram.setActive(true);
				featureProvider.link(newDiagram, bpmnDiagram);
			}
		});
		return newDiagram;
	}
	
	/**
	 * Find the Graphiti Diagram that corresponds to the given BPMNDiagram object.
	 * 
	 * @param editor
	 * @param bpmnDiagram
	 * @return
	 */
	public static Diagram findDiagram(final IDiagramBehavior editor, final BPMNDiagram bpmnDiagram) {
		ResourceSet resourceSet = editor.getEditingDomain().getResourceSet();
		if (resourceSet!=null) {
			return findDiagram(resourceSet, bpmnDiagram);
		}
		return null;
	}
	
	public static Diagram findDiagram(ResourceSet resourceSet, final BPMNDiagram bpmnDiagram) {
		if (resourceSet!=null) {
			for (Resource r : resourceSet.getResources()) {
				for (EObject o : r.getContents()) {
					if (o instanceof Diagram) {
						Diagram diagram = (Diagram)o;
						if (BusinessObjectUtil.getFirstElementOfType(diagram, BPMNDiagram.class) == bpmnDiagram) {
							return diagram;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static void deleteDiagram(final IDiagramBehavior editor, final BPMNDiagram bpmnDiagram) {
		Diagram diagram = DIUtils.findDiagram(editor, bpmnDiagram);
		if (diagram!=null) {
			List<EObject> list = new ArrayList<EObject>();
			TreeIterator<EObject> iter = diagram.eAllContents();
			while (iter.hasNext()) {
				EObject o = iter.next();
				if (o instanceof PictogramLink) {
					((PictogramLink)o).getBusinessObjects().clear();
					if (!list.contains(o))
						list.add(o);
				}
				else if (o instanceof Color) {
					if (!list.contains(o))
						list.add(o);
				}
				else if (o instanceof Font) {
					if (!list.contains(o))
						list.add(o);
				}
				else if (o instanceof Style) {
					if (!list.contains(o))
						list.add(o);
				}
			}
			for (EObject o : list)
				EcoreUtil.delete(o);
			
			EcoreUtil.delete(diagram);
			EcoreUtil.delete(bpmnDiagram);
		}	
	}
	
	/**
	 * Find the BPMNDiagram in the editor's Resource Set that corresponds to the given BaseElement.
	 * The BaseElement is expected be some kind of container class such as a Process or SubProcess.
	 * 
	 * @param editor
	 * @param baseElement
	 * @return
	 */
	public static BPMNDiagram findBPMNDiagram(final BaseElement baseElement) {
		return findBPMNDiagram(baseElement, false);
	}
	
	/**
	 * Find the BPMNDiagram in the editor's Resource Set that references the given BaseElement.
	 * 
	 * If the parameter "contains" is TRUE, then the BaseElement's ancestor hierarchy is searched recursively.
	 * 
	 * The BaseElement may be either a container (i.e. Process, SubProcess, Participant, etc.) or
	 * a simple shape (Task, Gateway, etc.)
	 * 
	 * @param editor
	 * @param baseElement
	 * @param contains
	 * @return
	 */
	public static BPMNDiagram findBPMNDiagram(final BaseElement baseElement, boolean contains) {
		if (baseElement==null || baseElement.eResource()==null)
			return null;
		ResourceSet resourceSet = baseElement.eResource().getResourceSet();
		if (resourceSet==null)
			return null;
		for (Resource r : resourceSet.getResources()) {
			if (r instanceof Bpmn2Resource) {
				for (EObject o : r.getContents()) {
					if (o instanceof DocumentRoot) {
						DocumentRoot root = (DocumentRoot)o;
						Definitions defs = root.getDefinitions();
						BaseElement bpmnElement;
						for (BPMNDiagram d : defs.getDiagrams()) {
							BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
							bpmnElement = bpmnDiagram.getPlane().getBpmnElement();
							if (bpmnElement == baseElement)
								return bpmnDiagram;
						}
						if (contains) {
							for (BPMNDiagram d : defs.getDiagrams()) {
								BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
								for (DiagramElement de : bpmnDiagram.getPlane().getPlaneElement()) {
									if (de instanceof BPMNShape)
										bpmnElement = ((BPMNShape)de).getBpmnElement();
									else if (de instanceof BPMNEdge)
										bpmnElement = ((BPMNEdge)de).getBpmnElement();
									else
										continue;
									if (bpmnElement == baseElement)
										return bpmnDiagram;
								}
							}
							EObject parent = baseElement.eContainer();
							if (parent instanceof BaseElement && !(parent instanceof Definitions)) {
								BPMNDiagram bpmnDiagram = findBPMNDiagram((BaseElement)parent, true);
								if (bpmnDiagram!=null)
									return bpmnDiagram;
							}
						}
//						for (BPMNDiagram d : defs.getDiagrams()) {
//							BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
//							bpmnElement = bpmnDiagram.getPlane().getBpmnElement();
//							if (bpmnElement instanceof Collaboration) {
//								Collaboration collaboration = (Collaboration)bpmnElement;
//								for (Participant p : collaboration.getParticipants()) {
//									if (baseElement==p)
//										return bpmnDiagram;
//									if (baseElement==p.getProcessRef())
//										return bpmnDiagram;
//								}
//							}
//						}
					}
				}
			}
		}
		return null;
	}
	
	public static BPMNDiagram createBPMNDiagram(Definitions definitions, BaseElement container) {
		
		Resource resource = definitions.eResource();
        BPMNDiagram bpmnDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
		ModelUtil.setID(bpmnDiagram, resource);
        bpmnDiagram.setName(ModelUtil.getDisplayName(container));

		BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
		ModelUtil.setID(plane, resource);
		plane.setBpmnElement(container);
		
		bpmnDiagram.setPlane(plane);
		
		// this has to happen last because the IResourceChangeListener in the DesignEditor
		// looks for add/remove to Definitions.diagrams
        definitions.getDiagrams().add(bpmnDiagram);

		return bpmnDiagram;
	}
	
	/**
	 * 
	 * @param baseElement
	 * @return
	 */
	public static BPMNShape findBPMNShape(BaseElement baseElement) {
		Definitions definitions = ModelUtil.getDefinitions(baseElement);
		for (BPMNDiagram d : definitions.getDiagrams()) {
			BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
			BaseElement bpmnElement = null;
			for (DiagramElement de : bpmnDiagram.getPlane().getPlaneElement()) {
				if (de instanceof BPMNShape) {
					bpmnElement = ((BPMNShape)de).getBpmnElement();
					if (bpmnElement == baseElement)
						return (BPMNShape)de;
				}
			}
		}
		return null;
	}
	
	public static BPMNEdge findBPMNEdge(EObject baseElement) {
		Definitions definitions = ModelUtil.getDefinitions(baseElement);
		if (definitions!=null) {
			for (BPMNDiagram d : definitions.getDiagrams()) {
				BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
				BaseElement bpmnElement = null;
				for (DiagramElement de : bpmnDiagram.getPlane().getPlaneElement()) {
					if (de instanceof BPMNEdge) {
						bpmnElement = ((BPMNEdge)de).getBpmnElement();
						if (bpmnElement == baseElement)
							return (BPMNEdge)de;
					}
				}
			}
		}
		return null;
	}
	
	public static DiagramElement findDiagramElement(EObject object) {
		Definitions definitions = ModelUtil.getDefinitions(object);
		for (BPMNDiagram d : definitions.getDiagrams()) {
			BPMNDiagram bpmnDiagram = (BPMNDiagram)d;
			BaseElement bpmnElement = null;
			for (DiagramElement de : bpmnDiagram.getPlane().getPlaneElement()) {
				EStructuralFeature f = de.eClass().getEStructuralFeature("bpmnElement"); //$NON-NLS-1$
				if (f!=null) {
					bpmnElement = (BaseElement) de.eGet(f);
					if (bpmnElement == object)
						return de;
				}
			}
		}
		return null;
	}
	

	/**
	 * Returns the BPMNDiagram element that owns the given DiagramElement.
	 * 
	 * @param de
	 * @return
	 */
	public static BPMNDiagram getBPMNDiagram(DiagramElement de) {
		EObject container = de.eContainer();
		while (!(container instanceof Definitions)) {
			if (container instanceof BPMNDiagram)
				return (BPMNDiagram)container;
			container = container.eContainer();
		}
		return null;
	}
	
	/**
	 * Returns a list of all PictogramElements that reference the given BaseElement in all Graphiti Diagrams
	 * contained in all Resources of the given ResourceSet
	 * 
	 * @param resourceSet
	 * @param baseElement
	 * @return
	 */
	public static List<PictogramElement> getPictogramElements(ResourceSet resourceSet, BaseElement baseElement) {
		List<PictogramElement> elements = new ArrayList<PictogramElement>();
		for (Resource r : resourceSet.getResources()) {
			for (EObject o : r.getContents()) {
				if (o instanceof Diagram) {
					Diagram diagram = (Diagram)o;
					elements.addAll( Graphiti.getLinkService().getPictogramElements(diagram, baseElement) );
				}
			}
		}
		return elements;
	}
	
	/**
	 * Convenience method to return only the Graphiti ContainerShapes that reference the given BaseElement
	 * in all Diagrams of the given ResourceSet
	 * 
	 * @param resourceSet
	 * @param baseElement
	 * @return
	 */
	public static List<ContainerShape> getContainerShapes(ResourceSet resourceSet, BaseElement baseElement) {
		List<ContainerShape> shapes = new ArrayList<ContainerShape>();
		List<PictogramElement> pes = DIUtils.getPictogramElements(resourceSet, baseElement);
		for (PictogramElement pe : pes) {
			if (pe instanceof ContainerShape) {
				if (BusinessObjectUtil.getFirstElementOfType(pe, BPMNShape.class) != null)
					shapes.add((ContainerShape)pe);
			}
		}
		return shapes;
	}
	
	/**
	 * Convenience method to return only the Graphiti Connections that reference the given BaseElement
	 * in all Diagrams of the given ResourceSet
	 * 
	 * @param resourceSet
	 * @param baseElement
	 * @return
	 */
	public static List<Connection> getConnections(ResourceSet resourceSet, BaseElement baseElement) {
		List<Connection> connections = new ArrayList<Connection>();
		List<PictogramElement> pes = DIUtils.getPictogramElements(resourceSet, baseElement);
		for (PictogramElement pe : pes) {
			if (pe instanceof Connection) {
				if (BusinessObjectUtil.getFirstElementOfType(pe, BPMNEdge.class) != null)
					connections.add((Connection)pe);
			}
		}
		return connections;
	}
}