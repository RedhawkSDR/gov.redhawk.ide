package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import gov.redhawk.diagram.IDiagramUtilHelper;
import gov.redhawk.diagram.editor.URIEditorInputProxy;
import gov.redhawk.sca.efs.ScaFileSystemPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAreaContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.ui.IEditorInput;

public class DiagramUtil {

	//These are property key/value pairs that help us resize an existing shape by properly identifying graphicsAlgorithms
	public final static String GA_TYPE = "GAType";   //key for gA types
	public final static String GA_outerRoundedRectangle = "outerRoundedRectangle";
	public final static String GA_innerRoundedRectangle = "innerRoundedRectangle";
	public final static String GA_outerRoundedRectangleText = "outerRoundedRectangleText";
	public final static String GA_innerRoundedRectangleText = "innerRoundedRectangleText";
	public final static String GA_outerRoundedRectangleImage = "outerRoundedRectangleImage";
	public final static String GA_innerRoundedRectangleImage = "innerRoundedRectangleImage";
	public final static String GA_innerRoundedRectangleLine = "innerRoundedRectangleLine";
	public final static String GA_providesPortsRectangle = "providesPortsRectangle";
	public final static String GA_usesPortsRectangle = "usesPortsRectangle";
	public final static String GA_usesPortRectangle = "usesPortRectangle";
	
	//Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove etc.)
	public final static String SHAPE_TYPE = "ShapeType";   //key for Shape types
	public final static String SHAPE_usesPortsContainerShape = "usesPortsContainerShape";
	public final static String SHAPE_providesPortsContainerShape = "providesPortsContainerShape";
	public final static String SHAPE_usesPortContainerShape = "usesPortContainerShape";
	public final static String SHAPE_providesPortContainerShape = "providesPortContainerShape";
	public final static String SHAPE_usesPortRectangleShape = "usesPortRectangleShape";
	public final static String SHAPE_providesPortRectangleShape = "providesPortRectangleShape";
	public final static String SHAPE_interfaceContainerShape = "interfaceContainerShape";
	public final static String SHAPE_interfaceEllipseShape = "interfaceEllipseShape";
	public final static int OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING = 10;
	public final static int INNER_CONTAINER_SHAPE_TOP_PADDING = 20;
	public final static int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	public final static int INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING = 60;
	public final static int INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING+DiagramUtil.PROVIDES_PORTS_LEFT_PADDING;
	public final static int PORTS_CONTAINER_SHAPE_TOP_PADDING = 60;
	public final static int INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING = 8;
	public final static int INNER_ROUNDED_RECTANGLE_LINE_Y = 28;
	public final static int NAME_CHAR_WIDTH = 7;
	public final static int LABEL_CHAR_WIDTH = 7;
	public final static int PORT_NAME_HORIZONTAL_PADDING = 5;
	public final static int PORT_SHAPE_HEIGHT = 15;
	public final static int PORT_SHAPE_WIDTH = 15;
	public final static int PORT_CHAR_WIDTH = 7;
	public final static int PROVIDES_PORTS_LEFT_PADDING = 5;
	public final static int LOLLIPOP_CIRCLE_RADIUS = 10;
	public final static int INTERFACE_SHAPE_WIDTH = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING+PROVIDES_PORTS_LEFT_PADDING;
	public final static int INTERFACE_SHAPE_HEIGHT = 10;
	public final static int REQ_PADDING_BETWEEN_PORT_TYPES = 0;
	public final static int ICON_IMAGE_LENGTH = 16;
	
