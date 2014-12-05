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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;

public class GraphicsUtil {

	private static final IGaService gaService = Graphiti.getGaService();
	static final IPeService peService = Graphiti.getPeService();
	private static Map<Diagram, SizeTemplate> diagramSizeMap;

	// TODO move all size properties to separate interface
	public static int DATA_WIDTH = 36;
	public static int DATA_HEIGHT = 50;

	public static int CHOREOGRAPHY_WIDTH = 150;
	public static int CHOREOGRAPHY_HEIGHT = 150;
	public static int PARTICIPANT_BAND_HEIGHT = 20;

	public static final int SHAPE_PADDING = 6;
	public static final int TEXT_PADDING = 5;
	public static final String LABEL_PROPERTY = "label"; //$NON-NLS-1$
	
	// TODO: Determine all cases to make a line break! The following implementation are the easy once.
	private static final String LINE_BREAK = "\n"; //$NON-NLS-1$

	public static class SizeTemplate{
		
		private Size eventSize = new Size(GraphicsUtil.EVENT_SIZE, GraphicsUtil.EVENT_SIZE);
		private Size gatewaySize = new Size(GraphicsUtil.GATEWAY_RADIUS*2, GraphicsUtil.GATEWAY_RADIUS*2);
		private Size activitySize = new Size(GraphicsUtil.TASK_DEFAULT_WIDTH, GraphicsUtil.TASK_DEFAULT_HEIGHT);
		
		public Size getEventSize() {
			return eventSize;
		}
		public void setEventSize(Size eventSize) {
			this.eventSize = eventSize;
		}
		public Size getGatewaySize() {
			return gatewaySize;
		}
		public void setGatewaySize(Size gatewaySize) {
			this.gatewaySize = gatewaySize;
		}
		public Size getActivitySize() {
			return this.activitySize;
		}
		public void setActivitySize(Size activitySize) {
			this.activitySize = activitySize;
		}
	}
	
