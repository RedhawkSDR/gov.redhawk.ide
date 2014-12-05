/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Calculates detour cuts for a given shape. These cuts surround the shape at each of the corners
 * of the shape's bounding rectangle, but "just outside" the shape.
 */
public class DetourPoints {
	public int leftMargin = 10;
	public int rightMargin = 10;
	public int topMargin = 10;
	public int bottomMargin = 10;
	public Point topLeft;
	public Point topRight;
	public Point bottomLeft;
	public Point bottomRight;
	
	public DetourPoints(ContainerShape shape) {
		calculate(shape);
	}
	
	public DetourPoints(ContainerShape shape, int margin) {
		this(shape,margin,margin,margin,margin);
	}
	
	public DetourPoints(ContainerShape shape, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
		calculate(shape);
	}
	
	protected void calculate(Shape shape) {
		ILocation loc = BendpointConnectionRouter.peService.getLocationRelativeToDiagram(shape);
		IDimension size = GraphicsUtil.calculateSize(shape);
		topLeft = GraphicsUtil.createPoint(loc.getX() - leftMargin, loc.getY() - topMargin);
		topRight = GraphicsUtil.createPoint(loc.getX() + size.getWidth() + rightMargin, loc.getY() - topMargin);
		bottomLeft = GraphicsUtil.createPoint(loc.getX() - leftMargin, loc.getY() + size.getHeight() + bottomMargin);
		bottomRight = GraphicsUtil.createPoint(loc.getX() + size.getWidth() + leftMargin, loc.getY() + size.getHeight() + bottomMargin);
	}

	public Point getNearest(Point p) {
		Point nearest = topLeft;
		int dmin = (int)GraphicsUtil.getLength(p, topLeft);
		if (dmin==0)
			// this isn't it
			dmin = Integer.MAX_VALUE;
		
		int d = (int)GraphicsUtil.getLength(p, topRight);
		if (d<dmin && d>0) {
			dmin = d;
			nearest = topRight;
		}
		d = (int)GraphicsUtil.getLength(p, bottomLeft);
		if (d<dmin && d>0) {
			dmin = d;
			nearest = bottomLeft;
		}
		d = (int)GraphicsUtil.getLength(p, bottomRight);
		if (d<dmin && d>0) {
			dmin = d;
			nearest = bottomRight;
		}
		
		return nearest; 
	}
	
	protected Point getDiagonal(Point p) {
		if (p==topLeft)
			return bottomRight;
		if (p==topRight)
			return bottomLeft;
		if (p==bottomLeft)
			return topRight;
		if (p==bottomRight)
			return topLeft;
		return null;
	}

	protected Point getHorzOpposite(Point p) {
		if (p==topLeft)
			return topRight;
		if (p==topRight)
			return topLeft;
		if (p==bottomLeft)
			return bottomRight;
		if (p==bottomRight)
			return bottomLeft;
		return null;
	}

	protected Point getVertOpposite(Point p) {
		if (p==topLeft)
			return bottomLeft;
		if (p==topRight)
			return bottomRight;
		if (p==bottomLeft)
			return topLeft;
		if (p==bottomRight)
			return topRight;
		return null;
	}
	
	protected boolean isTop(Point p) {
		return p==topLeft || p==topRight;
	}
	
	protected boolean isLeft(Point p) {
		return p==topLeft || p==bottomLeft;
	}
	
	protected int getSector(Point p) {
		int px = p.getX();
		int py = p.getY();
		int TLX = topLeft.getX();
		int TLY = topLeft.getY();
		int TRX = topRight.getX();
		int TRY = topRight.getY();
		int BLX = bottomLeft.getX();
		int BLY = bottomLeft.getY();
		int BRX = bottomRight.getX();
		int BRY = bottomRight.getY();

		if (
				(px<TLX && py<TLY) ||
				(px>TRX && py<TRY) ||
				(px<BLX && py>BLY) ||
				(px>BRX && py>BRY)
		) {
			return 1;
		}
		if (px>=TLX && px<=TRX) {
			if (py<=TLY)
				return 2;
			else if (py>=BLY)
				return 3;
		}
		int m = (BLY - TLY) / 2;
		if (TLY<=py && py<=TLY+m)
			return 3;
		if (TLY+m<py && py<=BLY)
			return 2;
		
		throw new IllegalArgumentException("Logic error in getSector()"); //$NON-NLS-1$
	}
	