	/**
	 * Returns the SoftwareAssembly for the provided diagram
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static SoftwareAssembly getDiagramSAD(IFeatureProvider featureProvider, Diagram diagram){
		
		//NOTE to developer
		//You must use the same transactionalEditingDomain and associated resourceSet if you want save/undo/redo to work
		//properly.  The Graphiti editor will try saving the resourceSet and therefore we want our model to be in the same resourceSet.
		//The editingDomain below isn't associated with Graphiti model and so it doesn't save the model when the diagram editor saves.
		//TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		ResourceSet resourceSet = editingDomain.getResourceSet();
		
		URI uri = diagram.eResource().getURI();
		uri = uri.trimFragment().trimFileExtension().appendFileExtension("sad.xml");
		
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resourceSet.getResource(uri, true));
		
		return sad;
	}

	public static Polyline createArrow(GraphicsAlgorithmContainer gaContainer, IFeatureProvider featureProvider, Color color){
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(gaContainer, new int[] {-15,10,0,0,-15,-10 });
		polyline.setForeground(Graphiti.getGaService().manageColor(featureProvider.getDiagramTypeProvider().getDiagram(), IColorConstant.BLACK));
		polyline.setLineWidth(2);
		polyline.setForeground(color);
		return polyline;
	}
	
	
	/**
	 * Returns true if the provided context contains a pictogram element with one of the provided property values.
	 * False otherwise.
	 * @param context
	 * @param propertyKeys
	 * @return
	 */
	public static boolean doesPictogramContainProperty(PictogramElement pe, String[] propertyValues){
		if(pe != null && pe.getProperties() != null){
			for(Property p: pe.getProperties()){
				for(String propValue: propertyValues){
					if(p.getValue().equals(propValue)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the provided context contains a pictogram element with one of the provided property values.
	 * False otherwise.
	 * @param context
	 * @param propertyKeys
	 * @return
	 */
	public static boolean doesPictogramContainProperty(IPictogramElementContext context, String[] propertyValues){
		PictogramElement pe = context.getPictogramElement();
		return doesPictogramContainProperty(pe, propertyValues);
	}
	
	/**
	 * Creates a large rectangle intended to be an outside container and links the provided business object to it.
	 * @param targetContainerShape
	 * @param text
	 * @param businessObject
	 * @param featureProvider
	 * @return
	 */
	public static ContainerShape addOuterRectangle(ContainerShape targetContainerShape, String text, Object businessObject, 
			IFeatureProvider featureProvider, String imageId, Style containerStyle){
		ContainerShape outerContainerShape = Graphiti.getCreateService().createContainerShape(targetContainerShape, true);
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(outerContainerShape, 5, 5);
		outerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangle);
		//image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangleImage);//ref helps with resize
		//text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, text);
		cText.setStyle(StyleUtil.getStyleForOuterText(findDiagram(targetContainerShape)));
		Graphiti.getPeService().setPropertyValue(cText, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangleText);
		featureProvider.link(outerContainerShape, businessObject); // link container and business object
		
		return outerContainerShape;
	}
	
	/**
	 * Creates a large rectangle intended to be an inside container and links the provided business object to it.
	 * @param targetContainerShape
	 * @param text
	 * @param businessObject
	 * @param featureProvider
	 * @return
	 */
	public static ContainerShape addInnerRectangle(Diagram diagram, ContainerShape outerContainerShape, String text, 
			IFeatureProvider featureProvider, String imageId, Style containerStyle){
		ContainerShape innerContainerShape = Graphiti.getCreateService().createContainerShape(outerContainerShape, false);
		RoundedRectangle innerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(innerContainerShape, 5, 5);
		innerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(innerRoundedRectangle, DiagramUtil.GA_TYPE, DiagramUtil.GA_innerRoundedRectangle);//ref helps with resize
		//image
		Image imgIcon = Graphiti.getGaCreateService().createImage(innerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DiagramUtil.GA_TYPE, DiagramUtil.GA_innerRoundedRectangleImage);//ref helps with resize
		//text
		Text ciText = Graphiti.getCreateService().createText(innerRoundedRectangle, text);
		ciText.setStyle(StyleUtil.getStyleForInnerText(diagram));
		Graphiti.getPeService().setPropertyValue(ciText, DiagramUtil.GA_TYPE, DiagramUtil.GA_innerRoundedRectangleText);//ref helps with resize
		//line
		Polyline polyline = Graphiti.getGaCreateService().createPolyline(innerRoundedRectangle, new int[] { 0, INNER_ROUNDED_RECTANGLE_LINE_Y, innerRoundedRectangle.getWidth(), INNER_ROUNDED_RECTANGLE_LINE_Y});
		polyline.setLineWidth(1);
		polyline.setBackground(Graphiti.getGaService().manageColor(diagram, StyleUtil.BLACK));
		polyline.setForeground(Graphiti.getGaService().manageColor(diagram, StyleUtil.BLACK));
		Graphiti.getPeService().setPropertyValue(polyline, DiagramUtil.GA_TYPE, DiagramUtil.GA_innerRoundedRectangleLine);//ref helps with resize
		
		
		return innerContainerShape;
	}
	
	/**
	 * Add lollipop to existing containerShape in the provided diagram.  Lollipop anchor will link to the provided business object.
	 * @param outerContainerShape
	 * @param diagram
	 * @param anchorBusinessObject
	 */
	public static void addLollipop(ContainerShape outerContainerShape, Diagram diagram, Object anchorBusinessObject, IFeatureProvider featureProvider){
		
		
		//interface container lollipop
		ContainerShape interfaceContainerShape  = Graphiti.getCreateService().createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(interfaceContainerShape, DiagramUtil.GA_TYPE, DiagramUtil.SHAPE_interfaceContainerShape);
		Rectangle interfaceRectangle = Graphiti.getCreateService().createRectangle(interfaceContainerShape);
		featureProvider.link(interfaceContainerShape, anchorBusinessObject);
		interfaceRectangle.setTransparency(.99d);
		Graphiti.getGaLayoutService().setLocationAndSize(interfaceRectangle, 0, 25, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);
		
		//interface lollipop circle
		Shape lollipopEllipseShape = Graphiti.getCreateService().createShape(interfaceContainerShape, true);
		Graphiti.getPeService().setPropertyValue(lollipopEllipseShape, DiagramUtil.GA_TYPE, DiagramUtil.SHAPE_interfaceEllipseShape);
		Ellipse lollipopEllipse = Graphiti.getCreateService().createEllipse(lollipopEllipseShape);
		lollipopEllipse.setStyle(StyleUtil.getStyleForLollipopCircle(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopEllipse, 0, 0, LOLLIPOP_CIRCLE_RADIUS, LOLLIPOP_CIRCLE_RADIUS);
		
		//interface lollipop line
		Shape lollipopLineShape  = Graphiti.getCreateService().createContainerShape(interfaceContainerShape, true);
		Rectangle lollipopLine = Graphiti.getCreateService().createRectangle(lollipopLineShape);
		lollipopLine.setStyle(StyleUtil.getStyleForLollipopLine(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopLine, LOLLIPOP_CIRCLE_RADIUS, LOLLIPOP_CIRCLE_RADIUS/2, INTERFACE_SHAPE_WIDTH - LOLLIPOP_CIRCLE_RADIUS, 1);

		//fix point anchor
		{
			FixPointAnchor fixPointAnchor = Graphiti.getPeCreateService().createFixPointAnchor(interfaceContainerShape);
			Point fixAnchorPoint = StylesFactory.eINSTANCE.createPoint();
			fixAnchorPoint.setX(0);
			fixAnchorPoint.setY(PORT_SHAPE_HEIGHT/2);
			fixPointAnchor.setLocation(fixAnchorPoint);
			fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
			fixPointAnchor.setReferencedGraphicsAlgorithm(interfaceRectangle);
			Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
			fixPointAnchorRectangle.setTransparency(.99d);
			Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, 0, -INTERFACE_SHAPE_HEIGHT/2, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);
		}
	}
	
	/**
	 * Adds provides port container to provided container shape.  Adds a port shape with name and anchor for each providesPortStub.
	 * @param outerContainerShape
	 * @param diagram
	 * @param name
	 * @param providesPortStubs
	 */
	public static void addProvidesPorts(ContainerShape outerContainerShape, Diagram diagram, EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider){
		
		//provides (input)
		int providesPortNameLength = getLongestProvidesPortWidth(providesPortStubs, diagram);
		int iter = 0;
		ContainerShape providesPortsContainerShape = Graphiti.getCreateService().createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, SHAPE_TYPE, SHAPE_providesPortsContainerShape);//ref prevent selection/deletion/removal
		Rectangle providesPortsRectangle = Graphiti.getCreateService().createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, GA_TYPE, GA_providesPortsRectangle);//ref helps with resize

		//iterate over all provides ports
		for(ProvidesPortStub p: providesPortStubs){
			ContainerShape providesPortContainerShape = Graphiti.getCreateService().createContainerShape(providesPortsContainerShape, true);
			Graphiti.getPeService().setPropertyValue(providesPortContainerShape, SHAPE_TYPE, SHAPE_providesPortContainerShape);//ref prevent selection/deletion/removal
			Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(providesPortContainerShape);
			providesPortContainerShapeRectangle.setTransparency(1d);
			Graphiti.getGaLayoutService().setLocationAndSize(providesPortContainerShapeRectangle, 0, iter++*(PORT_SHAPE_HEIGHT+5), PORT_SHAPE_WIDTH + providesPortNameLength, PORT_SHAPE_HEIGHT);
			featureProvider.link(providesPortContainerShape, p);

			//port shape
			ContainerShape providesPortRectangleShape  = Graphiti.getCreateService().createContainerShape(providesPortContainerShape, true);
			Graphiti.getPeService().setPropertyValue(providesPortRectangleShape, SHAPE_TYPE, SHAPE_providesPortRectangleShape);//ref prevent move
			Rectangle providesPortRectangle = Graphiti.getCreateService().createRectangle(providesPortRectangleShape);
			featureProvider.link(providesPortRectangleShape, p);
			providesPortRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
			Graphiti.getGaLayoutService().setLocationAndSize(providesPortRectangle, 0, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

			//port text
			Shape providesPortTextShape  = Graphiti.getCreateService().createShape(providesPortContainerShape, false);
			Text providesPortText = Graphiti.getCreateService().createText(providesPortTextShape, p.getName());
			providesPortText.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
			Graphiti.getGaLayoutService().setLocationAndSize(providesPortText, PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_HEIGHT, 0, providesPortNameLength*PORT_CHAR_WIDTH, 20);

			//fix point anchor
			FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(providesPortRectangleShape);
			Point point = StylesFactory.eINSTANCE.createPoint();
			point.setX(0);
			point.setY(PORT_SHAPE_HEIGHT/2);
			fixPointAnchor.setLocation(point);
			featureProvider.link(fixPointAnchor, p);
			fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
			fixPointAnchor.setReferencedGraphicsAlgorithm(providesPortRectangle);
			Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForProvidesPortAnchor(diagram));
			Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, 0, -PORT_SHAPE_HEIGHT/2, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

		}
	}
	
	/**
	 * Adds uses port container to provided container shape.  Adds a port shape with name and anchor for each usesPortStub.
	 * @param outerContainerShape
	 * @param diagram
	 * @param name
	 * @param providesPortStubs
	 */
	public static void addUsesPorts(ContainerShape outerContainerShape, Diagram diagram, EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider){
		
		//uses (output)
		int usesPortNameLength = getLongestUsesPortWidth(usesPortStubs, diagram);
		int intPortTextX = outerContainerShape.getGraphicsAlgorithm().getWidth() - (usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_WIDTH);

		ContainerShape usesPortsContainerShape = Graphiti.getPeService().createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, SHAPE_TYPE, SHAPE_usesPortsContainerShape); //ref prevent selection/deletion/removal
		Rectangle usesPortsRectangle = Graphiti.getCreateService().createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, GA_TYPE, GA_usesPortsRectangle);//ref helps with resize
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortsRectangle, intPortTextX, PORTS_CONTAINER_SHAPE_TOP_PADDING, PORT_SHAPE_WIDTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, usesPortStubs.size()*(PORT_SHAPE_HEIGHT));

		//iterate over all uses ports
		int iter = 0;
		for(UsesPortStub p: usesPortStubs){
			//port container
			ContainerShape usesPortContainerShape = Graphiti.getPeService().createContainerShape(usesPortsContainerShape, true);
			Graphiti.getPeService().setPropertyValue(usesPortContainerShape, SHAPE_TYPE, SHAPE_usesPortContainerShape); //ref prevent selection/deletion/removal
			Rectangle usesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(usesPortContainerShape);
			usesPortContainerShapeRectangle.setTransparency(1d);
			Graphiti.getGaLayoutService().setLocationAndSize(usesPortContainerShapeRectangle, usesPortsRectangle.getWidth()-(PORT_SHAPE_WIDTH + usesPortNameLength), iter++*(PORT_SHAPE_HEIGHT+5), PORT_SHAPE_WIDTH + usesPortNameLength, PORT_SHAPE_HEIGHT);
			featureProvider.link(usesPortContainerShape, p);
			
			
			//port shape
			ContainerShape usesPortRectangleShape  = Graphiti.getPeService().createContainerShape(usesPortContainerShape, true);
			Graphiti.getPeService().setPropertyValue(usesPortRectangleShape, SHAPE_TYPE, SHAPE_usesPortRectangleShape);//ref prevent move
			Rectangle usesPortRectangle = Graphiti.getCreateService().createRectangle(usesPortRectangleShape);
			Graphiti.getPeService().setPropertyValue(usesPortRectangle, GA_TYPE, GA_usesPortRectangle);//ref helps with resize
			featureProvider.link(usesPortRectangleShape, p);
			usesPortRectangle.setStyle(StyleUtil.getStyleForUsesPort(diagram));
			Graphiti.getGaLayoutService().setLocationAndSize(usesPortRectangle, usesPortNameLength, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);
			
			//port text
			Shape usesPortTextShape  = Graphiti.getPeService().createShape(usesPortContainerShape, false);
			Text usesPortText = Graphiti.getCreateService().createText(usesPortTextShape, p.getName());
			usesPortText.setStyle(StyleUtil.getStyleForUsesPort(diagram));
			usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
			Graphiti.getGaLayoutService().setLocationAndSize(usesPortText, 0, 0, usesPortContainerShapeRectangle.getWidth()-(usesPortRectangle.getWidth() + PORT_NAME_HORIZONTAL_PADDING), 20);

			//fix point anchor
			FixPointAnchor fixPointAnchor = Graphiti.getPeService().createFixPointAnchor(usesPortRectangleShape);
			Point point = StylesFactory.eINSTANCE.createPoint();
			point.setX(PORT_SHAPE_WIDTH);
			point.setY(PORT_SHAPE_HEIGHT/2);
			fixPointAnchor.setLocation(point);
			featureProvider.link(fixPointAnchor, p);
			fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
			fixPointAnchor.setReferencedGraphicsAlgorithm(usesPortRectangle);
			Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForUsesPortAnchor(diagram));
			Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, -PORT_SHAPE_WIDTH, -PORT_SHAPE_HEIGHT/2, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);
		}
	}
	
	
	/**
	 * Returns all of the children contained within the provided PropertyContainer and their children recursively.
	 * @param diagramElement
	 * @return
	 */
	public static List<PropertyContainer> collectChildren(PropertyContainer diagramElement){
		
		List<PropertyContainer> children = new ArrayList<PropertyContainer>();
		children.add(diagramElement);
		
		//if containershape, collect children recursively
		if(diagramElement instanceof ContainerShape){
			ContainerShape cs = (ContainerShape)diagramElement;
			for(Shape c: cs.getChildren()){
				children.addAll(collectChildren(c));
			}
			if(cs.getGraphicsAlgorithm() != null){
				children.addAll(collectChildren(cs.getGraphicsAlgorithm()));
			}
		//if containershape, collect children recursively
		}else if(diagramElement instanceof GraphicsAlgorithm){
			GraphicsAlgorithm ga = (GraphicsAlgorithm)diagramElement;
			for(GraphicsAlgorithm c: ga.getGraphicsAlgorithmChildren()){
				children.addAll(collectChildren(c));
			}
		}
		
		return children;
	}
	
	/**
	 * Determine the height by which we need to expand by comparing the number of uses and provides ports and return the largest
	 * @return int Return the length by which we need to expand the height of the associated Shape
	 */
	public static int getAdjustedHeight(final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs) {;

		int numPorts = Math.max(providesPortStubs.size(), usesPortStubs.size());
		
		return numPorts;
	}
	
	/**
	 * Returns minimum height for Shape containing provides and uses ports
	 * @param ci
	 * @return
	 */
	public static int getMinimumHeight(final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		return DiagramUtil.getAdjustedHeight(providesPortStubs, usesPortStubs) * DiagramUtil.PORT_SHAPE_HEIGHT + DiagramUtil.PORTS_CONTAINER_SHAPE_TOP_PADDING + 10;
	}
	

	
	/**
	 * Returns preferred height for component
	 * @param ci
	 * @return
	 */
	public static int getPreferredHeight(final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		return DiagramUtil.getAdjustedHeight(providesPortStubs, usesPortStubs) * DiagramUtil.PORT_SHAPE_HEIGHT + 100;
	}
	

	/**
	 * Returns list of Shapes that are contained in selected diagram context area
	 * @param diagram
	 * @param context
	 * @return
	 */
	public static List<Shape> getContainersInArea(final Diagram diagram, final IAreaContext context){ 
		
		List<Shape> retList = new ArrayList<Shape>();
		
		EList<Shape> shapes = diagram.getChildren();
		for(Shape s: shapes){
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			if(context.getX() <= ga.getX() && context.getWidth() >= ga.getWidth() &&
					context.getY() <= ga.getY() && context.getHeight() >= ga.getHeight()){
				retList.add(s);
			}
		}
		return retList;
	}
	
	/**
	 * Returns list of ContainerShape in provided AreaContext with 
	 * property key DiagramUtil.GA_TYPE and provided propertyValue
	 * @param diagram
	 * @param context
	 * @return
	 */
	public static List<Shape> getContainersInArea(final Diagram diagram, final IAreaContext context, String propertyValue){ 
		
		List<Shape> retList = new ArrayList<Shape>();
		
		EList<Shape> shapes = diagram.getChildren();
		for(Shape s: shapes){
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			if(gaExistInArea(ga, context) && DiagramUtil.isPropertyElementType(ga, propertyValue)){
				retList.add(s);
			}
		}
		return retList;
	}
	
	/**
	 * Return true if GraphicsAlgorithm exists within IAreaContext
	 * @param ga
	 * @param context
	 * @return
	 */
	public static boolean gaExistInArea(final GraphicsAlgorithm ga, final IAreaContext context){
		if(context.getX() <= ga.getX() && context.getWidth() >= ga.getWidth() &&
				context.getY() <= ga.getY() && context.getHeight() >= ga.getHeight()){
			return true;
		}
		return false;
	}
		
	
	
	
	/**
	 * Resizes component with desired size.  Minimums are enforced.  Ports are kept at sides while inner box grows
	 * @param context
	 * @param pe
	 */
	public static void resizeOuterContainerShape(IAreaContext context, PictogramElement pe, final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		
		RoundedRectangle outerRoundedRectangle = null;
		RoundedRectangle innerRoundedRectangle = null;
		Text innerRoundedRectangleText = null;
		Text outerRoundedRectangleText = null;
		Rectangle usesPortsRectangle = null;
		Rectangle providesPortsRectangle = null;
		Image innerRoundedRectangleImage = null;
		Image outerRoundedRectangleImage = null;
		Polyline innerRoundedRectangleLine = null;
		
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pe);
				
		//enforce minimum width/height
		int width = DiagramUtil.getMinimumWidth(outerTitle, innerTitle, providesPortStubs, usesPortStubs, Graphiti.getPeService().getDiagramForPictogramElement(pe));
		if(context.getWidth() > width){
			width = context.getWidth();
		}
		int height = getMinimumHeight(providesPortStubs, usesPortStubs);
		if(context.getHeight() > height){
			height = context.getHeight();
		}
		
		//find all of our diagram elements
		List<PropertyContainer> children = collectChildren(pe);
		for(PropertyContainer pc: children){
			if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_outerRoundedRectangle)){
				outerRoundedRectangle = (RoundedRectangle)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_innerRoundedRectangle)){
				innerRoundedRectangle = (RoundedRectangle)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_innerRoundedRectangleText)){
				innerRoundedRectangleText = (Text)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_outerRoundedRectangleText)){
				outerRoundedRectangleText = (Text)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_usesPortsRectangle)){
				usesPortsRectangle = (Rectangle)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_providesPortsRectangle)){
				providesPortsRectangle = (Rectangle)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_innerRoundedRectangleImage)){
				innerRoundedRectangleImage = (Image)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_outerRoundedRectangleImage)){
				outerRoundedRectangleImage = (Image)pc;
			}else if(DiagramUtil.isPropertyElementType(pc, DiagramUtil.GA_innerRoundedRectangleLine)){
				innerRoundedRectangleLine = (Polyline)pc;
			}
		}
		
		//resize all diagram elements
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		
		//outerRoundedRectangle
		gaLayoutService.setLocationAndSize(outerRoundedRectangle, context.getX(), context.getY(), width, height);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleText, DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING+ICON_IMAGE_LENGTH+4, 0, width-(DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING+ICON_IMAGE_LENGTH+4), 20);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleImage, DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		
		
		//innerRoundedRectangle
		int innerContainerShapeWidth = width-DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING*2 - DiagramUtil.PROVIDES_PORTS_LEFT_PADDING;
		int innerContainerShapeHeight = height-DiagramUtil.INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setLocationAndSize(innerRoundedRectangle, DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, DiagramUtil.INNER_CONTAINER_SHAPE_TOP_PADDING, innerContainerShapeWidth, innerContainerShapeHeight);
