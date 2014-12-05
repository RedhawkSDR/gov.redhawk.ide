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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.AnchorUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.AnchorLocation;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.BusinessObjectUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.GraphicsUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.ModelUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection.AnchorUtil.BoundaryAnchor;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ConnectionRoute implements Comparable<ConnectionRoute>, Comparator<ConnectionRoute> {
		class Collision {
			Shape shape;
			Point start;
			Point end;
			
			public Collision(Shape shape, Point start, Point end) {
				this.shape = shape;
				this.start = start;
				this.end = end;
			}
			
			public String toString() {
				Object o = BusinessObjectUtil.getFirstBaseElement(shape);
				return ModelUtil.getDisplayName(o);
			}
		}
		
		class Crossing {
			Connection connection;
			Point start;
			Point end;
			public Crossing(Connection connection, Point start, Point end) {
				this.connection = connection;
				this.start = start;
				this.end = end;
			}
			
			public String toString() {
				Object o = BusinessObjectUtil.getFirstBaseElement(connection);
				return ModelUtil.getDisplayName(o);
			}
		}
		
		DefaultConnectionRouter router;
		int id;
		private List<Point> points = new ArrayList<Point>();
		List<Collision> collisions = new ArrayList<Collision>();
		List<Crossing> crossings = new ArrayList<Crossing>();
		Shape source;
		Shape target;
		boolean valid = true;
		private int rank = 0;
		
		public ConnectionRoute(DefaultConnectionRouter router, int id, Shape source, Shape target) {
			this.router = router;
			this.id = id;
			this.source = source;
			this.target = target;
		}

		public void apply(FreeFormConnection ffc) {
			apply(ffc,null,null);
		}
		
		public void apply(FreeFormConnection ffc, Anchor sourceAnchor, Anchor targetAnchor) {
			
			// set connection's source and target anchors if they are Boundary Anchors
			if (sourceAnchor==null) {
				BoundaryAnchor ba = AnchorUtil.findNearestBoundaryAnchor(source, this.get(0));
				sourceAnchor = ba.anchor;
				ffc.setStart(sourceAnchor);
			}
			
			if (targetAnchor==null) {
				// NOTE: a route with only a starting point indicates that it could not be calculated.
				// In this case, make the connection a straight line from source to target.
				Point p = this.get(this.size() - 1);
				BoundaryAnchor ba = AnchorUtil.findNearestBoundaryAnchor(target, p);
				targetAnchor = ba.anchor;
				ffc.setEnd(targetAnchor);
			}
			
			// add the bendpoints
			ffc.getBendpoints().clear();
			for (int i=1; i<this.size()-1; ++i) {
				ffc.getBendpoints().add(this.get(i));
			}
		}
		
		public String toString() {
			String text;
			if (isValid()) {
				BoundaryAnchor sa = AnchorUtil.findNearestBoundaryAnchor(source, get(0));
				BoundaryAnchor ta = AnchorUtil.findNearestBoundaryAnchor(target, get(size()-1));
				text = id+": length="+getLength()+" points="+getPoints().size()+ //$NON-NLS-1$ //$NON-NLS-2$
						" source="+sa.locationType+" target="+ta.locationType; //$NON-NLS-1$ //$NON-NLS-2$
				if (collisions.size()>0) {
					text += " collisions="; //$NON-NLS-1$
					Iterator<Collision> iter=collisions.iterator();
					while (iter.hasNext()) {
						Collision c = iter.next();
						text += "'" + c.toString() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
						if (iter.hasNext())
							text += ", "; //$NON-NLS-1$
					}
				}
				if (crossings.size()>0) {
					text += " crossings="; //$NON-NLS-1$
					Iterator<Crossing> iter=crossings.iterator();
					while (iter.hasNext()) {
						Crossing c = iter.next();
						text += "'" + c.toString() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
						if (iter.hasNext())
							text += ", "; //$NON-NLS-1$
					}
				}
			}
			else
				text = "not valid"; //$NON-NLS-1$
			return text;
		}
		
		public boolean add(Point newPoint) {
			for (Point p : getPoints()) {
				if (GraphicsUtil.pointsEqual(newPoint, p)) {
					valid = false;
					return false;
				}
			}
			getPoints().add(newPoint);
			return true;
		}
		
		public Point get(int index) {
			return getPoints().get(index);
		}
		
		public int size() {
			return getPoints().size();
		}
		
		public void addCollision(Shape shape, Point start, Point end) {
			collisions.add( new Collision(shape, start, end) );
		}
		
		public void addCrossing(Connection connection, Point start, Point end) {
			crossings.add( new Crossing(connection, start, end) );
		}
		
		public boolean isValid() {
			if (valid)
				return getLength() < Integer.MAX_VALUE;
			return false;
		}
		
		public int getLength() {
			int length = 0;
			if (getPoints().size()>1) {
				Point p1 = getPoints().get(0);
				for (int i=1; i<getPoints().size(); ++i) {
					Point p2 = getPoints().get(i);
//					if (isHorizontal(p1,p2) || isVertical(p1,p2))
						length += (int)GraphicsUtil.getLength(p1, p2);
//					else 
//						return Integer.MAX_VALUE;
					p1 = p2;
				}
			}
			else {
				// this route could not be calculated
				return Integer.MAX_VALUE;
			}
			return length;
		}

		@Override
		public int compareTo(ConnectionRoute arg0) {
			return compare(this,arg0);
		}

		@Override
		public int compare(ConnectionRoute o1, ConnectionRoute o2) {
			if (o1.isValid()) {
				if (o2.isValid()) {
					int i = o1.getRank() - o2.getRank();
					if (i==0) {
						i = o1.collisions.size() - o2.collisions.size();
						if (i==0) {
//							i = o1.crossings.size() - o2.crossings.size();
							if (i==0) {
								i = o1.getLength() - o2.getLength();
								if (Math.abs(i)<=10) {
									i = o1.getPoints().size() - o2.getPoints().size();
//									if (i==0) {
//										BoundaryAnchor ba1 = AnchorUtil.findNearestBoundaryAnchor(source, o1.get(0));
//										BoundaryAnchor ba2 = AnchorUtil.findNearestBoundaryAnchor(source, o2.get(0));
//
//										i = AnchorLocation.valueOf(ba1.locationType) - (int)ba2.locationType;
//									}
								}
							}
						}
					}
					return i;
				}
				return -1;
			}
			else if (!o2.isValid())
				return 0;
			return 1;
		}

		private boolean removeUnusedPoints() {
			boolean changed = false;

			Point p1 = getPoints().get(0);
			for (int i=1; i<getPoints().size()-1; ++i) {
				Point p2 = getPoints().get(i);
				if (i+1 < getPoints().size()) {
					// remove unnecessary bendpoints: two consecutive
					// horizontal or vertical line segments
					Point p3 = getPoints().get(i+1);
					int x1 = p1.getX();
					int x2 = p2.getX();
					int x3 = p3.getX();
					int y1 = p1.getY();
					int y2 = p2.getY();
					int y3 = p3.getY();
					if (
							(GraphicsUtil.isVertical(p1,p2) && GraphicsUtil.isVertical(p2,p3) && ((y1<y2 && y2<y3) || y1>y2 && y2>y3)) ||
							(GraphicsUtil.isHorizontal(p1,p2) && GraphicsUtil.isHorizontal(p2,p3) && ((x1<x2 && x2<x3) || x1>x2 && x2>x3))
					) {
						getPoints().remove(i);
						// look at these set of points again
						--i;
						changed = true;
					}
				}
				p1 = p2;
			}
			return changed;
		}
		
		private boolean removeUnusedSegments() {
			boolean changed = false;

			// remove unnecessary "U" shapes
			Point p1 = getPoints().get(0);
			for (int i=1; i<getPoints().size()-1; ++i) {
				Point p2 = getPoints().get(i);
				if (i+2 < getPoints().size()) {
					Point p3 = getPoints().get(i+1);
					Point p4 = getPoints().get(i+2);
					if (GraphicsUtil.isHorizontal(p1,p2) && GraphicsUtil.isVertical(p2,p3) && GraphicsUtil.isHorizontal(p3,p4)) {
						int x1 = p1.getX();
						int x2 = p2.getX();
						int x4 = p4.getX();
						if ((x1 < x4 && x4 < x2) || (x1 > x4 && x4 > x2)) {
							// this forms a vertical "U" - remove if the new configuration does not cause a collision
							Point p = GraphicsUtil.createPoint(x4, p2.getY());
							if (router.getCollision(p,p4)==null) {
								getPoints().set(i, p);
								getPoints().remove(p3);
								--i;
								changed = true;
							}
						}
					}
					else if (GraphicsUtil.isVertical(p1,p2) && GraphicsUtil.isHorizontal(p2,p3) && GraphicsUtil.isVertical(p3,p4)) {
						int y1 = p1.getY();
						int y2 = p2.getY();
						int y4 = p4.getY();
						if ((y1 < y4 && y4 < y2) || (y1 > y4 && y4 > y2)) {
							// this forms a horizontal "U"
							Point p = GraphicsUtil.createPoint(p2.getX(), y4);
							if (router.getCollision(p,p4)==null) {
								getPoints().set(i, p);
								getPoints().remove(p3);
								--i;
								changed = true;
							}
						}
					}
				}
				p1 = p2;
			}
			
			// remove "T" shapes
			p1 = getPoints().get(0);
			for (int i=1; i<getPoints().size()-1; ++i) {
				Point p2 = getPoints().get(i);
				if (i+1 < getPoints().size()) {
					Point p3 = getPoints().get(i+1);
					if (p1.getX() == p2.getX() && p2.getX() == p3.getX()) {
						if (	(p2.getY() < p1.getY() && p2.getY() < p3.getY()) ||
								(p2.getY() > p1.getY() && p2.getY() > p3.getY())
						) {
							getPoints().remove(p2);
							--i;
							changed = true;
						}
					}
					else if (p1.getY() == p2.getY() && p2.getY() == p3.getY()) {
						if (	(p2.getX() < p1.getX() && p2.getX() < p3.getX()) ||
								(p2.getX() > p1.getX() && p2.getX() > p3.getX())
						) {
							getPoints().remove(p2);
							--i;
							changed = true;
						}
					}
				}
				p1 = p2;
			}
			return changed;
		}
		
		public boolean optimize() {
			boolean changed = removeUnusedPoints();
			if (removeUnusedSegments()) {
				// this may cause some unused points to be left over
				removeUnusedPoints();
				changed = true;
			}
			return changed;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public List<Point> getPoints() {
			return points;
		}

		public void setPoints(List<Point> points) {
			this.points = points;
		}
	}
