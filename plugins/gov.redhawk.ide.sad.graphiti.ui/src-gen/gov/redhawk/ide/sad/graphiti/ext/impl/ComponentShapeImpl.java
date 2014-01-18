package gov.redhawk.ide.sad.graphiti.ext.impl;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.math.BigInteger;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Shape</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ComponentShapeImpl extends RHContainerShapeImpl implements ComponentShape
{
	
	//These are property key/value pairs that help us resize an existing shape by properly identifying graphicsAlgorithms
	public final static String GA_startOrderEllipse = "startOrderEllipse";
	public final static String GA_startOrderText = "startOrderText";
	
	//Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove etc.)
	public final static String SHAPE_startOrderEllipseShape = "startOrderEllipseShape";
	
	//Shape size constants
	public final static int START_ORDER_ELLIPSE_DIAMETER = 17;
	public final static int START_ORDER_TOP_TEXT_PADDING = 0;
	public final static int START_ORDER_ELLIPSE_LEFT_PADDING = 20;
	public final static int START_ORDER_ELLIPSE_RIGHT_PADDING = 5;
	public final static int START_ORDER_ELLIPSE_TOP_PADDING = 5;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentShapeImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return RHGxPackage.Literals.COMPONENT_SHAPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Creates the inner shapes that make up this container shape
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void init(ContainerShape targetContainerShape, SadComponentInstantiation ci, 
			IFeatureProvider featureProvider)
	{
		super.init(targetContainerShape, ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), 
				ci, featureProvider,
				ImageProvider.IMG_COMPONENT_PLACEMENT,
				StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(targetContainerShape)),
				ci.getUsageName(), ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(DUtil.findDiagram(targetContainerShape)),
				ci.getInterfaceStub(), ci.getUses(), ci.getProvides());

		
		//get inner containershape
		ContainerShape innerContainerShape = getInnerContainerShape();

		//add start order ellipse
		addStartOrderEllipse(innerContainerShape, ci);
	}


	/**
	 * <!-- begin-user-doc -->
	 * Add an Ellipse to provided container shape that will contain the start order from sadComponentInstantiation
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ContainerShape addStartOrderEllipse(ContainerShape innerContainerShape, SadComponentInstantiation sadComponentInstantiation)
	{
		Diagram diagram= DUtil.findDiagram(innerContainerShape);

		//start order ellipse
		ContainerShape startOrderEllipseShape = Graphiti.getCreateService().createContainerShape(innerContainerShape, false);
		Graphiti.getPeService().setPropertyValue(startOrderEllipseShape, DUtil.SHAPE_TYPE, SHAPE_startOrderEllipseShape);
		Ellipse startOrderEllipse = Graphiti.getCreateService().createEllipse(startOrderEllipseShape);
		//if start order zero (assembly controller), then use special style
		if(BigInteger.ZERO.compareTo(sadComponentInstantiation.getStartOrder()) == 0){
			startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderAssemblyControllerEllipse(diagram));
		}else{
			startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderEllipse(diagram));
		}
		Graphiti.getPeService().setPropertyValue(startOrderEllipse, DUtil.GA_TYPE, GA_startOrderEllipse);
		Graphiti.getGaLayoutService().setSize(startOrderEllipse, START_ORDER_ELLIPSE_DIAMETER, START_ORDER_ELLIPSE_DIAMETER);

		//port text
		Shape startOrderTextShape  = Graphiti.getCreateService().createShape(startOrderEllipseShape, false);
		Text startOrderText = Graphiti.getCreateService().createText(startOrderTextShape, sadComponentInstantiation.getStartOrder().toString());
		Graphiti.getPeService().setPropertyValue(startOrderText, DUtil.GA_TYPE, GA_startOrderText);
		startOrderText.setStyle(StyleUtil.getStyleForStartOrderText(diagram));
		//TODO: bwhoff2 we need to handle the x for the text inside the shape
		IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(
				sadComponentInstantiation.getStartOrder().toString(), StyleUtil.getStartOrderFont(diagram));
		int textX = START_ORDER_ELLIPSE_DIAMETER/2 - textDimension.getWidth()/2;
		Graphiti.getGaLayoutService().setLocationAndSize(startOrderText, textX, START_ORDER_TOP_TEXT_PADDING, START_ORDER_ELLIPSE_DIAMETER, START_ORDER_ELLIPSE_DIAMETER);


		return startOrderEllipseShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Return the startOrderEllipseShape
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ContainerShape getStartOrderEllipseShape()
	{
		return (ContainerShape)DUtil.findFirstPropertyContainer(this, SHAPE_startOrderEllipseShape);
	}

	/**
	 * <!-- begin-user-doc -->
	 * Return the startOrderText
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Text getStartOrderText()
	{
		return (Text)DUtil.findFirstPropertyContainer(getStartOrderEllipseShape(), GA_startOrderText);
	}

	/**
	 * <!-- begin-user-doc -->
	 * performs a layout on the contents of this shape
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void layout(){

		super.layout();

		//start order ellipse
		Graphiti.getGaLayoutService().setLocation(getStartOrderEllipseShape().getGraphicsAlgorithm(), getInnerContainerShape().getGraphicsAlgorithm().getWidth() - (START_ORDER_ELLIPSE_DIAMETER + START_ORDER_ELLIPSE_RIGHT_PADDING), START_ORDER_ELLIPSE_TOP_PADDING);

	}

	/**
	 * Performs either an update or a check to determine if update is required.  
	 * if performUpdate flag is true it will update the shape, 
	 * otherwise it will return reason why update is required.
	 * @param ci
	 * @param performUpdate
	 * @return
	 */
	public Reason internalUpdate(SadComponentInstantiation ci, IFeatureProvider featureProvider, boolean performUpdate){
		Diagram diagram = DUtil.findDiagram(this);
		Reason superReason = null;
		if(performUpdate){
			superReason = super.update(this, 
					ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), 
					ci, featureProvider, ImageProvider.IMG_COMPONENT_PLACEMENT,
					StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(this)),
					ci.getUsageName(), ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(diagram),
					ci.getInterfaceStub(), ci.getUses(), ci.getProvides());
		}else{
			superReason = super.updateNeeded(this, 
					ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), 
					ci, featureProvider, ImageProvider.IMG_COMPONENT_PLACEMENT,
					StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(this)),
					ci.getUsageName(), ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(diagram),
					ci.getInterfaceStub(), ci.getUses(), ci.getProvides());
		}

		boolean updateStatus;

		//if parent says we need to update, return now
		if(!performUpdate && superReason.toBoolean()){
			return superReason;
		}else{
			updateStatus = superReason.toBoolean();
		}


		//startOrderText
		Text startOrderTextGA = getStartOrderText();
		if(startOrderTextGA != null && ci.getStartOrder().compareTo(new BigInteger(startOrderTextGA.getValue())) != 0){
			if(performUpdate){
				updateStatus = true;
				startOrderTextGA.setValue(ci.getStartOrder().toString());
				//adjust for startOrderText size
				IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(
						ci.getStartOrder().toString(), StyleUtil.getStartOrderFont(diagram));
				int textX = START_ORDER_ELLIPSE_DIAMETER/2 - textDimension.getWidth()/2;
				Graphiti.getGaLayoutService().setLocationAndSize(startOrderTextGA, textX, START_ORDER_TOP_TEXT_PADDING, 
						START_ORDER_ELLIPSE_DIAMETER, START_ORDER_ELLIPSE_DIAMETER);
				
				//Style
				Ellipse startOrderEllipse = (Ellipse)getStartOrderEllipseShape().getGraphicsAlgorithm();
				//if start order zero (assembly controller), then use special style
				if(BigInteger.ZERO.compareTo(ci.getStartOrder()) == 0){
					startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderAssemblyControllerEllipse(diagram));
				}else{
					startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderEllipse(diagram));
				}
			}else{
				return new Reason(true, "Component start order requires update");
			}
		}

		

		if(updateStatus && performUpdate){
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/**
	 * <!-- begin-user-doc -->
     * Updates the shape's contents using the supplied fields.  Return true if an update occurred, false otherwise.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Reason update(SadComponentInstantiation ci, IFeatureProvider featureProvider)
	{
		return internalUpdate(ci, featureProvider, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Reason updateNeeded(SadComponentInstantiation ci, IFeatureProvider featureProvider)
	{
		return internalUpdate(ci, featureProvider, false);
	}
	
	/**
	 * Returns minimum width for Shape with provides and uses port stubs and name text
	 * @param ci
	 * @return
	 */
	public int getMinimumWidth(final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs){
		
		//determine width of parentshape
		int rhContainerShapeMinWidth = super.getMinimumWidth(outerTitle, innerTitle, providesPortStubs, usesPortStubs);
		
		
		int innerTitleWidth = 0;
		Diagram diagram = DUtil.findDiagram(this);
		

		//inner title (including start order)
		IDimension innerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(
				innerTitle, StyleUtil.getInnerTitleFont(diagram));
		innerTitleWidth = innerTitleDimension.getWidth() + INTERFACE_SHAPE_WIDTH + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING + ComponentShapeImpl.START_ORDER_ELLIPSE_DIAMETER + ComponentShapeImpl.START_ORDER_ELLIPSE_LEFT_PADDING + ComponentShapeImpl.START_ORDER_ELLIPSE_RIGHT_PADDING;
		
		//return the largest width
		if(rhContainerShapeMinWidth > innerTitleWidth){
			return rhContainerShapeMinWidth;
		}else{
			return innerTitleWidth;
		}

	}

} //ComponentShapeImpl
