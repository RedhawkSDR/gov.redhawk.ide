package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IAreaContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ICreateService;
import org.eclipse.graphiti.services.IGaLayoutService;
import org.eclipse.graphiti.services.IPeCreateService;

public class ComponentPattern extends AbstractPattern implements IPattern{

	
	//These are property key/value pairs that help us resize an existing component by properly identifying graphicsAlgorithms
	private final static String COMPONENT_GA_TYPE = "ComponentGAType";   //key for gA types
	private final static String COMPONENT_GA_outerRoundedRectangle = "outerRoundedRectangle";
	private final static String COMPONENT_GA_innerRoundedRectangle = "innerRoundedRectangle";
	private final static String COMPONENT_GA_cText = "cText";
	private final static String COMPONENT_GA_ciText = "ciText";
	private final static String COMPONENT_GA_providesPortsRectangle = "providesPortsRectangle";
	private final static String COMPONENT_GA_usesPortsRectangle = "usesPortsRectangle";
	
	
	//Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove etc.)
	public final static String COMPONENT_SHAPE_TYPE = "ComponentShapeType";   //key for Shape types
	public final static String COMPONENT_SHAPE_usesPortsContainerShape = "usesPortsContainerShape";
	public final static String COMPONENT_SHAPE_providesPortsContainerShape = "providesPortsContainerShape";
	public final static String COMPONENT_SHAPE_usesPortContainerShape = "usesPortContainerShape";
	public final static String COMPONENT_SHAPE_providesPortContainerShape = "providesPortContainerShape";
	public final static String COMPONENT_SHAPE_usesPortRectangleShape = "usesPortRectangleShape";
	public final static String COMPONENT_SHAPE_providesPortRectangleShape = "providesPortRectangleShape";
	
	
	
//	private final static String COMPONENT_ELEMENT_INDEX = "ComponentElementIndex"; //index key for port elements
	//values for component element type

	
//	private final static String COMPONENT_ELEMENT_providesPortRectangle = "providesPortRectangle";
//	private final static String COMPONENT_ELEMENT_usesPortRectangle = "usesPortRectangle";
//	private final static String COMPONENT_ELEMENT_providesPortText = "providesPortText";
//	private final static String COMPONENT_ELEMENT_usesPortText = "usesPortText";
	
	private final static int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	private final static int INNER_CONTAINER_SHAPE_TOP_PADDING = 20;
	private final static int PORTS_CONTAINER_SHAPE_TOP_PADDING = 50;
	private final static int PORT_NAME_HORIZONTAL_PADDING = 5;
	private final static int REQ_PADDING_BETWEEN_PORT_TYPES = 0;
	private final static int PORT_SHAPE_LENGTH = 15;
	private final static int PORT_CHAR_WIDTH = 7;
	
	

	
	private URI spdUri = null;
	
	public ComponentPattern(){
		super(null);
	}
	
	public URI getSpdUri() {
		return spdUri;
	}
	public void setSpdUri(URI spdUri){
		this.spdUri=spdUri;
	}
	