//		//left justified
//		gaLayoutService.setLocationAndSize(innerRoundedRectangleText, ICON_IMAGE_LENGTH+5, 0, innerRoundedRectangle.getWidth()-(ICON_IMAGE_LENGTH+5), 20);
//		gaLayoutService.setLocationAndSize(innerRoundedRectangleImage, 5, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		IDimension innerRoundedRectangleTextSize = GraphitiUi.getUiLayoutService().calculateTextSize(innerRoundedRectangleText.getValue(), StyleUtil.getInnerTitleFont(Graphiti.getPeService().getDiagramForPictogramElement(pe)));
		int xForImage = (innerRoundedRectangle.getWidth()-(innerRoundedRectangleTextSize.getWidth()+ICON_IMAGE_LENGTH+5))/2;
		gaLayoutService.setLocationAndSize(innerRoundedRectangleImage, xForImage, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		gaLayoutService.setLocationAndSize(innerRoundedRectangleText, xForImage+ICON_IMAGE_LENGTH+5, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, innerRoundedRectangleTextSize.getWidth()+10, innerRoundedRectangleTextSize.getHeight());
		innerRoundedRectangleLine.getPoints().get(1).setX(innerContainerShapeWidth);
		
		//providesPortsRectangle
		if(providesPortStubs != null && providesPortsRectangle != null){
			int providesPortNameLength = DiagramUtil.getLongestProvidesPortWidth(providesPortStubs, diagram);
			gaLayoutService.setLocationAndSize(providesPortsRectangle, DiagramUtil.PROVIDES_PORTS_LEFT_PADDING, DiagramUtil.PORTS_CONTAINER_SHAPE_TOP_PADDING, DiagramUtil.PORT_SHAPE_WIDTH + providesPortNameLength, providesPortStubs.size()*(DiagramUtil.PORT_SHAPE_HEIGHT));
		}
		
		//usesPortsRectangle
		if(usesPortStubs != null && usesPortsRectangle != null){
			int usesPortNameLength = DiagramUtil.getLongestUsesPortWidth(usesPortStubs, diagram); 
			int intPortTextX = outerRoundedRectangle.getWidth() - (usesPortNameLength + DiagramUtil.PORT_NAME_HORIZONTAL_PADDING + DiagramUtil.PORT_SHAPE_WIDTH);
			gaLayoutService.setLocationAndSize(usesPortsRectangle, intPortTextX, DiagramUtil.PORTS_CONTAINER_SHAPE_TOP_PADDING, DiagramUtil.PORT_SHAPE_WIDTH + usesPortNameLength + DiagramUtil.PORT_NAME_HORIZONTAL_PADDING, usesPortStubs.size()*(DiagramUtil.PORT_SHAPE_HEIGHT));
		}
	}
	
	//returns width required to support longest provides port name
	//4 used as minimum, characters cut off otherwise
	public static int getLongestProvidesPortWidth(final EList<ProvidesPortStub> providesPortStubs, Diagram diagram){
		String longest = "four";
		if(providesPortStubs != null){
			for (final ProvidesPortStub provides : providesPortStubs) {
				if (provides.getName().length() > longest.length()) {
					longest = provides.getName();
				}
			}
		}
		
		IDimension requiredWidth = GraphitiUi.getUiLayoutService().calculateTextSize(
				longest, StyleUtil.getPortFont(diagram));
	
		
		return requiredWidth.getWidth();
	}
	
	//returns width required to support longest uses port name
	//4 used as minimum, characters cut off otherwise
	public static int getLongestUsesPortWidth(final EList<UsesPortStub> usesPortsStubs, Diagram diagram){
		String longest = "four";
		if(usesPortsStubs != null){
			for (final UsesPortStub uses : usesPortsStubs) {
				if (uses.getName().length() > longest.length()) {
					longest = uses.getName();
				}
			}
		}
		
		IDimension requiredWidth = GraphitiUi.getUiLayoutService().calculateTextSize(
				longest, StyleUtil.getPortFont(diagram));
	
		
		return requiredWidth.getWidth() + 20;
	}
	
	/**
	 * Returns true if the property container contains a property key DiagramUtil.GA_TYPE and with propertyValue as value
	 * @param pc
	 * @param propertyValue
	 * @return
	 */
	public static boolean isPropertyElementType(PropertyContainer pc, String propertyValue){
		for(Property p: pc.getProperties()){
			if(GA_TYPE.equals(p.getKey()) && propertyValue.equals(p.getValue())){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns minimum width for Shape with provides and uses port stubs and name text
	 * @param ci
	 * @return
	 */
	public static int getMinimumWidth(final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs, Diagram diagram){
		
		int portsWidth = 0;
		int innerTitleWidth = 0;
		int outerTitleWidth = 0;
		
		int usesWidth = DiagramUtil.PORT_SHAPE_WIDTH + 
				getLongestUsesPortWidth(usesPortStubs, diagram) +
				DiagramUtil.PORT_NAME_HORIZONTAL_PADDING + 
				DiagramUtil.PORT_SHAPE_WIDTH;
		int providesWidth = DiagramUtil.PORT_SHAPE_WIDTH + getLongestProvidesPortWidth(providesPortStubs, diagram);
		
		portsWidth = usesWidth+providesWidth+REQ_PADDING_BETWEEN_PORT_TYPES;
		
		//inner title
		IDimension innerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(
				innerTitle, StyleUtil.getInnerTitleFont(diagram));
		innerTitleWidth = innerTitleDimension.getWidth() + INTERFACE_SHAPE_WIDTH + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING;
		
		//outer title
		IDimension outerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(
				outerTitle, StyleUtil.getOuterTitleFont(diagram));
		outerTitleWidth = INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + 
				outerTitleDimension.getWidth() + 
				INTERFACE_SHAPE_WIDTH + OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING;
		
		//return the largest
		int largestWidth = portsWidth;
		if(largestWidth < innerTitleWidth){
			largestWidth = innerTitleWidth;
		}
		if(largestWidth < outerTitleWidth){
			largestWidth = outerTitleWidth;
		}
		return largestWidth;
	}
	
	/**
	 * Return true if target is HostCollocation ContainerShape
	 * @param context
	 */
	public static HostCollocation getHostCollocation(final ContainerShape targetContainerShape){
		if(targetContainerShape instanceof ContainerShape){
			if(targetContainerShape.getLink() != null && targetContainerShape.getLink().getBusinessObjects() != null){
				for(EObject obj: targetContainerShape.getLink().getBusinessObjects()){
					if(obj instanceof HostCollocation){
						return (HostCollocation)obj;
					}
				}
			}
		}
		return null;
	}
	
	//convenient method for getting diagram for a ContainerShape
	public static Diagram findDiagram(ContainerShape containerShape){
		return Graphiti.getPeService().getDiagramForShape(containerShape);
	}
	
	//convenient method for getting business object for PictogramElement
	public static Object getBusinessObject(PictogramElement pe){
		return GraphitiUi.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
	}
	
	/**
	 * Delete SadComponentInstantiation and corresponding SadComponentPlacement business object from SoftwareAssembly
	 * This method should be executed within a RecordingCommand.
	 * @param ciToDelete
	 * @param diagram
	 */
	public static void deleteComponentInstantiation(final SadComponentInstantiation ciToDelete, final SoftwareAssembly sad){
		
		//assembly controller may reference componentInstantiation
		//delete reference if applicable
		if(sad.getAssemblyController() != null &&
				sad.getAssemblyController().getComponentInstantiationRef() != null &&
				sad.getAssemblyController().getComponentInstantiationRef().getInstantiation().equals(ciToDelete)){
			//TODO: how should this be handled? We need to test this out
			EcoreUtil.delete(sad.getAssemblyController().getComponentInstantiationRef());
			sad.getAssemblyController().setComponentInstantiationRef(null);
		}

		//get placement for instantiation and delete it from sad partitioning after we look at removing the component file ref.
		SadComponentPlacement placement = (SadComponentPlacement)ciToDelete.getPlacement();

		//find and remove any attached connections
		//gather connections
		List<SadConnectInterface> connectionsToRemove = new ArrayList<SadConnectInterface>();
		if(sad.getConnections() != null){
			for(SadConnectInterface connectionInterface: sad.getConnections().getConnectInterface()){
				//we need to do thorough null checks here because of the many connection possibilities.  Firstly a connection requires only a usesPort and either (providesPort || componentSupportedInterface) 
				//and therefore null checks need to be performed.
				//FindBy connections don't have ComponentInstantiationRefs and so they can also be null
				if((connectionInterface.getComponentSupportedInterface() != null && connectionInterface.getComponentSupportedInterface().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getComponentSupportedInterface().getComponentInstantiationRef().getRefid())) ||
						(connectionInterface.getUsesPort() != null && connectionInterface.getUsesPort().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getUsesPort().getComponentInstantiationRef().getRefid())) ||
						(connectionInterface.getProvidesPort() != null && connectionInterface.getProvidesPort().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getProvidesPort().getComponentInstantiationRef().getRefid()))){
					connectionsToRemove.add(connectionInterface);
				}
			}
		}
		//remove gathered connections
		if(sad.getConnections() != null){
			sad.getConnections().getConnectInterface().removeAll(connectionsToRemove);
		}

		//delete component file if applicable
		//figure out which component file we are using and if no other component placements using it then remove it.
		ComponentFile componentFileToRemove = placement.getComponentFileRef().getFile();
		for(SadComponentPlacement p: sad.getPartitioning().getComponentPlacement()){
			if(p != placement && p.getComponentFileRef().getRefid().equals(placement.getComponentFileRef().getRefid())){
				componentFileToRemove = null;
			}
		}
		if(componentFileToRemove != null){
			sad.getComponentFiles().getComponentFile().remove(componentFileToRemove);
		}

		//delete component placement
		sad.getPartitioning().getComponentPlacement().remove(placement);
	}
	
	public static URI getDiagramResourceURI(final IDiagramUtilHelper options, final Resource resource) throws IOException {
		if (resource != null) {
			final URI uri = resource.getURI();
			if (uri.isPlatformResource()) {
				final IFile file = options.getResource(resource);
				return DiagramUtil.getRelativeDiagramResourceURI(options, file);
			} else {
				return DiagramUtil.getTemporaryDiagramResourceURI(options, uri);
			}
		}
		return null;
	}
	
	/**
	 * Initialize sad diagram.
	 * 
	 * @param b
	 */
	private static URI getRelativeDiagramResourceURI(final IDiagramUtilHelper options, final IFile file) {
		final IFile diagramFile = file.getParent()
		        .getFile(
		                new Path(file.getName().substring(0, file.getName().length() - options.getSemanticFileExtension().length())
		                		+ options.getDiagramFileExtension()));
		final URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
		return uri;
	}
	
	/**
	 * Initialize sad diagram.
	 * 
	 * @param b
	 * @throws IOException
	 */
	private static URI getTemporaryDiagramResourceURI(final IDiagramUtilHelper options, final URI uri) throws IOException {
		final String name = uri.lastSegment();
		String tmpName = "rh_" + name.substring(0, name.length() - options.getSemanticFileExtension().length());
		File tempDir = ScaFileSystemPlugin.getDefault().getTempDirectory();
		final File tempFile = File.createTempFile(tmpName, options.getDiagramFileExtension(), tempDir);
		tempFile.deleteOnExit();

		final URI retVal = URI.createURI(tempFile.toURI().toString());

		return retVal;
	}
	
