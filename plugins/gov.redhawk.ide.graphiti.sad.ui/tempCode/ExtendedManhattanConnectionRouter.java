package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class ExtendedManhattanConnectionRouter extends BaseManhattanConnectionRouter {

	public ExtendedManhattanConnectionRouter(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	protected List<ContainerShape> findAllShapes() {
		allShapes = new ArrayList<ContainerShape>();
		Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
		TreeIterator<EObject> iter = diagram.eAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			if (o instanceof ContainerShape) {
				// this is a potential collision shape
				ContainerShape shape = (ContainerShape)o;
//				BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(shape, BPMNShape.class);
//				if (bpmnShape==null)
//					continue;
//				if (shape==source || shape==target)
//					continue;
				// ignore containers (like Lane, SubProcess, etc.) if the source
				// or target shapes are children of the container's hierarchy
				if (shape==source.eContainer() || shape==target.eContainer())
					continue;
				
				// ignore some containers altogether
//				BaseElement be = bpmnShape.getBpmnElement();
//				if (be instanceof Lane)
//					continue;
				// TODO: other criteria here?
	
				allShapes.add(shape);
			}
		}
		GraphicsUtil.dump("All Shapes", allShapes);
		return allShapes;
	}
}