	public static class Size {
		private int width;
		private int height;
		
		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}
	}

	public static class Envelope {
		public Rectangle rect;
		public Polyline line;
	}

	public static class Asterisk {
		public Polyline horizontal;
		public Polyline vertical;
		public Polyline diagonalDesc;
		public Polyline diagonalAsc;
	}

	public static class Compensation {
		public Polygon arrow1;
		public Polygon arrow2;
	}

	public static class Cross {
		public Polyline vertical;
		public Polyline horizontal;
	}

	public static class DiagonalCross {
		public Polyline diagonalAsc;
		public Polyline diagonalDesc;
	}

	public static class MultiInstance {
		public Polyline line1;
		public Polyline line2;
		public Polyline line3;
	}

	public static class Loop {
		public Polyline circle;
		public Polyline arrow;
	}

	public static class Expand {
		public Rectangle rect;
		public Polyline horizontal;
		public Polyline vertical;
	}

	public static class LineSegment {
		private Point start;
		private Point end;
		
		public LineSegment() {
			this(0,0,0,0);
		}
		public LineSegment(Point start, Point end) {
			this(start.getX(),start.getY(), end.getX(),end.getY());
		}
		public LineSegment(int x1, int y1, int x2, int y2) {
			start = Graphiti.getCreateService().createPoint(x1, y1);
			end = Graphiti.getCreateService().createPoint(x2, y2);
		}
		public void setStart(Point p) {
			setStart(p.getX(),p.getY());
		}
		public void setStart(int x, int y) {
			start.setX(x);
			start.setY(y);
		}
		public void setEnd(Point p) {
			setEnd(p.getX(),p.getY());
		}
		public void setEnd(int x, int y) {
			end.setX(x);
			end.setY(y);
		}
		public Point getStart() {
			return start;
		}
		public Point getEnd() {
			return end;
		}
		public double getDistance(Point p) {
			// for vertical and horizontal line segments, the distance to a point
			// is the orthogonal distance if the point lies between the start and end
			// points of the line segment
			if (isHorizontal()) {
				if (p.getX()>=start.getX() && p.getX()<=end.getX())
					return Math.abs(start.getY() - p.getY());
			}
			if (isVertical()) {
				if (p.getY()>=start.getY() && p.getY()<=end.getY())
					return Math.abs(start.getX() - p.getX());
			}
			// otherwise, the distance is the minimum of the distances
			// of the point to the two endpoints of the line segment
	        double d1 = getDistanceToStart(p);
	        double d2 = getDistanceToEnd(p);
	        return Math.min(d1, d2);
		}
		public boolean isHorizontal() {
			return Math.abs(start.getY() - end.getY()) <= 1;
		}
		public boolean isVertical() {
			return Math.abs(start.getX() - end.getX()) <= 1;
		}
		public boolean isSlanted() {
			return !isHorizontal() && !isVertical();
		}
		public double getDistanceToStart(Point p) {
	        return Math.hypot(start.getX()-p.getX(), start.getY()-p.getY());
		}
		public double getDistanceToEnd(Point p) {
	        return Math.hypot(end.getX()-p.getX(), end.getY()-p.getY());
		}
		
		public String toString() {
			return "[" + start.getX() + "," + start.getY() +"]" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					" [" + end.getX() + "," + end.getY() +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/* GATEWAY */

	private static final String DELETABLE_PROPERTY = "deletable"; //$NON-NLS-1$

	public static final int GATEWAY_RADIUS = 25;
	public static final int GATEWAY_TEXT_AREA = 15;

	private static int generateRatioPointValue(float originalPointValue, float ratioValue) {
		return Math.round(Float.valueOf(originalPointValue * ratioValue));
	}
	
	// TODO: Think about line break in the ui...
	public static int getLabelHeight(AbstractText text) {
		if (text.getValue() != null && !text.getValue().isEmpty()) {
			String[] strings = text.getValue().split(LINE_BREAK);
			return strings.length * 14;
		}
		return 0;
	}

	// TODO: Think about a maximum-width...
	public static int getLabelWidth(AbstractText text) {
		if (text.getValue() != null && !text.getValue().isEmpty()) {
			String[] strings = text.getValue().split(LINE_BREAK);
			int result = 0;
			for (String string : strings) {
				IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(string, text.getFont());
				if (dim.getWidth() > result) {
					result = dim.getWidth();
				}
			}
			return result;
		}
		return 0;
	}
	
	public static void alignWithShape(AbstractText text, ContainerShape labelContainer, 
			int width,
			int height,
			int shapeX,
			int shapeY, 
			int preShapeX, 
			int preShapeY){
		final int textHeight = getLabelHeight(text);
		final int textWidth = getLabelWidth(text);
		
		int currentLabelX = labelContainer.getGraphicsAlgorithm().getX();
		int currentLabelY = labelContainer.getGraphicsAlgorithm().getY();
		
		int newShapeX = shapeX - ((textWidth + SHAPE_PADDING) / 2) + width / 2;
		int newShapeY = shapeY + height + 2;

		if (currentLabelX > 0 && preShapeX > 0){
			newShapeX = currentLabelX + (shapeX - preShapeX);
			newShapeY = currentLabelY + (shapeY - preShapeY);
		}
		
		IGaService gaService = Graphiti.getGaService();
		
		gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(), 
				newShapeX , newShapeY ,
				textWidth + SHAPE_PADDING, textHeight + SHAPE_PADDING);
		gaService.setLocationAndSize(text, 
				0, 0,
				textWidth + TEXT_PADDING, textHeight + TEXT_PADDING);
	}
	
	private static float calculateRatio(float x, float y) {
		return x / y;
	}
	
	private static int getShapeHeight(Shape shape) {
		return shape.getGraphicsAlgorithm().getHeight();
	}
	
	private static int getShapeWidth(Shape shape) {
		return shape.getGraphicsAlgorithm().getWidth();
	}
	
	public static Shape getContainedShape(ContainerShape container, String propertyKey) {
		IPeService peService = Graphiti.getPeService();
		Iterator<Shape> iterator = peService.getAllContainedShapes(container).iterator();
		while (iterator.hasNext()) {
			Shape shape = iterator.next();
			String property = peService.getPropertyValue(shape, propertyKey);
			if (property != null && new Boolean(property)) {
				return shape;
			}
		}
		return null;
	}
	
	public static List<PictogramElement> getContainedPictogramElements(PictogramElement container, String propertyKey) {
		List<PictogramElement> pictogramElements = new ArrayList<PictogramElement>();
		IPeService peService = Graphiti.getPeService();
		Iterator<PictogramElement> iterator = peService.getAllContainedPictogramElements(container).iterator();
		while (iterator.hasNext()) {
			PictogramElement pe = iterator.next();
			String property = peService.getPropertyValue(pe, propertyKey);
			if (property != null && new Boolean(property)) {
				pictogramElements.add(pe);
			}
		}
		return pictogramElements;
	}

//	private static final int[] GATEWAY = { 0, GATEWAY_RADIUS, GATEWAY_RADIUS, 0, 2 * GATEWAY_RADIUS, GATEWAY_RADIUS,
//	        GATEWAY_RADIUS, 2 * GATEWAY_RADIUS };

	public static Polygon createGateway(Shape container, final int width, final int height) {
		final int widthRadius = width / 2;
		final int heightRadius = height / 2;
		final int[] gateWayPoints = {0, heightRadius, widthRadius, 0, 2 * widthRadius, heightRadius, widthRadius, 2 * heightRadius};
		return gaService.createPolygon(container, gateWayPoints);
	}

	public static Polygon createGatewayPentagon(ContainerShape container) {
		Shape pentagonShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		
//		Polygon pentagon = gaService.createPolygon(pentagonShape,
//				new int[] { GATEWAY_RADIUS, 18,
//						GATEWAY_RADIUS + 8, GATEWAY_RADIUS - 2,
//						GATEWAY_RADIUS + 5, GATEWAY_RADIUS + 7,
//						GATEWAY_RADIUS - 5, GATEWAY_RADIUS + 7,
//						GATEWAY_RADIUS - 8, GATEWAY_RADIUS - 2 });
		Polygon pentagon = gaService.createPolygon(pentagonShape,
				new int[] { gatewayWidth / 2, generateRatioPointValue(18, heightRatio),
							gatewayWidth / 2 + generateRatioPointValue(8, widthRatio), gatewayHeight / 2 - generateRatioPointValue(2, heightRatio),
							gatewayWidth / 2 + generateRatioPointValue(5, widthRatio), gatewayHeight / 2 + generateRatioPointValue(7, heightRatio),
							gatewayWidth / 2 - generateRatioPointValue(5, widthRatio), gatewayHeight / 2 + generateRatioPointValue(7, heightRatio),
							gatewayWidth / 2 - generateRatioPointValue(8, widthRatio), gatewayHeight / 2 - generateRatioPointValue(2, heightRatio) });
							
		peService.setPropertyValue(pentagonShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return pentagon;
	}

	public static Ellipse createGatewayInnerCircle(Ellipse outer) {
		final int gatewayHeight = outer.getHeight();
		final int gatewayWidth = outer.getWidth();
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		Float x = (5 * widthRatio) * new Float(0.8);
		Float y = (5 * heightRatio) * new Float(0.8);

		Float width = gatewayHeight * new Float(0.8);
		Float height = gatewayWidth * new Float(0.8);
		
//		gaService.setLocationAndSize(ellipse, 14, 14, 23, 23);
		Ellipse ellipse = gaService.createEllipse(outer);
		gaService.setLocationAndSize(ellipse,
				 Math.round(x), Math.round(y),
				 Math.round(width), Math.round(height));
		ellipse.setFilled(false);
		ellipse.setLineWidth(1);
		peService.setPropertyValue(ellipse, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return ellipse;
	}

	public static Ellipse createGatewayOuterCircle(ContainerShape container) {
		Shape ellipseShape = peService.createShape(container, false);
		Ellipse ellipse = gaService.createEllipse(ellipseShape);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
//		gaService.setLocationAndSize(ellipse, 12, 12, 27, 27);
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(12, widthRatio),
				generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(27, widthRatio),
				generateRatioPointValue(27, heightRatio));
		ellipse.setFilled(false);
		ellipse.setLineWidth(1);
		peService.setPropertyValue(ellipseShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return ellipse;
	}

	public static Cross createGatewayCross(ContainerShape container) {
		Shape verticalShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
//		Polyline verticalLine = gaService.createPolyline(verticalShape, new int[] { 24, 7, 24, 43 });
		Polyline verticalLine = gaService.createPolyline(verticalShape,
				new int[] { generateRatioPointValue(24, widthRatio), generateRatioPointValue(7, heightRatio),
							generateRatioPointValue(24, widthRatio), generateRatioPointValue(43, heightRatio) });
		verticalLine.setLineWidth(3);
		peService.setPropertyValue(verticalShape, DELETABLE_PROPERTY, "false"); //$NON-NLS-1$

		Shape horizontalShape = peService.createShape(container, false);
		
//		Polyline horizontalLine = gaService.createPolyline(horizontalShape, new int[] { 7, 24, 43, 24 });
		
		Polyline horizontalLine = gaService.createPolyline(horizontalShape,
				new int[] { generateRatioPointValue(7, widthRatio), generateRatioPointValue(24, heightRatio),
							generateRatioPointValue(43, widthRatio), generateRatioPointValue(24, heightRatio) });
		horizontalLine.setLineWidth(3);
		peService.setPropertyValue(horizontalShape, DELETABLE_PROPERTY, "false"); //$NON-NLS-1$

		Cross cross = new Cross();
		cross.vertical = verticalLine;
		cross.horizontal = horizontalLine;
		return cross;
	}

	public static DiagonalCross createGatewayDiagonalCross(ContainerShape container) {
		IPeService service = Graphiti.getPeService();

		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));

		Shape diagonalDescShape = service.createShape(container, false);
//		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape, new int[] { 13, 14, 37, 37 });
		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape,
				new int[] { generateRatioPointValue(14, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(37, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalDesc.setLineWidth(3);
		peService.setPropertyValue(diagonalDescShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalAscShape = service.createShape(container, false);
		
//		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape, new int[] { 37, 14, 13, 37 });
		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape,
				new int[] { generateRatioPointValue(37, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(14, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalAsc.setLineWidth(3);
		peService.setPropertyValue(diagonalAscShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		DiagonalCross diagonalCross = new DiagonalCross();
		diagonalCross.diagonalDesc = diagonalDesc;
		diagonalCross.diagonalAsc = diagonalAsc;
		return diagonalCross;
	}

	public static Polygon createEventGatewayParallelCross(ContainerShape container) {
		Shape crossShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		int n1x = generateRatioPointValue(14, widthRatio);
		int n1y = generateRatioPointValue(14, heightRatio);
		int n2x = generateRatioPointValue(22, widthRatio);
		int n2y = generateRatioPointValue(22, heightRatio);
		int n3x = generateRatioPointValue(28, widthRatio);
		int n3y = generateRatioPointValue(28, heightRatio);
		int n4x = generateRatioPointValue(36, widthRatio);
		int n4y = generateRatioPointValue(36, heightRatio);
		
		Collection<Point> points = new ArrayList<Point>();
		points.add(gaService.createPoint(n1x, n2y));
		points.add(gaService.createPoint(n2x, n2y));
		points.add(gaService.createPoint(n2x, n1y));
		points.add(gaService.createPoint(n3x, n1y));
		points.add(gaService.createPoint(n3x, n2y));
		points.add(gaService.createPoint(n4x, n2y));
		points.add(gaService.createPoint(n4x, n3y));
		points.add(gaService.createPoint(n3x, n3y));
		points.add(gaService.createPoint(n3x, n4y));
		points.add(gaService.createPoint(n2x, n4y));
		points.add(gaService.createPoint(n2x, n3y));
		points.add(gaService.createPoint(n1x, n3y));
		Polygon cross = gaService.createPolygon(crossShape, points);
		cross.setFilled(false);
		cross.setLineWidth(1);
		peService.setPropertyValue(crossShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return cross;
	}

	public static Asterisk createGatewayAsterisk(ContainerShape container) {
		IPeService service = Graphiti.getPeService();

		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));

		Shape verticalShape = service.createShape(container, false);
//		Polyline vertical = gaService.createPolyline(verticalShape, new int[] { 23, 8, 23, 42 });
		Polyline vertical = gaService.createPolyline(verticalShape,
				new int[] { generateRatioPointValue(24, widthRatio), generateRatioPointValue(7, heightRatio),
							generateRatioPointValue(24, widthRatio), generateRatioPointValue(43, heightRatio) });
		vertical.setLineWidth(3);
		peService.setPropertyValue(verticalShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape horizontalShape = service.createShape(container, false);
//		Polyline horizontal = gaService.createPolyline(horizontalShape, new int[] { 8, 24, 42, 24 });
		Polyline horizontal = gaService.createPolyline(horizontalShape,
				new int[] { generateRatioPointValue(7, widthRatio), generateRatioPointValue(24, heightRatio),
							generateRatioPointValue(43, widthRatio), generateRatioPointValue(24, heightRatio) });
		horizontal.setLineWidth(3);
		peService.setPropertyValue(horizontalShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalDescShape = service.createShape(container, false);
//		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape, new int[] { 13, 14, 37, 37 });
		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape,
				new int[] { generateRatioPointValue(14, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(37, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalDesc.setLineWidth(3);
		peService.setPropertyValue(diagonalDescShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalAscShape = service.createShape(container, false);
//		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape, new int[] { 37, 14, 13, 37 });
		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape,
				new int[] { generateRatioPointValue(37, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(14, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalAsc.setLineWidth(3);
		peService.setPropertyValue(diagonalAscShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Asterisk a = new Asterisk();
		a.horizontal = horizontal;
		a.vertical = vertical;
		a.diagonalDesc = diagonalDesc;
		a.diagonalAsc = diagonalAsc;
		return a;
	}

	public static void clearGateway(PictogramElement element) {
		for (PictogramElement pe : getContainedPictogramElements(element, DELETABLE_PROPERTY)) {
			peService.deletePictogramElement(pe);
		}
	}

	/* EVENT */

	public static final int EVENT_SIZE = 36;
//	public static final int EVENT_TEXT_AREA = 15;

	public static Ellipse createEventShape(Shape container, final int width, final int height) {
		Ellipse ellipse = gaService.createEllipse(container);
		gaService.setLocationAndSize(ellipse, 0, 0, width, height);
		return ellipse;
	}

	public static Envelope createEventEnvelope(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
//		return createEnvelope(shape, 9, 9, 18, 18);
		return createEnvelope(shape,
				generateRatioPointValue(9, widthRatio),
				generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(18, widthRatio),
				generateRatioPointValue(14, heightRatio));
		
	}

	public static Polygon createEventPentagon(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
//		return gaService.createPolygon(shape, new int[] { r, 7, r + 10, r - 4, r + 7, r + 10, r - 7, r + 10, r - 10,
//		        r - 4 });
		return gaService.createPolygon(shape,
				new int[] { eventWidth / 2, generateRatioPointValue(7, heightRatio),
				eventWidth / 2 + generateRatioPointValue(10, widthRatio), eventHeight / 2 - generateRatioPointValue(4, heightRatio),
				eventWidth / 2 + generateRatioPointValue(7, widthRatio), eventHeight / 2 + generateRatioPointValue(10, heightRatio),
				eventWidth / 2 - generateRatioPointValue(7, widthRatio), eventHeight / 2 + generateRatioPointValue(10, heightRatio),
				eventWidth / 2 - generateRatioPointValue(10, widthRatio), eventHeight / 2 - generateRatioPointValue(4, heightRatio) });
	}

	public static Ellipse createIntermediateEventCircle(Ellipse ellipse) {
		final int eventHeight = ellipse.getHeight();
		final int eventWidth = ellipse.getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Float x = (5 * widthRatio) * new Float(0.8);
		Float y = (5 * heightRatio) * new Float(0.8);

		Float width = eventWidth * new Float(0.8);
		Float height = eventHeight * new Float(0.8);
		
		Ellipse circle = gaService.createEllipse(ellipse);
//		gaService.setLocationAndSize(circle, 
//				generateRatioPointValue(4, widthRatio), generateRatioPointValue(4, heightRatio),
//				eventWidth - generateRatioPointValue(8, widthRatio), eventWidth - generateRatioPointValue(8, heightRatio));
		gaService.setLocationAndSize(circle,
				 Math.round(x), Math.round(y),
				 width.intValue(), height.intValue());
		circle.setLineWidth(1);
		circle.setFilled(false);
		return circle;
	}

	public static Image createEventImage(Shape shape, String imageId) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Image image = gaService.createImage(shape, imageId);
		gaService.setLocationAndSize(image, 
				generateRatioPointValue(8, widthRatio), generateRatioPointValue(8, heightRatio),
				generateRatioPointValue(20, widthRatio), generateRatioPointValue(20, heightRatio));
		return image;
	}

	public static Polygon createEventSignal(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));

		Polygon polygon = gaService.createPolygon(shape, 
				new int[] { generateRatioPointValue(16, widthRatio), generateRatioPointValue(4, heightRatio),
							generateRatioPointValue(28, widthRatio), generateRatioPointValue(26, heightRatio),
							generateRatioPointValue(7, widthRatio), generateRatioPointValue(26, heightRatio) });
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventEscalation(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int[] points = { widthRadius, generateRatioPointValue(8, heightRatio),
						 widthRadius + generateRatioPointValue(8, widthRatio), heightRadius + generateRatioPointValue(9, heightRatio),
						 widthRadius, heightRadius + generateRatioPointValue(2, heightRatio),
						 widthRadius - generateRatioPointValue(8, widthRatio), heightRadius + generateRatioPointValue(9, heightRatio) };
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Compensation createEventCompensation(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Rectangle rect = gaService.createInvisibleRectangle(shape);

		int w = generateRatioPointValue(22, widthRatio);
		int h = generateRatioPointValue(18, heightRatio);
		gaService.setLocationAndSize(rect, 
				generateRatioPointValue(5, widthRatio), generateRatioPointValue(9, heightRatio), w, h);

		int _w = w / 2;
		int _h = h / 2;
		int[] pontsArrow1 = { _w, 0, _w, h, 0, _h };
		Polygon arrow1 = gaService.createPolygon(rect, pontsArrow1);

		int[] pontsArrow2 = { w, 0, w, h, w / 2, _h };
		Polygon arrow2 = gaService.createPolygon(rect, pontsArrow2);

		Compensation compensation = new Compensation();
		compensation.arrow1 = arrow1;
		compensation.arrow2 = arrow2;
		return compensation;
	}

	public static Polygon createEventLink(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;

		int[] points = { 
				generateRatioPointValue(32, widthRatio), heightRadius,
				generateRatioPointValue(23, widthRatio), heightRadius + generateRatioPointValue(11, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius + generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(5, widthRatio), heightRadius + generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(5, widthRatio), heightRadius - generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius - generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius - generateRatioPointValue(11, heightRatio)};
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventError(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int[] points = { 
				widthRadius + generateRatioPointValue(4, widthRatio), heightRadius,
				widthRadius + generateRatioPointValue(10, widthRatio), heightRadius - generateRatioPointValue(10, heightRatio),
				widthRadius + generateRatioPointValue(7, widthRatio), heightRadius + generateRatioPointValue(10, heightRatio),
				widthRadius - generateRatioPointValue(4, widthRatio), heightRadius,
				widthRadius - generateRatioPointValue(10, widthRatio), heightRadius + generateRatioPointValue(10, heightRatio),
				widthRadius - generateRatioPointValue(7, widthRatio), heightRadius - generateRatioPointValue(10, heightRatio)};
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventCancel(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int a1 = generateRatioPointValue(9, widthRatio);
		int a2 = generateRatioPointValue(9, heightRatio);
		int b1 = generateRatioPointValue(12, widthRatio);
		int b2 = generateRatioPointValue(12, heightRatio);
		int c1 = generateRatioPointValue(4, widthRatio);
		int c2 = generateRatioPointValue(4, heightRatio);
		int[] points = { widthRadius, heightRadius - c2,
						 widthRadius + a1, heightRadius - b2,
						 widthRadius + b1, heightRadius - a2,
						 widthRadius + c1, heightRadius,
						 widthRadius + b1, heightRadius + a2,
						 widthRadius + a1, heightRadius + b2,
						 widthRadius, heightRadius + c2,
						 widthRadius - a1, heightRadius + b2,
						 widthRadius - b1, heightRadius + a2,
						 widthRadius - c1, heightRadius,
						 widthRadius - b1, heightRadius - a2,
						 widthRadius - a1, heightRadius - b2 };
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Ellipse createEventTerminate(Shape terminateShape) {
		final int eventHeight = terminateShape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = terminateShape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Ellipse ellipse = gaService.createEllipse(terminateShape);
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(6, widthRatio), generateRatioPointValue(6, heightRatio),
				eventWidth - generateRatioPointValue(12, widthRatio), eventHeight - generateRatioPointValue(12, heightRatio));
		ellipse.setLineWidth(1);
		ellipse.setFilled(true);
		return ellipse;
	}

	public static Ellipse createEventNotAllowed(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Ellipse ellipse = gaService.createEllipse(shape);
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(6, widthRatio), generateRatioPointValue(6, heightRatio),
				eventWidth - generateRatioPointValue(12, widthRatio), eventHeight - generateRatioPointValue(12, heightRatio));
		ellipse.setLineWidth(2);
		ellipse.setFilled(false);
		ellipse.setForeground(manageColor(shape, IColorConstant.RED));

		
		int[] points = {
				generateRatioPointValue(8, widthRatio), generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(28, widthRatio), generateRatioPointValue(24, heightRatio),
		};

		shape = Graphiti.getPeService().createShape(shape.getContainer(), false);
		Polyline polygon = gaService.createPolyline(shape, points);
		polygon.setLineWidth(2);
		polygon.setForeground(manageColor(shape, IColorConstant.RED));
		return ellipse;
	}

	public static Polygon createEventParallelMultiple(Shape shape) {
		int r = EVENT_SIZE / 2;
		int a = 3;
		int b = 11;
		int[] points = { r - a, r - b, r + a, r - b, r + a, r - a, r + b, r - a, r + b, r + a, r + a, r + a, r + a,
		        r + b, r - a, r + b, r - a, r + a, r - b, r + a, r - b, r - a, r - a, r - a };
		Polygon cross = gaService.createPolygon(shape, points);
		cross.setFilled(false);
		cross.setLineWidth(1);
		return cross;
	}
//bwhoff2
//	public static void deleteEventShape(ContainerShape containerShape) {
//		for (PictogramElement shape : containerShape.getChildren()) {
//			if (shape.getLink() != null) {
//				EList<EObject> objects = shape.getLink().getBusinessObjects();
//				if (objects.size()>0 && objects.get(0) instanceof EventDefinition) {
//					peService.deletePictogramElement(shape);
//					break;
//				}
//			}
//		}
//	}

	/* OTHER */

	public static Envelope createEnvelope(GraphicsAlgorithmContainer gaContainer, int x, int y, int w, int h) {
		Rectangle rect = gaService.createRectangle(gaContainer);
		gaService.setLocationAndSize(rect, x, y, w, h);
		rect.setFilled(false);

		Polyline line = gaService.createPolyline(rect, new int[] { 0, 0, w / 2, h / 2, w, 0 });

		Envelope envelope = new Envelope();
		envelope.rect = rect;
		envelope.line = line;

		return envelope;
	}

	public static Polygon createDataArrow(Polygon p) {
		int[] points = { 4, 8, 14, 8, 14, 4, 18, 10, 14, 16, 14, 12, 4, 12 };
		Polygon arrow = gaService.createPolygon(p, points);
		arrow.setLineWidth(1);
		return arrow;
	}

	// ACTIVITY

	public static final int TASK_DEFAULT_WIDTH = 110;
	public static final int TASK_DEFAULT_HEIGHT = 50;
	public static final int TASK_IMAGE_SIZE = 16;

	public static final int SUB_PROCEESS_DEFAULT_WIDTH = 300;
	public static final int SUB_PROCESS_DEFAULT_HEIGHT = 300;

	public static final int MARKER_WIDTH = 10;
	public static final int MARKER_HEIGHT = 10;

	private static final String ACTIVITY_MARKER_CONTAINER = "activity.marker.container"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_COMPENSATE = "activity.marker.compensate"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_LC_STANDARD = "activity.marker.lc.standard"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL = "activity.marker.lc.multi.sequential"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_LC_MULTI_PARALLEL = "activity.marker.lc.multi.parallel"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_AD_HOC = "activity.marker.adhoc"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_EXPAND = "activity.marker.expand"; //$NON-NLS-1$
	public static final String ACTIVITY_MARKER_OFFSET = "activity.marker.offset"; //$NON-NLS-1$
	public static final String EVENT_MARKER_CONTAINER = "event.marker.container"; //$NON-NLS-1$

	private static GraphicsAlgorithmContainer createActivityMarkerCompensate(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        ACTIVITY_MARKER_COMPENSATE);
		Compensation compensation = createCompensation(algorithmContainer, MARKER_WIDTH, MARKER_HEIGHT);
		compensation.arrow1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		compensation.arrow2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerStandardLoop(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
				ACTIVITY_MARKER_LC_STANDARD);

		int[] xy = { 8, 10, 10, 5, 5, 0, 0, 5, 3, 10 };
		int[] bend = { 0, 0, 3, 4, 4, 4, 4, 3, 3, 0 };
		Polyline circle = gaService.createPolyline(algorithmContainer, xy, bend);

		Loop loop = new Loop();
		loop.circle = circle;
		loop.arrow = gaService.createPolyline(algorithmContainer, new int[] { 5, 5, 5, 10, 0, 10 });
		loop.circle.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		loop.arrow.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerMultiParallel(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
				ACTIVITY_MARKER_LC_MULTI_PARALLEL);
		MultiInstance multiInstance = new MultiInstance();
		multiInstance.line1 = gaService.createPolyline(algorithmContainer, new int[] { 2, 0, 2, MARKER_HEIGHT });
		multiInstance.line2 = gaService.createPolyline(algorithmContainer, new int[] { 5, 0, 5, MARKER_HEIGHT });
		multiInstance.line3 = gaService.createPolyline(algorithmContainer, new int[] { 8, 0, 8, MARKER_HEIGHT });
		multiInstance.line1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line3.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerMultiSequential(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
		MultiInstance multiInstance = new MultiInstance();
		multiInstance.line1 = gaService.createPolyline(algorithmContainer, new int[] { 0, 2, MARKER_WIDTH, 2 });
		multiInstance.line2 = gaService.createPolyline(algorithmContainer, new int[] { 0, 5, MARKER_WIDTH, 5 });
		multiInstance.line3 = gaService.createPolyline(algorithmContainer, new int[] { 0, 8, MARKER_WIDTH, 8 });
		multiInstance.line1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line3.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerAdHoc(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        ACTIVITY_MARKER_AD_HOC);
		int[] xy = { 0, 8, 3, 2, 7, 8, 10, 2 };
		int[] bend = { 0, 3, 3, 3, 3, 3, 3, 0 };
		Polyline tilde = gaService.createPolyline(algorithmContainer, xy, bend);
		tilde.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerExpand(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        ACTIVITY_MARKER_EXPAND);

		Rectangle rect = gaService.createRectangle(algorithmContainer);
		rect.setFilled(false);
		gaService.setLocationAndSize(rect, 0, 0, 10, 10);

		Expand expand = new Expand();
		expand.rect = rect;
		expand.horizontal = gaService.createPolyline(algorithmContainer, new int[] { 0, 5, 10, 5 });
		expand.vertical = gaService.createPolyline(algorithmContainer, new int[] { 5, 0, 5, 10 });
		expand.rect.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		expand.horizontal.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		expand.vertical.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	
	private static ContainerShape getActivityMarkerContainer(ContainerShape container) {
		String property = peService.getPropertyValue(container, ACTIVITY_MARKER_CONTAINER);
		if (property != null && new Boolean(property)) {
			return container;
		}
		return (ContainerShape) getContainedShape(container, ACTIVITY_MARKER_CONTAINER);
	}

	private static ContainerShape createActivityMarkerContainer(ContainerShape container) {
		
		ContainerShape markerContainer = getActivityMarkerContainer(container);
		if (markerContainer==null) {
			// need to create a marker container first
			markerContainer = peService.createContainerShape(container, false);
			Rectangle markerInvisibleRect = gaService.createInvisibleRectangle(markerContainer);
			GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
			int x = ga.getWidth() / 2;
			int y = ga.getHeight() - 10;
			int w = 50;
			int h = 10;
			gaService.setLocationAndSize(markerInvisibleRect, x, y, w, h);
			peService.setPropertyValue(markerContainer, GraphicsUtil.ACTIVITY_MARKER_CONTAINER, Boolean.toString(true));

			createActivityMarkerCompensate(markerContainer);
			createActivityMarkerStandardLoop(markerContainer);
			createActivityMarkerMultiParallel(markerContainer);
			createActivityMarkerMultiSequential(markerContainer);
			createActivityMarkerAdHoc(markerContainer);
			createActivityMarkerExpand(markerContainer);
			
			// make them all invisible
			Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
			while (iterator.hasNext()) {
				Shape shape = iterator.next();
				shape.setVisible(false);
			}
		}
		return markerContainer;
	}

	public static void setActivityMarkerOffest(ContainerShape container, int offset) {
		peService.setPropertyValue(container, GraphicsUtil.ACTIVITY_MARKER_OFFSET, Integer.toString(offset));
	}

	public static int getActivityMarkerOffest(ContainerShape container) {
		int offset = 0;
		String s = peService.getPropertyValue(container, GraphicsUtil.ACTIVITY_MARKER_OFFSET);
		if (s!=null) {
			try {
				offset = Integer.parseInt(s);
			}
			catch (Exception e) {
			}
		}
		return offset;
	}
	
	public static void layoutActivityMarkerContainer(ContainerShape container) {

		ContainerShape markerContainer = getActivityMarkerContainer(container);
		if (markerContainer!=null) {
			int lastX = 0;
			Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
			while (iterator.hasNext()) {
				Shape marker = iterator.next();
				if (marker.isVisible()) {
					GraphicsAlgorithm ga = marker.getGraphicsAlgorithm();
					gaService.setLocation(ga, lastX, 0);
					lastX += ga.getWidth() + 3;
				}
			}
			
			GraphicsAlgorithm parentGa = container.getGraphicsAlgorithm();
			GraphicsAlgorithm ga = markerContainer.getGraphicsAlgorithm();
			int newWidth = parentGa.getWidth();
			int newHeight = parentGa.getHeight();
			int x = (newWidth / 2) - (lastX / 2);
			int y = newHeight - 13 - getActivityMarkerOffest(container);
			gaService.setLocation(ga, x, y);
		}
	}
	
	public static void showActivityMarker(ContainerShape container, String property) {

		ContainerShape markerContainer = getActivityMarkerContainer(container);
		if (markerContainer==null) {
			markerContainer = createActivityMarkerContainer(container);
		}
		GraphicsUtil.getContainedShape(markerContainer, property).setVisible(true);
		layoutActivityMarkerContainer(container);
	}
	
	public static void hideActivityMarker(ContainerShape container, String property) {

		ContainerShape markerContainer = getActivityMarkerContainer(container);
		if (markerContainer==null) {
			markerContainer = createActivityMarkerContainer(container);
		}
		GraphicsUtil.getContainedShape(markerContainer, property).setVisible(false);
		layoutActivityMarkerContainer(container);
	}
	
	private static Color manageColor(PictogramElement pe, IColorConstant colorConstant) {
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pe);
		return Graphiti.getGaService().manageColor(diagram, colorConstant);
	}

	private static GraphicsAlgorithmContainer createActivityMarkerGaContainer(ContainerShape markerContainer,
	        String property) {
		GraphicsAlgorithm ga = markerContainer.getGraphicsAlgorithm();

		int totalWidth = MARKER_WIDTH;
		int parentW = ((ContainerShape) markerContainer.eContainer()).getGraphicsAlgorithm().getWidth();
		int parentH = ((ContainerShape) markerContainer.eContainer()).getGraphicsAlgorithm().getHeight();
		
		int lastX = 0;

		Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
		while (iterator.hasNext()) {
			Shape containedShape = (Shape) iterator.next();
			if (containedShape.isVisible()) {
				GraphicsAlgorithm containedGa = containedShape.getGraphicsAlgorithm();
				totalWidth += containedGa.getWidth();
				lastX = containedGa.getX() + containedGa.getWidth();
			}
		}

		gaService.setLocationAndSize(ga, (parentW / 2) - (totalWidth / 2), parentH-MARKER_WIDTH, totalWidth, MARKER_HEIGHT);

		Shape shape = peService.createShape(markerContainer, false);
		peService.setPropertyValue(shape, property, Boolean.toString(true));
		Rectangle invisibleRect = gaService.createInvisibleRectangle(shape);
		gaService.setLocationAndSize(invisibleRect, lastX, 0, MARKER_WIDTH, MARKER_HEIGHT);

		return invisibleRect;
	}

	private static Compensation createCompensation(GraphicsAlgorithmContainer container, int w, int h) {
		int[] xy = { 0, h / 2, w / 2, 0, w / 2, h };
		Polygon arrow1 = gaService.createPolygon(container, xy);
		arrow1.setFilled(false);

		xy = new int[] { w / 2, h / 2, w, 0, w, h };
		Polygon arrow2 = gaService.createPolygon(container, xy);
		arrow2.setFilled(false);

		Compensation compensation = new Compensation();
		compensation.arrow1 = arrow1;
		compensation.arrow2 = arrow2;

		return compensation;
	}

	/**
	 * Check if the given Point is with a given distance of the given Location.
	 * 
	 * @param p - the Point to check
	 * @param loc - the target Location
	 * @param dist - the maximum distance horizontally and vertically from the given Location
	 * @return true if the point lies within the rectangular area of the Location.
	 */
	public static boolean isPointNear(Point p, ILocation loc, int dist) {
		int x = p.getX();
		int y = p.getY();
		int lx = loc.getX();
		int ly = loc.getY();
		return lx-dist <= x && x <= lx+dist && ly-dist <= y && y <= ly+dist;
	}

	public static void setEventSize(int width, int height, Diagram diagram) {
		if (diagramSizeMap == null) {
			diagramSizeMap = new HashMap<Diagram, GraphicsUtil.SizeTemplate>();
			SizeTemplate temp = new SizeTemplate();
			temp.setEventSize(new Size(EVENT_SIZE, EVENT_SIZE));
			temp.setGatewaySize(new Size(GATEWAY_RADIUS*2, GATEWAY_RADIUS*2));
		}
		
		SizeTemplate sizeTemplate = diagramSizeMap.get(diagram);
		if (sizeTemplate == null) {
			sizeTemplate = new SizeTemplate();
			diagramSizeMap.put(diagram, sizeTemplate);
		}
		sizeTemplate.setEventSize(new Size(width, height));
	}

	public static void setGatewaySize(int width, int height, Diagram diagram) {
		if (diagramSizeMap == null) {
			diagramSizeMap = new HashMap<Diagram, GraphicsUtil.SizeTemplate>();
		}
		
		SizeTemplate sizeTemplate = diagramSizeMap.get(diagram);
		if (sizeTemplate == null) {
			sizeTemplate = new SizeTemplate();
			diagramSizeMap.put(diagram, sizeTemplate);
		}
		sizeTemplate.setGatewaySize(new Size(width, height));
	}

	public static void setActivitySize(int width, int height, Diagram diagram) {
		if (diagramSizeMap == null) {
			diagramSizeMap = new HashMap<Diagram, GraphicsUtil.SizeTemplate>();
		}
		
		SizeTemplate sizeTemplate = diagramSizeMap.get(diagram);
		if (sizeTemplate == null) {
			sizeTemplate = new SizeTemplate();
			diagramSizeMap.put(diagram, sizeTemplate);
		}
		sizeTemplate.setActivitySize(new Size(width, height));
	}
	
	public static Size getEventSize(Diagram diagram) {
		if (diagramSizeMap != null) {
			SizeTemplate temp = diagramSizeMap.get(diagram);
			if (temp != null) {
				return temp.getEventSize();
			}
		}
		return new Size(EVENT_SIZE, EVENT_SIZE);
	}
	
	public static Size getGatewaySize(Diagram diagram) {
		if (diagramSizeMap != null) {
			SizeTemplate temp = diagramSizeMap.get(diagram);
			if (temp != null) {
				return temp.getGatewaySize();
			}
		}
		return new Size(GATEWAY_RADIUS*2, GATEWAY_RADIUS*2);
	}
	
	public static Size getActivitySize(Diagram diagram) {
		if (diagramSizeMap != null) {
			SizeTemplate temp = diagramSizeMap.get(diagram);
			if (temp != null) {
				return temp.getActivitySize();
			}
		}
		return new Size(TASK_DEFAULT_WIDTH, TASK_DEFAULT_HEIGHT);
	}
//bwhoff2
//	
//	public static Size getShapeSize(BaseElement be, Diagram diagram) {
//		if (be instanceof Event)
//			return getEventSize(diagram);
//		if (be instanceof Gateway)
//			return getGatewaySize(diagram);
//		if (be instanceof Activity)
//			return getActivitySize(diagram);
//		return new Size(TASK_DEFAULT_WIDTH,TASK_DEFAULT_HEIGHT);
//	}
	
	public static boolean contains(Shape parent, Shape child) {
		IDimension size = calculateSize(child);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(child);
		return contains(parent, createPoint(loc.getX(), loc.getY()))
				&& contains(parent, createPoint(loc.getX() + size.getWidth(), loc.getY()))
				&& contains(parent, createPoint(loc.getX() + size.getWidth(), loc.getY() + size.getHeight()))
				&& contains(parent, createPoint(loc.getX(), loc.getY() + size.getHeight()));
	}
	
	public static boolean contains(Shape shape, Point point) {
		IDimension size = calculateSize(shape);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
		int x = point.getX();
		int y = point.getY();
		return x>loc.getX() && x<loc.getX() + size.getWidth() &&
				y>loc.getY() && y<loc.getY() + size.getHeight();
	}
	
	public static boolean intersects(Shape shape1, Shape shape2) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		ILocation loc2 = layoutService.getLocationRelativeToDiagram(shape2);
		int x2 = loc2.getX();
		int y2 = loc2.getY();
		int w2 = getShapeWidth(shape2);
		int h2 = getShapeHeight(shape2);
		return intersects(shape1, x2, y2, w2, h2);
	}
	
	public static boolean intersects(Shape shape1, int x2, int y2, int w2, int h2) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		ILocation loc1 = layoutService.getLocationRelativeToDiagram(shape1);
		int x1 = loc1.getX();
		int y1 = loc1.getY();
		int w1 = getShapeWidth(shape1);
		int h1 = getShapeHeight(shape1);
		return intersects(x1, y1, w1, h1, x2, y2, w2, h2);
	}

	public static boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if(x2<=x1 || y1<=y2) {  
			int t1, t2, t3, t4;
			t1 = x1; x1 = x2; x2 = t1;  
			t2 = y1; y1 = y2; y2 = t2;  
			t3 = w1; w1 = w2; w2 = t3;  
			t4 = h1; h1 = h2; h2 = t4;  
		}  
		if( y2 + h2 < y1 || y1 + h1 < y2 ||  x2 + w2 < x1 || x1 + w1 < x2 ) {
			return false;
		}
		return true;
	}
	
	public static boolean intersects(Shape shape, Connection connection) {
		Point p1 = createPoint(connection.getStart());
		Point p3 = createPoint(connection.getEnd());
		if (connection instanceof FreeFormConnection) {
			FreeFormConnection ffc = (FreeFormConnection) connection;
			Point p2 = p1;
			for (Point p : ffc.getBendpoints()) {
				if (intersectsLine(shape, p1, p))
					return true;
				p2 = p1 = p;
			}
			if (intersectsLine(shape, p2, p3))
				return true;
		}
		else if (intersectsLine(shape, p1, p3))
			return true;
		return false;
	}
	
	public static boolean intersectsLine(Shape shape, Point p1, Point p2) {
		ILocation loc = peService.getLocationRelativeToDiagram(shape);
		IDimension size = calculateSize(shape);
		// adjust the shape rectangle so that a point touching one of the edges
		// is not considered to be "intersecting"
		if (size.getWidth()>2) {
			loc.setX(loc.getX()+1);
			size.setWidth(size.getWidth()-2);
		}
		if (size.getHeight()>2) {
			loc.setY(loc.getY()+1);
			size.setHeight(size.getHeight()-2);
		}
		return RectangleIntersectsLine.intersectsLine(
				p1.getX(), p1.getY(), p2.getX(), p2.getY(),
				loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
		
//		java.awt.Rectangle rect = new java.awt.Rectangle(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
//		return rect.intersectsLine(start.getX(), start.getY(), end.getX(), end.getY());
	}

	/**
	 * Code copied from {@link java.awt.geom.Rectangle2D#intersectsLine(double, double, double, double)}
	 * in an attempt to avoid loading the java.awt package here...
	 */
	public static final class RectangleIntersectsLine {
	    private static final int OUT_LEFT = 1;
	    private static final int OUT_TOP = 2;
	    private static final int OUT_RIGHT = 4;
	    private static final int OUT_BOTTOM = 8;
	
	    private static int outcode(double pX, double pY, double rectX, double rectY, double rectWidth, double rectHeight) {
	        int out = 0;
	        if (rectWidth <= 0) {
	            out |= OUT_LEFT | OUT_RIGHT;
	        } else if (pX < rectX) {
	            out |= OUT_LEFT;
	        } else if (pX > rectX + rectWidth) {
	            out |= OUT_RIGHT;
	        }
	        if (rectHeight <= 0) {
	            out |= OUT_TOP | OUT_BOTTOM;
	        } else if (pY < rectY) {
	            out |= OUT_TOP;
	        } else if (pY > rectY + rectHeight) {
	            out |= OUT_BOTTOM;
	        }
	        return out;
	    }
	
	    public static boolean intersectsLine(double lineX1, double lineY1, double lineX2, double lineY2, double rectX, double rectY, double rectWidth, double rectHeight) {
	        int out1, out2;
	        if ((out2 = outcode(lineX2, lineY2, rectX, rectY, rectWidth, rectHeight)) == 0) {
	            return true;
	        }
	        while ((out1 = outcode(lineX1, lineY1, rectX, rectY, rectWidth, rectHeight)) != 0) {
	            if ((out1 & out2) != 0) {
	                return false;
	            }
	            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
	                double x = rectX;
	                if ((out1 & OUT_RIGHT) != 0) {
	                    x += rectWidth;
	                }
	                lineY1 = lineY1 + (x - lineX1) * (lineY2 - lineY1) / (lineX2 - lineX1);
	                lineX1 = x;
	            } else {
	                double y = rectY;
	                if ((out1 & OUT_BOTTOM) != 0) {
	                    y += rectHeight;
	                }
	                lineX1 = lineX1 + (y - lineY1) * (lineX2 - lineX1) / (lineY2 - lineY1);
	                lineY1 = y;
	            }
	        }
	        return true;
	    }
	}

	public static boolean intersects(Point p1Start, Point p1End, Point p2Start, Point p2End) {
		return isLineIntersectingLine(
				p1Start.getX(), p1Start.getY(),
				p1End.getX(), p1End.getY(),
				p2Start.getX(), p2Start.getY(),
				p2End.getX(), p2End.getY()
		);
	}
	
	/**
	 * Check if two line segments intersects. Integer domain.
	 * 
	 * @param x0, y0, x1, y1 End points of first line to check.
	 * @param x2, yy, x3, y3 End points of second line to check.
	 * @return True if the two lines intersects.
	 */
	public static boolean isLineIntersectingLine(int x0, int y0, int x1,
			int y1, int x2, int y2, int x3, int y3) {
		int s1 = sameSide(x0, y0, x1, y1, x2, y2, x3, y3);
		int s2 = sameSide(x2, y2, x3, y3, x0, y0, x1, y1);

		return s1 <= 0 && s2 <= 0;
	}

	/**
	 * Check if two points are on the same side of a given line. Algorithm from
	 * Sedgewick page 350.
	 * 
	 * @param x0, y0, x1, y1 The line.
	 * @param px0, py0 First point.
	 * @param px1, py1 Second point.
	 * @return <0 if points on opposite sides. =0 if one of the points is
	 *         exactly on the line >0 if points on same side.
	 */
	private static int sameSide(int x0, int y0, int x1, int y1,
			int px0, int py0, int px1, int py1) {
		int sameSide = 0;

		int dx = x1 - x0;
		int dy = y1 - y0;
		int dx1 = px0 - x0;
		int dy1 = py0 - y0;
		int dx2 = px1 - x1;
		int dy2 = py1 - y1;

		// Cross product of the vector from the endpoint of the line to the
		// point
		int c1 = dx * dy1 - dy * dx1;
		int c2 = dx * dy2 - dy * dx2;

		if (c1 != 0 && c2 != 0)
			sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
		else if (dx == 0 && dx1 == 0 && dx2 == 0)
			sameSide = !isBetween(y0, y1, py0) && !isBetween(y0, y1, py1) ? 1
					: 0;
		else if (dy == 0 && dy1 == 0 && dy2 == 0)
			sameSide = !isBetween(x0, x1, px0) && !isBetween(x0, x1, px1) ? 1
					: 0;

		return sameSide;
	}

	/**
	 * Return true if c is between a and b.
	 */
	private static boolean isBetween(int a, int b, int c) {
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}

	public static Color clone(Color c) {
		return c;
	}

	public static boolean pointsEqual(Point p1, Point p2) {
		return p1.getX()==p2.getX() && p1.getY()==p2.getY();
	}
	
	public static Point createPoint(Point p) {
		return gaService.createPoint(p.getX(), p.getY());
	}
	
	public static Point createPoint(int x, int y) {
		return gaService.createPoint(x, y);
	}

	public static Point createPoint(Anchor a) {
		return createPoint(peService.getLocationRelativeToDiagram(a));
	}

	public static Point createPoint(AnchorContainer ac) {
		if (ac instanceof Shape)
			return createPoint(peService.getLocationRelativeToDiagram((Shape)ac));
		return null;
	}
	
	public static Point getShapeCenter(AnchorContainer shape) {
		Point p = createPoint(shape);
		IDimension size = calculateSize(shape);
		p.setX( p.getX() + size.getWidth()/2 );
		p.setY( p.getY() + size.getHeight()/2 );
		return p;
	}

	public static Point createPoint(ILocation loc) {
		return createPoint(loc.getX(), loc.getY());
	}

	public static Point getMidpoint(Point p1, Point p2) {
		int dx = p2.getX() - p1.getX();
		int dy = p2.getY() - p1.getY();
		int x = p1.getX() + dx/2;
		int y = p1.getY() + dy/2;
		return createPoint(x,y);
	}

	public static double getLength(ILocation start, ILocation end) {
		double a = (double)(start.getX() - end.getX());
		double b = (double)(start.getY() - end.getY());
		return Math.sqrt(a*a + b*b);
	}

	public static double getLength(List<Point> points) {
		double length = 0;
		int size = points.size();
		if (size>=2) {
			Point p1 = points.get(0);
			for (int i=1; i<size-1; ++i) {
				Point p2 = points.get(i);
				length += getLength(p1,p2);
				p1 = p2;
			}
		}
		return length;
	}
	
	public static double getLength(Point p1, Point p2) {
		double a = (double)(p1.getX() - p2.getX());
		double b = (double)(p1.getY() - p2.getY());
		return Math.sqrt(a*a + b*b);
	}
	
	/**
	 * Check if the line segment defined by the two Points is vertical.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is vertical
	 */
	public final static boolean isVertical(Point p1, Point p2) {
		return Math.abs(p1.getX() - p2.getX()) <= 2;
	}
	
	/**
	 * Check if the line segment defined by the two Points is horizontal.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is horizontal
	 */
	public final static boolean isHorizontal(Point p1, Point p2) {
		return Math.abs(p1.getY() - p2.getY()) <= 2;
	}

	/**
	 * Check if the line segment defined by the two Points is neither horizontal nor vertical.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is slanted
	 */
	public final static boolean isSlanted(Point p1, Point p2) {
		return !isHorizontal(p1, p2) && !isVertical(p1,p2);
	}

	public static Point getVertMidpoint(Point start, Point end, double fract) {
		Point m = GraphicsUtil.createPoint(start);
		int d = (int)(fract * (double)(end.getY() - start.getY()));
		m.setY(start.getY()+d);
		return m;
	}
	
	public static Point getHorzMidpoint(Point start, Point end, double fract) {
		Point m = GraphicsUtil.createPoint(start);
		int d = (int)(fract * (double)(end.getX() - start.getX()));
		m.setX(start.getX()+d);
		return m;
	}

	public static IDimension calculateSize(AnchorContainer shape) {
		GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
		if (ga!=null)
			return gaService.calculateSize(ga);
		
		IDimension dim = null;
		if (shape instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape)shape;
			for (Shape s : cs.getChildren()) {
				ga = s.getGraphicsAlgorithm();
				if (ga!=null) {
					IDimension d = gaService.calculateSize(ga);
					if (dim==null)
						dim = d;
					else {
						if (d.getWidth() > dim.getWidth())
							dim.setWidth(d.getWidth());
						if (d.getHeight() > dim.getHeight())
							dim.setHeight(d.getHeight());
					}
				}
			}
		}
		return dim;
	}
	
	public static boolean debug = false;

	public static void dump(String label, List<ContainerShape> shapes) {
		if (shapes!=null) {
			if (debug) {
				System.out.println(label);
				for (ContainerShape shape : shapes)
					dump(1, "",shape,0,0); //$NON-NLS-1$
				System.out.println(""); //$NON-NLS-1$
			}
		}
	}
	
	public static void dump(String label, Anchor anchor) {
		if (debug) {
			System.out.print(label+" "); //$NON-NLS-1$
			ILocation loc = peService.getLocationRelativeToDiagram(anchor);
			System.out.print(" at "+loc.getX()+", "+loc.getY()); //$NON-NLS-1$ //$NON-NLS-2$
			dump(" parent=", (ContainerShape)anchor.getParent()); //$NON-NLS-1$
			if (AnchorUtil.isBoundaryAnchor(anchor)) {
				String property = Graphiti.getPeService().getPropertyValue(
						anchor, AnchorUtil.BOUNDARY_FIXPOINT_ANCHOR);
				if (property != null && anchor instanceof FixPointAnchor) {
					System.out.println(" location="+AnchorLocation.getLocation(property)); //$NON-NLS-1$
				}
			}
		}
	}
	
	public static void dump(String label, ContainerShape shape) {
		dump(0, label,shape,0,0);
	}
	
	public static void dump(int level, String label, ContainerShape shape) {
		dump(level, label,shape,0,0);
	}
	
	public static void dump(int level, String label, ContainerShape shape, int x, int y) {
		if (debug) {
			String text = getDebugText(shape);
			for (int i=0; i<level; ++i)
				System.out.print("    "); //$NON-NLS-1$
			System.out.print(label+" "+text); //$NON-NLS-1$
			if (x>0 && y>0) {
				System.out.println(" at "+x+", "+y); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
				System.out.println(""); //$NON-NLS-1$
		}
	}
	
	public static String getDebugText(ContainerShape shape) {
		EObject be = BusinessObjectUtil.getBusinessObjectForPictogramElement(shape);
		String id = ""; //$NON-NLS-1$
		if (be instanceof BaseElement) {
			id = " " + ((BaseElement)be).getId(); //$NON-NLS-1$
		}
		String text = be.eClass().getName()+id+": "+ModelUtil.getDisplayName(be); //$NON-NLS-1$
		return text;
	}
	
	public static void dump(String label) {
		if (debug) {
			System.out.println(label);
		}
	}
	
	public static LineSegment[] getEdges(Shape shape) {
		ILocation loc = peService.getLocationRelativeToDiagram(shape);
		IDimension size = GraphicsUtil.calculateSize(shape);
		LineSegment top = new LineSegment(loc.getX(),loc.getY(),
				loc.getX()+size.getWidth(), loc.getY());
		LineSegment left = new LineSegment(loc.getX(),loc.getY(), loc.getX(),
				loc.getY()+size.getHeight());
		LineSegment bottom = new LineSegment(loc.getX(), loc.getY()+size.getHeight(),
				loc.getX()+size.getWidth(), loc.getY()+size.getHeight());
		LineSegment right = new LineSegment(loc.getX()+size.getWidth(), loc.getY(),
				loc.getX()+size.getWidth(), loc.getY()+size.getHeight());
		return new LineSegment[] {top, bottom, left, right};
	}
	
	public static LineSegment findNearestEdge(Shape shape, Point p) {
		LineSegment edges[] = getEdges(shape);
		LineSegment top = edges[0];
		LineSegment bottom = edges[1];
		LineSegment left = edges[2];
		LineSegment right = edges[3];
		double minDist;
		double dist;
		LineSegment result;
		
		minDist = top.getDistance(p);
		result = top;
		
		dist = bottom.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = bottom;
		}
		dist = left.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = left;
		}
		dist = right.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = right;
		}
		return result;
	}

	public static void sendToFront(Shape shape) {
		peService.sendToFront(shape);
		BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(shape, BPMNShape.class);
		if (bpmnShape!=null) {
			BPMNPlane plane = (BPMNPlane)bpmnShape.eContainer();
			plane.getPlaneElement().remove(bpmnShape);
			plane.getPlaneElement().add(bpmnShape);
		}
	}

	public static void sendToBack(Shape shape) {
		peService.sendToBack(shape);
		BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(shape, BPMNShape.class);
		if (bpmnShape!=null) {
			BPMNPlane plane = (BPMNPlane)bpmnShape.eContainer();
			plane.getPlaneElement().remove(bpmnShape);
			plane.getPlaneElement().add(0,bpmnShape);
		}
	}
}