	@Override
	public String getCreateName(){
		return "Component";
	}
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof SadComponentInstantiation;
	}
	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	
	
	//DIAGRAM FEATURES
	
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof SadComponentInstantiation) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a Component to the diagram.  Immediately calls resize at the end to keep sizing and location in one place.
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation) context.getNewObject();
		Diagram diagram = (Diagram) context.getTargetContainer();
		
		
		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		ICreateService createService = Graphiti.getCreateService();
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		
		ContainerShape outerContainerShape = createService.createContainerShape(diagram, true);
		ContainerShape innerContainerShape;
		RoundedRectangle outerRoundedRectangle;
		RoundedRectangle innerRoundedRectangle;
		Text ciText;
		Text cText;
		
		
		//OUTER RECTANGLE
		outerRoundedRectangle = createService.createRoundedRectangle(outerContainerShape, 5, 5);
		outerRoundedRectangle.setStyle(StyleUtil.getStyleForComponentOuter(diagram));
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, COMPONENT_GA_TYPE, COMPONENT_GA_outerRoundedRectangle);
		cText = createService.createText(outerRoundedRectangle, sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName());
		cText.setStyle(StyleUtil.getStyleForComponentText(diagram));
		Graphiti.getPeService().setPropertyValue(cText, COMPONENT_GA_TYPE, COMPONENT_GA_cText);
		link(outerContainerShape, sadComponentInstantiation); // link container and business object
		

		//INNER RECTANGLE
		innerContainerShape = createService.createContainerShape(outerContainerShape, false);
		innerRoundedRectangle = createService.createRoundedRectangle(innerContainerShape, 5, 5);
		innerRoundedRectangle.setStyle(StyleUtil.getStyleForComponentInner(diagram));
		Graphiti.getPeService().setPropertyValue(innerRoundedRectangle, COMPONENT_GA_TYPE, COMPONENT_GA_innerRoundedRectangle);//ref helps with resize
		ciText = createService.createText(innerRoundedRectangle, sadComponentInstantiation.getUsageName());
		ciText.setStyle(StyleUtil.getStyleForComponentText(diagram));
		Graphiti.getPeService().setPropertyValue(ciText, COMPONENT_GA_TYPE, COMPONENT_GA_ciText);//ref helps with resize

		//provides (input)
		int providesPortNameLength = getLongestProvidesPortLength(sadComponentInstantiation)*PORT_CHAR_WIDTH;
		int iter = 0;
		ContainerShape providesPortsContainerShape = createService.createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_providesPortsContainerShape);//ref prevent selection/deletion/removal
		Rectangle providesPortsRectangle = createService.createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, COMPONENT_GA_TYPE, COMPONENT_GA_providesPortsRectangle);//ref helps with resize

		//iterate over all provides ports
		for(ProvidesPortStub p: sadComponentInstantiation.getProvides()){
			ContainerShape providesPortContainerShape = createService.createContainerShape(providesPortsContainerShape, true);
			Graphiti.getPeService().setPropertyValue(providesPortContainerShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_providesPortContainerShape);//ref prevent selection/deletion/removal
			Rectangle providesPortContainerShapeRectangle = createService.createRectangle(providesPortContainerShape);
			providesPortContainerShapeRectangle.setTransparency(1d);
			gaLayoutService.setLocationAndSize(providesPortContainerShapeRectangle, 0, iter++*(PORT_SHAPE_LENGTH+5), PORT_SHAPE_LENGTH + providesPortNameLength, PORT_SHAPE_LENGTH);
			link(providesPortContainerShape, p);

//			BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(providesPortContainerShape);
//			boxAnchor.setRelativeWidth(1);
//			boxAnchor.setRelativeHeight(.5);
//			boxAnchor.setReferencedGraphicsAlgorithm(providesPortContainerShapeRectangle);
//			Rectangle boxAnchorRectangle = createService.createRectangle(boxAnchor);
//			boxAnchorRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
//			gaLayoutService.setLocationAndSize(boxAnchorRectangle, 0, 0, 50, 50);
			
			Shape providesPortRectangleShape  = createService.createShape(providesPortContainerShape, true);
			Graphiti.getPeService().setPropertyValue(providesPortRectangleShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_providesPortRectangleShape);//ref prevent move
			Rectangle providesPortRectangle = createService.createRectangle(providesPortRectangleShape);
			link(providesPortRectangleShape, p);
			peCreateService.createChopboxAnchor(providesPortRectangleShape);
//			Rectangle providesPortRectangle = createService.createRectangle(boxAnchor);
//			link(boxAnchor, p);
			
			//providesPortRectangle.setFilled(true);
			
			
			providesPortRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
			gaLayoutService.setLocationAndSize(providesPortRectangle, 0, 0, PORT_SHAPE_LENGTH, PORT_SHAPE_LENGTH);

			Shape providesPortTextShape  = createService.createShape(providesPortContainerShape, false);
			Text providesPortText = createService.createText(providesPortTextShape, p.getName());
			providesPortText.setStyle(StyleUtil.getStyleForPortText(diagram));
			gaLayoutService.setLocationAndSize(providesPortText, PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_LENGTH, 0, p.getName().length()*PORT_CHAR_WIDTH, 20);
			//anchor
//			Anchor anchor = PictogramsFactory.eINSTANCE.createChopboxAnchor();
//			anchor.setParent(providesPortContainerShape);
			
		}


		//uses (output)
		int usesPortNameLength = getLongestUsesPortLength(sadComponentInstantiation)*PORT_CHAR_WIDTH;
		int intPortTextX = outerRoundedRectangle.getWidth() - (usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_LENGTH);

		ContainerShape usesPortsContainerShape = createService.createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_usesPortsContainerShape); //ref prevent selection/deletion/removal
		Rectangle usesPortsRectangle = createService.createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, COMPONENT_GA_TYPE, COMPONENT_GA_usesPortsRectangle);//ref helps with resize
		gaLayoutService.setLocationAndSize(usesPortsRectangle, intPortTextX, PORTS_CONTAINER_SHAPE_TOP_PADDING, PORT_SHAPE_LENGTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, sadComponentInstantiation.getUses().size()*(PORT_SHAPE_LENGTH));


		//iterate over all uses ports
		iter = 0;
		for(UsesPortStub p: sadComponentInstantiation.getUses()){
			//port container
			ContainerShape usesPortContainerShape = createService.createContainerShape(usesPortsContainerShape, true);
			Graphiti.getPeService().setPropertyValue(usesPortContainerShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_usesPortContainerShape); //ref prevent selection/deletion/removal
			Rectangle usesPortContainerShapeRectangle = createService.createRectangle(usesPortContainerShape);
			usesPortContainerShapeRectangle.setTransparency(1d);
			gaLayoutService.setLocationAndSize(usesPortContainerShapeRectangle, usesPortsRectangle.getWidth()-(PORT_SHAPE_LENGTH + usesPortNameLength), iter++*(PORT_SHAPE_LENGTH+5), PORT_SHAPE_LENGTH + usesPortNameLength, PORT_SHAPE_LENGTH);
			link(usesPortContainerShape, p);
			
			
//			BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(usesPortContainerShape);
//			boxAnchor.setRelativeWidth(1);
//			boxAnchor.setRelativeHeight(.5);
//			boxAnchor.setReferencedGraphicsAlgorithm(usesPortContainerShapeRectangle);
//			Rectangle boxAnchorRectangle = createService.createRectangle(boxAnchor);
//			boxAnchorRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
//			gaLayoutService.setLocationAndSize(boxAnchorRectangle, 0, 0, 50, 50);
			
			
			//rectangle
			Shape usesPortRectangleShape  = createService.createShape(usesPortContainerShape, true);
			Graphiti.getPeService().setPropertyValue(usesPortRectangleShape, COMPONENT_SHAPE_TYPE, COMPONENT_SHAPE_usesPortRectangleShape);//ref prevent move
			Rectangle usesPortRectangle = createService.createRectangle(usesPortRectangleShape);
			link(usesPortRectangleShape, p);
			peCreateService.createChopboxAnchor(usesPortRectangleShape);
//			Rectangle usesPortRectangle = createService.createRectangle(boxAnchor);
//			link(boxAnchor, p);
			//usesPortRectangle.setFilled(true);
			
			usesPortRectangle.setStyle(StyleUtil.getStyleForUsesPort(diagram));
			gaLayoutService.setLocationAndSize(usesPortRectangle, usesPortNameLength, 0, PORT_SHAPE_LENGTH, PORT_SHAPE_LENGTH);
			//text
			Shape usesPortTextShape  = createService.createShape(usesPortContainerShape, false);
			Text usesPortText = createService.createText(usesPortTextShape, p.getName());
			usesPortText.setStyle(StyleUtil.getStyleForPortText(diagram));
			usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
			gaLayoutService.setLocationAndSize(usesPortText, 0, 0, usesPortContainerShapeRectangle.getWidth()-(usesPortRectangle.getWidth() + PORT_NAME_HORIZONTAL_PADDING), 20);
			//anchor
//			Anchor anchor = PictogramsFactory.eINSTANCE.createChopboxAnchor();
//			anchor.setParent(usesPortContainerShape);
			
		}

		//Define size and location
		AreaContext areaContext = new AreaContext();
		areaContext.setLocation(context.getX(), context.getY());
		areaContext.setSize(getPreferredWidth(sadComponentInstantiation), getPreferredHeight(sadComponentInstantiation));
		
		//Size component (we are doing this so that we don't have to keep sizing/location information in both the add() and resize(), only resize())
		resizeComponent(areaContext, outerContainerShape);
		
		//layout
		layoutPictogramElement(outerContainerShape);

		return outerContainerShape;
	}
	

	
	/**
	 * Resizes component with desired size.  Minimums are enforced.  Ports are kept at sides while inner box grows
	 * @param context
	 * @param pe
	 */
	private void resizeComponent(IAreaContext context, PictogramElement pe){
		
		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation)getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		RoundedRectangle outerRoundedRectangle = null;
		RoundedRectangle innerRoundedRectangle = null;
		Text ciText = null;
		Text cText = null;
		Rectangle usesPortsRectangle = null;
		Rectangle providesPortsRectangle = null;
				
		//enforce minimum width/height
		int width = getMinimumWidth(sadComponentInstantiation);
		if(context.getWidth() > width){
			width = context.getWidth();
		}
		int height = getMinimumHeight(sadComponentInstantiation);
		if(context.getHeight() > height){
			height = context.getHeight();
		}
		
		//find all of our diagram elements
		List<PropertyContainer> children = collectChildren(pe);
		for(PropertyContainer pc: children){
			if(isPropertyElementType(pc, COMPONENT_GA_outerRoundedRectangle)){
				outerRoundedRectangle = (RoundedRectangle)pc;
			}else if(isPropertyElementType(pc, COMPONENT_GA_innerRoundedRectangle)){
				innerRoundedRectangle = (RoundedRectangle)pc;
			}else if(isPropertyElementType(pc, COMPONENT_GA_ciText)){
				ciText = (Text)pc;
			}else if(isPropertyElementType(pc, COMPONENT_GA_cText)){
				cText = (Text)pc;
			}else if(isPropertyElementType(pc, COMPONENT_GA_usesPortsRectangle)){
				usesPortsRectangle = (Rectangle)pc;
			}else if(isPropertyElementType(pc, COMPONENT_GA_providesPortsRectangle)){
				providesPortsRectangle = (Rectangle)pc;
			}
		}
		
		//resize all diagram elements
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		
		//outerRoundedRectangle
		gaLayoutService.setLocationAndSize(outerRoundedRectangle, context.getX(), context.getY(), width, height);
		//cText
		gaLayoutService.setLocationAndSize(cText, 10, 0, width, 20);
		
		//innerRoundedRectangle
		int innerContainerShapeWidth = width-INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING*2;
		int innerContainerShapeHeight = height-INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setLocationAndSize(innerRoundedRectangle, INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING, INNER_CONTAINER_SHAPE_TOP_PADDING, innerContainerShapeWidth, innerContainerShapeHeight);
		//ciText
		gaLayoutService.setLocationAndSize(ciText, 5, 0, innerRoundedRectangle.getWidth()-20, 20);
		
		//providesPortsRectangle
		int providesPortNameLength = getLongestProvidesPortLength(sadComponentInstantiation)*PORT_CHAR_WIDTH;
		gaLayoutService.setLocationAndSize(providesPortsRectangle, 0, PORTS_CONTAINER_SHAPE_TOP_PADDING, PORT_SHAPE_LENGTH + providesPortNameLength, sadComponentInstantiation.getProvides().size()*(PORT_SHAPE_LENGTH));
		
		
		//usesPortsRectangle		
		int usesPortNameLength = getLongestUsesPortLength(sadComponentInstantiation)*PORT_CHAR_WIDTH;
		int intPortTextX = outerRoundedRectangle.getWidth() - (usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_LENGTH);
		gaLayoutService.setLocationAndSize(usesPortsRectangle, intPortTextX, PORTS_CONTAINER_SHAPE_TOP_PADDING, PORT_SHAPE_LENGTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, sadComponentInstantiation.getUses().size()*(PORT_SHAPE_LENGTH));

	}
	
	/**
	 * Returns all of the children contained within the provided PropertyContainer and their children recursively.
	 * @param diagramElement
	 * @return
	 */
	private List<PropertyContainer> collectChildren(PropertyContainer diagramElement){
		
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
	
	
//	private void resizeComponentBackup(IAreaContext context, PictogramElement pe){
//		
//		//ContainerShape outerContainerShape = cs;
//		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation)getFeatureProvider().getBusinessObjectForPictogramElement(pe);
//		int portShapeLength = 15;
//		//ContainerShape innerContainerShape;
//		RoundedRectangle outerRoundedRectangle = null;
//		RoundedRectangle innerRoundedRectangle = null;
//		Text ciText = null;
//		Text cText = null;
//		//Map<ContainerShape> providesPortContainerShapes = new HashMap<ContainerShape>;
//		Map<Integer, Rectangle> providesPortRectangles = new HashMap<Integer, Rectangle>();
//		Map<Integer, Text> providesPortTexts = new HashMap<Integer, Text>();
//		//Map<Integer, ContainerShape> usesPortContainerShapes;
//		Map<Integer, Rectangle> usesPortRectangles = new HashMap<Integer, Rectangle>();
//		Map<Integer, Text> usesPortTexts = new HashMap<Integer, Text>();
//		
//		
//		//find all of our diagram elements
//		List<PropertyContainer> children = collectChildren(pe);
//		for(PropertyContainer pc: children){
////			if(isPropertyElementType(pc, "innerContainerShape")){
////				innerContainerShape = (ContainerShape)pc;
//			if(isPropertyElementType(pc, COMPONENT_ELEMENT_outerRoundedRectangle)){
//				outerRoundedRectangle = (RoundedRectangle)pc;
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_innerRoundedRectangle)){
//				innerRoundedRectangle = (RoundedRectangle)pc;
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_ciText)){
//				ciText = (Text)pc;
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_cText)){
//				cText = (Text)pc;
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_providesPortRectangle)){
//				providesPortRectangles.put(getIndexProperty(pc), (Rectangle)pc);
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_usesPortRectangle)){
//				usesPortRectangles.put(getIndexProperty(pc), (Rectangle)pc);
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_usesPortText)){
//				usesPortTexts.put(getIndexProperty(pc), (Text)pc);
//			}else if(isPropertyElementType(pc, COMPONENT_ELEMENT_providesPortText)){
//				providesPortTexts.put(getIndexProperty(pc), (Text)pc);
//			}
//		}
//		
//		//resize all diagram elements
//		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
//		
//		//outerRoundedRectangle
//		gaLayoutService.setLocationAndSize(outerRoundedRectangle, context.getX(), context.getY(), context.getWidth(), context.getHeight());
//		//cText
//		gaLayoutService.setLocationAndSize(cText, 10, 0, context.getWidth(), 20);
//		
//		//innerRoundedRectangle
//		int innerContainerShapeWidth = context.getWidth()-INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING*2;
//		int innerContainerShapeHeight = context.getHeight()-INNER_CONTAINER_SHAPE_TOP_PADDING;
//		gaLayoutService.setLocationAndSize(innerRoundedRectangle, INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING, INNER_CONTAINER_SHAPE_TOP_PADDING, innerContainerShapeWidth, innerContainerShapeHeight);
//		//ciText
//		gaLayoutService.setLocationAndSize(ciText, 5, 0, innerRoundedRectangle.getWidth()-20, 20);
//		
//
//		int portNamePadding = PORT_NAME_HORIZONTAL_PADDING;
//		
//		//providesPortRectangles
//		for(Integer key: providesPortRectangles.keySet()){
//			int y = key*20;
//			gaLayoutService.setLocationAndSize(providesPortRectangles.get(key), 0, y, portShapeLength, portShapeLength);
//		}
//		
//		//providesPortTexts
//		for(Integer key: providesPortTexts.keySet()){
//			int y = key*20;
//			ProvidesPortStub p = (ProvidesPortStub)getFeatureProvider().getBusinessObjectForPictogramElement(providesPortRectangles.get(key).getPictogramElement());
//			gaLayoutService.setLocationAndSize(providesPortTexts.get(key), portNamePadding, y-INNER_CONTAINER_SHAPE_TOP_PADDING, p.getName().length()*PORT_CHAR_WIDTH, 20);
//		}
//		
//		//usesPortRectangles
//		for(Integer key: usesPortRectangles.keySet()){
//			int y = key*20;
//			int intPortSquareX = innerRoundedRectangle.getWidth() + portShapeLength;
//			gaLayoutService.setLocationAndSize(usesPortRectangles.get(key), intPortSquareX, y, portShapeLength, portShapeLength);
//		}
//		
//		//usesPortTexts
//		for(Integer key: usesPortTexts.keySet()){
//			int y = key*20;
//			int usesPortNameLength = getLongestUsesPortLength(sadComponentInstantiation)*PORT_CHAR_WIDTH;
//			int intPortTextX = innerRoundedRectangle.getWidth() - (usesPortNameLength + portNamePadding);
//			UsesPortStub p = (UsesPortStub)getFeatureProvider().getBusinessObjectForPictogramElement(usesPortRectangles.get(key).getPictogramElement());
//			gaLayoutService.setLocationAndSize(usesPortTexts.get(key), intPortTextX, y-INNER_CONTAINER_SHAPE_TOP_PADDING, usesPortNameLength, 20);
//		}
//		
//	}
	
	
	
	/**
	 * Returns true if the property container contains a property with elementType as value
	 * @param pc
	 * @param elementType
	 * @return
	 */
	private boolean isPropertyElementType(PropertyContainer pc, String elementType){
		for(Property p: pc.getProperties()){
			if(COMPONENT_GA_TYPE.equals(p.getKey()) && elementType.equals(p.getValue())){
				return true;
			}
		}
		return false;
	}
	
	
//	private Integer getIndexProperty(PropertyContainer pc){
//		for(Property p: pc.getProperties()){
//			if(COMPONENT_ELEMENT_INDEX.equals(p.getKey())){
//				return Integer.valueOf(p.getValue());
//			}
//		}
//		return null;
//	}

	/**
	 * Resize Component
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		//resize component
		resizeComponent(context, context.getPictogramElement());
	}
	
	/**
	 * Resizing a Component shape is always allowed
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}
	
	/**
	 * Returns minimum height for Component
	 * @param ci
	 * @return
	 */
	private int getMinimumHeight(final ComponentInstantiation ci){
		return getAdjustedHeight(ci) * PORT_SHAPE_LENGTH + PORTS_CONTAINER_SHAPE_TOP_PADDING + 10;
	}
	
	/**
	 * Returns minimum width for Component
	 * @param ci
	 * @return
	 */
	private int getMinimumWidth(final ComponentInstantiation ci){
		
		int usesWidth = PORT_SHAPE_LENGTH + 
				getLongestUsesPortLength(ci)*PORT_CHAR_WIDTH + 
				PORT_NAME_HORIZONTAL_PADDING + 
				PORT_SHAPE_LENGTH;
		int providesWidth = PORT_SHAPE_LENGTH + getLongestProvidesPortLength(ci)*PORT_CHAR_WIDTH;
		
		return usesWidth+providesWidth+REQ_PADDING_BETWEEN_PORT_TYPES;
	}
	
	/**
	 * Returns preferred height for component
	 * @param ci
	 * @return
	 */
	public int getPreferredHeight(final ComponentInstantiation ci){
		return getAdjustedHeight(ci) * PORT_SHAPE_LENGTH + 100;
	}
	
	/**
	 * Returns preferred width for component
	 * @param ci
	 * @return
	 */
	public int getPreferredWidth(final ComponentInstantiation ci){
		return getAdjustedWidth(ci) * 7 + 100;
	}
	

	
	/**
	 * Determine the height by which we need to expand by comparing the number of uses and provides ports and return the largest
	 * @param ci The Component Instantiation that we shall examine the characteristics of
	 * @return int Return the length by which we need to expand the height of the associated ComponentInstantiationFigure
	 */
	private int getAdjustedHeight(final ComponentInstantiation ci) {;
		if (ci == null) {
			return 0;
		}
		int numPorts = Math.max(ci.getProvides().size(), ci.getUses().size());
		
		return numPorts;
	}
	
	private int getLongestProvidesPortLength(final ComponentInstantiation ci){
		int longest = 0;
		for (final ProvidesPortStub provides : ci.getProvides()) {
			if (provides.getName().length() > longest) {
				longest = provides.getName().length();
			}
		}
		return longest;
	}
	
	private int getLongestUsesPortLength(final ComponentInstantiation ci){
		int longest = 0;
		for (final UsesPortStub uses : ci.getUses()) {
			if (uses.getName().length() > longest) {
				longest = uses.getName().length();
			}
		}
		return longest;
	}
	
	/**
	 * Determine the length by which we need to expand by comparing the largest port names against the component name and return
	 * the longer of the two.
	 * @param ci The Component Instantiation that we shall examine the characteristics of
	 * @return int Return the length by which we need to expand the associated ComponentInstantiationFigure
	 */
	private int getAdjustedWidth(final ComponentInstantiation ci) {
		int left = 0, right = 0;
		if (ci == null) {
			return 0;
		}
		String usageName = ci.getUsageName();
		if (usageName == null) {
			return 0;
		}

		final int name = usageName.length();

		for (final UsesPortStub uses : ci.getUses()) {
			if (uses.getName().length() > left) {
				left = uses.getName().length();
			}
		}

		for (final ProvidesPortStub provides : ci.getProvides()) {
			if (provides.getName().length() > right) {
				right = provides.getName().length();
			}
		}

		return (left + right > name ? left + right : name); // SUPPRESS CHECKSTYLE Ternary 
	}
}
