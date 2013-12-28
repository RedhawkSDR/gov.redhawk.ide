package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
	public final static int INNER_CONTAINER_SHAPE_TOP_PADDING = 20;
	public final static int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	public final static int LEFT_INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING+DiagramUtil.PROVIDES_PORTS_LEFT_PADDING;
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
	 * @param diagram
	 * @param text
	 * @param businessObject
	 * @param featureProvider
	 * @return
	 */
	public static ContainerShape addOuterRectangle(Diagram diagram, String text, Object businessObject, 
			IFeatureProvider featureProvider, String imageId, Style containerStyle){
		ContainerShape outerContainerShape = Graphiti.getCreateService().createContainerShape(diagram, true);
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(outerContainerShape, 5, 5);
		outerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangle);
		//image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangleImage);//ref helps with resize
		//text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, text);
		cText.setStyle(StyleUtil.getStyleForOuterText(diagram));
		Graphiti.getPeService().setPropertyValue(cText, DiagramUtil.GA_TYPE, DiagramUtil.GA_outerRoundedRectangleText);
		featureProvider.link(outerContainerShape, businessObject); // link container and business object
		
		return outerContainerShape;
	}
	
	/**
	 * Creates a large rectangle intended to be an inside container and links the provided business object to it.
	 * @param diagram
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
		Rectangle interfaceRectangle = Graphiti.getCreateService().createRectangle(interfaceContainerShape);
		featureProvider.link(interfaceContainerShape, anchorBusinessObject);
		interfaceRectangle.setTransparency(.99d);
		Graphiti.getGaLayoutService().setLocationAndSize(interfaceRectangle, 0, 25, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);
		
		//interface lollipop circle
		Shape lollipopEllipseShape = Graphiti.getCreateService().createShape(interfaceContainerShape, true);
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
		int providesPortNameLength = getLongestProvidesPortLength(providesPortStubs)*PORT_CHAR_WIDTH;
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
			providesPortText.setStyle(StyleUtil.getStyleForPortText(diagram));
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
		int usesPortNameLength = getLongestUsesPortLength(usesPortStubs)*PORT_CHAR_WIDTH;
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
			usesPortText.setStyle(StyleUtil.getStyleForPortText(diagram));
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
	 * Returns preferred width for component
	 * @param ci
	 * @return
	 */
	public static int getPreferredWidth(final String name, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		return DiagramUtil.getAdjustedWidth(name, providesPortStubs, usesPortStubs) * 7 + 100;
	}
	/**
	 * Resizes component with desired size.  Minimums are enforced.  Ports are kept at sides while inner box grows
	 * @param context
	 * @param pe
	 */
	public static void resizeOuterContainerShape(IAreaContext context, PictogramElement pe, final String labelText, final String name, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		
		RoundedRectangle outerRoundedRectangle = null;
		RoundedRectangle innerRoundedRectangle = null;
		Text innerRoundedRectangleText = null;
		Text outerRoundedRectangleText = null;
		Rectangle usesPortsRectangle = null;
		Rectangle providesPortsRectangle = null;
		Image innerRoundedRectangleImage = null;
		Image outerRoundedRectangleImage = null;
		Polyline innerRoundedRectangleLine = null;
				
		//enforce minimum width/height
		int width = DiagramUtil.getMinimumWidth(labelText, name, providesPortStubs, usesPortStubs);
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
		gaLayoutService.setLocationAndSize(outerRoundedRectangleText, DiagramUtil.LEFT_INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING+ICON_IMAGE_LENGTH+4, 0, width-(DiagramUtil.LEFT_INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING+ICON_IMAGE_LENGTH+4), 20);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleImage, DiagramUtil.LEFT_INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		
		
		//innerRoundedRectangle
		int innerContainerShapeWidth = width-DiagramUtil.INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING*2 - DiagramUtil.PROVIDES_PORTS_LEFT_PADDING;
		int innerContainerShapeHeight = height-DiagramUtil.INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setLocationAndSize(innerRoundedRectangle, DiagramUtil.LEFT_INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING, DiagramUtil.INNER_CONTAINER_SHAPE_TOP_PADDING, innerContainerShapeWidth, innerContainerShapeHeight);
//		//left justified
//		gaLayoutService.setLocationAndSize(innerRoundedRectangleText, ICON_IMAGE_LENGTH+5, 0, innerRoundedRectangle.getWidth()-(ICON_IMAGE_LENGTH+5), 20);
//		gaLayoutService.setLocationAndSize(innerRoundedRectangleImage, 5, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		IDimension innerRoundedRectangleTextSize = GraphitiUi.getUiLayoutService().calculateTextSize(innerRoundedRectangleText.getValue(), StyleUtil.getInnerTextFont(Graphiti.getPeService().getDiagramForPictogramElement(pe)));
		int xForImage = (innerRoundedRectangle.getWidth()-(innerRoundedRectangleTextSize.getWidth()+ICON_IMAGE_LENGTH+5))/2;
		gaLayoutService.setLocationAndSize(innerRoundedRectangleImage, xForImage, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		gaLayoutService.setLocationAndSize(innerRoundedRectangleText, xForImage+ICON_IMAGE_LENGTH+5, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, innerRoundedRectangleTextSize.getWidth()+10, innerRoundedRectangleTextSize.getHeight());
		innerRoundedRectangleLine.getPoints().get(1).setX(innerContainerShapeWidth);
		
		//providesPortsRectangle
		int providesPortNameLength = DiagramUtil.getLongestProvidesPortLength(providesPortStubs)*DiagramUtil.PORT_CHAR_WIDTH;
		gaLayoutService.setLocationAndSize(providesPortsRectangle, DiagramUtil.PROVIDES_PORTS_LEFT_PADDING, DiagramUtil.PORTS_CONTAINER_SHAPE_TOP_PADDING, DiagramUtil.PORT_SHAPE_WIDTH + providesPortNameLength, providesPortStubs.size()*(DiagramUtil.PORT_SHAPE_HEIGHT));
		
		
		//usesPortsRectangle		
		int usesPortNameLength = DiagramUtil.getLongestUsesPortLength(usesPortStubs)*DiagramUtil.PORT_CHAR_WIDTH;
		int intPortTextX = outerRoundedRectangle.getWidth() - (usesPortNameLength + DiagramUtil.PORT_NAME_HORIZONTAL_PADDING + DiagramUtil.PORT_SHAPE_WIDTH);
		gaLayoutService.setLocationAndSize(usesPortsRectangle, intPortTextX, DiagramUtil.PORTS_CONTAINER_SHAPE_TOP_PADDING, DiagramUtil.PORT_SHAPE_WIDTH + usesPortNameLength + DiagramUtil.PORT_NAME_HORIZONTAL_PADDING, usesPortStubs.size()*(DiagramUtil.PORT_SHAPE_HEIGHT));

	}
	
	//returns length of longest provides port name
	//4 used as minimum, characters cut off otherwise
	public static int getLongestProvidesPortLength(final EList<ProvidesPortStub> providesPortStubs){
		int longest = 4;
		for (final ProvidesPortStub provides : providesPortStubs) {
			if (provides.getName().length() > longest) {
				longest = provides.getName().length();
			}
		}
		return longest;
	}
	
	//returns length of longest uses port name
	//4 used as minimum, characters cut off otherwise
	public static int getLongestUsesPortLength(final EList<UsesPortStub> usesPortsStubs){
		int longest = 4;
		for (final UsesPortStub uses : usesPortsStubs) {
			if (uses.getName().length() > longest) {
				longest = uses.getName().length();
			}
		}
		return longest;
	}
	
	/**
	 * Returns true if the property container contains a property with elementType as value
	 * @param pc
	 * @param elementType
	 * @return
	 */
	public static boolean isPropertyElementType(PropertyContainer pc, String elementType){
		for(Property p: pc.getProperties()){
			if(GA_TYPE.equals(p.getKey()) && elementType.equals(p.getValue())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determine the length by which we need to expand by comparing the largest port names against the name and return
	 * the longer of the two.
	 * @return int Return the length by which we need to expand the associated Shape
	 */
	public static int getAdjustedWidth(final String name, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs) {
		int left = 0, right = 0;

		String usageName = name;
		if (usageName == null) {
			return 0;
		}

		final int nameLength = usageName.length();

		for (final UsesPortStub uses : usesPortStubs) {
			if (uses.getName().length() > left) {
				left = uses.getName().length();
			}
		}

		for (final ProvidesPortStub provides : providesPortStubs) {
			if (provides.getName().length() > right) {
				right = provides.getName().length();
			}
		}

		return (left + right > nameLength ? left + right : nameLength); // SUPPRESS CHECKSTYLE Ternary 
	}
	
	/**
	 * Returns minimum width for Shape with provides and uses port stubs and name text
	 * @param ci
	 * @return
	 */
	public static int getMinimumWidth(final String labelText, final String nameText, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		
		int portsWidth = 0;
		int nameTextWidth = 0;
		int labelTextWidth = 0;
		
		int usesWidth = DiagramUtil.PORT_SHAPE_WIDTH + 
				getLongestUsesPortLength(usesPortStubs)*DiagramUtil.PORT_CHAR_WIDTH + 
				DiagramUtil.PORT_NAME_HORIZONTAL_PADDING + 
				DiagramUtil.PORT_SHAPE_WIDTH;
		int providesWidth = DiagramUtil.PORT_SHAPE_WIDTH + getLongestProvidesPortLength(providesPortStubs)*DiagramUtil.PORT_CHAR_WIDTH;
		
		portsWidth = usesWidth+providesWidth+REQ_PADDING_BETWEEN_PORT_TYPES;
		
		nameTextWidth = (nameText.length() * NAME_CHAR_WIDTH) + INTERFACE_SHAPE_WIDTH;
		
		nameTextWidth = (labelText.length() * LABEL_CHAR_WIDTH) + INTERFACE_SHAPE_WIDTH;
		
		//return the largest
		int largestWidth = portsWidth;
		if(largestWidth < nameTextWidth){
			largestWidth = nameTextWidth;
		}
		if(largestWidth < labelTextWidth){
			largestWidth = labelTextWidth;
		}
		return largestWidth;
		
		
	}
	
}