//	/**
//	 * 
//	 */
//	public static void initializeDiagramResource(final IDiagramUtilHelper options, final URI diagramURI, final Resource sadResource) throws IOException,
//	        CoreException {
//		if (diagramURI.isPlatform()) {
//			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(diagramURI.toPlatformString(true)));
//			
//			file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
//			
//			if (!file.exists()) {
//				final IWorkspaceRunnable operation = new IWorkspaceRunnable() {
//
//					@Override
//					public void run(final IProgressMonitor monitor) throws CoreException {
//						final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//						try {
//							DiagramUtil.populateDiagram(options, diagramURI, sadResource, buffer);
//						} catch (final IOException e) {
//							// PASS
//						}
//						file.create(new ByteArrayInputStream(buffer.toByteArray()), true, monitor);
//					}
//
//				};
//				final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRuleFactory().createRule(file);
//
//				ResourcesPlugin.getWorkspace().run(operation, rule, 0, null);
//			}
//		} else {
//			DiagramUtil.populateDiagram(options, diagramURI, sadResource, null);
//		}
//	}

//	private static void populateDiagram(final IDiagramUtilHelper options, final URI diagramURI, final Resource resource, final OutputStream buffer)
//	        throws IOException {
//		// Create a resource set
//		//
//		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
//
//		// Create a resource for this file.
//		//
//		final Resource diagramResource = resourceSet.createResource(diagramURI);
//
//		final String diagramName = diagramURI.lastSegment();
//		final EObject obj = options.getRootDiagramObject(resource);
//		final Diagram diagram = ViewService.createDiagram(obj, options.getModelId(), options.getDiagramPreferencesHint());
//		if (diagram != null) {
//			diagram.setName(diagramName);
//			diagram.setElement(obj);
//			diagramResource.getContents().add(diagram);
//			if (buffer != null) {
//				diagramResource.save(buffer, options.getSaveOptions());
//			} else {
//				diagramResource.save(options.getSaveOptions());
//			}
//		}
//	}

	
	public static IEditorInput getDiagramWrappedInput(final URI diagramURI, final TransactionalEditingDomain editingDomaing) {
		return new URIEditorInputProxy(new URIEditorInput(diagramURI), editingDomaing);
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public static boolean isDiagramLocalSandbox(final Resource resource) {
		return ".LocalSca.sad.xml".equals(resource.getURI().lastSegment());
	}
}