	public List<Point> calculateDetour(Point p1, Point p2) {
		List<Point> detour = new ArrayList<Point>();
		
		Point np1 = getNearest(p1);
		Point np2 = getNearest(p2);

		if (np2 == np1){
			detour.add(np1);
		}
		else if (np2 == getDiagonal(np1)) {
			int s1 = getSector(p1);
			int s2 = getSector(p2);
			if (s1==1) {
				if (s2==1) {
					// 1 -> 1
					double d1, d2;
					if (np1==topLeft || np1==bottomRight) {
						d1 = GraphicsUtil.getLength(p1, topRight) + GraphicsUtil.getLength(topRight, p2);
						d2 = GraphicsUtil.getLength(p1, bottomLeft) + GraphicsUtil.getLength(bottomLeft, p2);
						if (d1<d2)
							detour.add(topRight);
						else
							detour.add(bottomLeft);
					}
					else {
						d1 = GraphicsUtil.getLength(p1, topLeft) + GraphicsUtil.getLength(topLeft, p2);
						d2 = GraphicsUtil.getLength(p1, bottomRight) + GraphicsUtil.getLength(bottomRight, p2);
						if (d1<d2)
							detour.add(topLeft);
						else
							detour.add(bottomRight);
					}
				}
				else if (s2==2) {
					// 1 -> 2
					if (np1==topLeft || np1==bottomRight)
						detour.add(topRight);
					else
						detour.add(topLeft);
				}
				else {
					// 1 -> 3
					if (np1==topLeft || np1==bottomRight) {
						detour.add(bottomLeft);
					}
					else {
						detour.add(bottomRight);
					}
				}
			}
			else if (s1==2) {
				if (s2==1) {
					// 2 -> 1
					if (np1==topLeft || np1==bottomRight)
						detour.add(topRight);
					else
						detour.add(topLeft);
				}
				else if (s2==2) {
					// 2 -> 2
					if (np1==topLeft || np1==bottomRight)
						detour.add(topRight);
					else
						detour.add(topLeft);
				}
				else {
					// 2 -> 3
					if (np1==topLeft) {
						detour.add(topRight);
						detour.add(bottomRight);
					}
					else if (np1==topRight) {
						detour.add(topRight);
						detour.add(bottomRight);
					}
					else if (np1==bottomRight) {
						detour.add(topRight);
						detour.add(topLeft);
					}
					else {
						detour.add(topLeft);
						detour.add(topRight);
					}
				}
			}
			else {
				if (s2==1) {
					// 3 -> 1
					if (np1==topLeft || np1==bottomRight)
						detour.add(bottomLeft);
					else
						detour.add(bottomRight);
				}
				else if (s2==2) {
					// 3 -> 2
					if (np1==topLeft) {
						detour.add(topLeft);
						detour.add(topRight);
					}
					else if ( np1==topRight) {
						detour.add(topRight);
						detour.add(topLeft);
					}
					else if (np1==bottomRight) {
						detour.add(topRight);
						detour.add(topLeft);
					}
					else {
						detour.add(bottomLeft);
						detour.add(topLeft);
					}
				}
				else {
					// 3 -> 3
					if (np1==topLeft || np1==bottomRight)
						detour.add(bottomLeft);
					else
						detour.add(bottomRight);
				}
			}
		}
		else if (np2 == getHorzOpposite(np1)) {
			if (isTop(np2)) {
				// check top edge
				if (p1.getY()>topLeft.getY() && p2.getY()>topLeft.getY()) {
					// both cuts below top edge
					detour.add(np1);
					detour.add(np2);
				}
				else if (p1.getY()>topLeft.getY())
					// only p1 is below top edge
					detour.add(np1);
				else
					// only p2 is below top edge
					detour.add(np2);
			}
			else {
				// check bottom edge
				if (p1.getY()<bottomLeft.getY() && p2.getY()<bottomLeft.getY()) {
					// both cuts above bottom edge
					detour.add(np1);
					detour.add(np2);
				}
				else if (p1.getY()<bottomLeft.getY())
					// only p1 is above bottomedge
					detour.add(np1);
				else
					// only p2 is below top edge
					detour.add(np2);
			}
		}
		else if (np2 == getVertOpposite(np1)) {
			if (isLeft(np2)) {
				// check left edge
				if (p1.getX()>topLeft.getX() && p2.getX()>topLeft.getX()) {
					// both cuts to right of left edge
					detour.add(np1);
					detour.add(np2);
				}
				else if (p1.getX()>topLeft.getX())
					detour.add(np1);
				else
					detour.add(np2);
			}
			else {
				// check right edge
				if (p1.getX()<topRight.getX() && p2.getX()<topRight.getX()) {
					// both cuts to right of left edge
					detour.add(np1);
					detour.add(np2);
				}
				else if (p1.getX()<topRight.getX())
					detour.add(np1);
				else
					detour.add(np2);
			}
		}

		return detour;
	}
	
	public boolean intersects(DetourPoints d2) {
		return GraphicsUtil.intersects(
				this.topLeft.getX(), this.topLeft.getY(), this.topRight.getX() - this.topLeft.getX(), this.bottomLeft.getY() - this.topLeft.getY(),
				d2.topLeft.getX(), d2.topLeft.getY(), d2.topRight.getX() - d2.topLeft.getX(), d2.bottomLeft.getY() - d2.topLeft.getY()
		);
	}
	
	public boolean contains(DetourPoints d2) {
		return	this.topLeft.getX()<=d2.topLeft.getX() &&
				this.topRight.getX()>=d2.topRight.getX() &&
				this.topLeft.getY()<=d2.topLeft.getY() && 
				this.bottomLeft.getY()>=d2.bottomLeft.getY(); 
	}
	
	public void merge(DetourPoints d2) {
		this.topLeft.setX( Math.min(this.topLeft.getX(), d2.topLeft.getX()) );
		this.topLeft.setY( Math.min(this.topLeft.getY(), d2.topLeft.getY()) );
		this.topRight.setX( Math.max(this.topRight.getX(), d2.topRight.getX()) );
		this.topRight.setY( Math.min(this.topRight.getY(), d2.topRight.getY()) );
		this.bottomLeft.setX( Math.min(this.bottomLeft.getX(), d2.bottomLeft.getX()) );
		this.bottomLeft.setY( Math.max(this.bottomLeft.getY(), d2.bottomLeft.getY()) );
		this.bottomRight.setX( Math.max(this.bottomRight.getX(), d2.bottomRight.getX()) );
		this.bottomRight.setY( Math.max(this.bottomRight.getY(), d2.bottomRight.getY()) );
	}
}
