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
package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import java.util.Collection;

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.swt.graphics.Color;

public class StyleUtil { //SUPPRESS CHECKSTYLE INLINE

	public static final IColorConstant TEXT_FOREGROUND = IColorConstant.BLACK;
	public static final IColorConstant WHITE = new ColorConstant(255, 255, 255);
	public static final IColorConstant GOLD = new ColorConstant(255, 218, 105);
	public static final IColorConstant BLACK = new ColorConstant(0, 0, 0);
	public static final IColorConstant RED = new ColorConstant(255, 0, 0);
	public static final IColorConstant BLUE = new ColorConstant(0, 0, 194);
	public static final IColorConstant COMPONENT_FOREGROUND = new ColorConstant(98, 131, 167);
	public static final IColorConstant COMPONENT_BACKGROUND = new ColorConstant(187, 218, 247);
	public static final IColorConstant OUTER_CONTAINER_BACKGROUND = new ColorConstant(250, 250, 250);

	// COMPONENT
	public static final int DEFAULT_LINE_WIDTH = 2;
	public static final int ASSEMBLY_CONTROLLER_LINE_WIDTH = 3;
	public static final Color FOREGROUND_COLOR = new Color(null, 116, 130, 141); // TODO shouldn't we be disposing of
																					// these correctly?
	public static final Color COMPONENT_IDLE_COLOR = new Color(null, 219, 233, 246); // TODO shouldn't we be disposing
																						// of these correctly?
	public static final Color COMPONENT_STARTED_COLOR = new Color(null, 186, 234, 173); // TODO shouldn't we be
																						// disposing of these correctly?
	public static final Color DEFAULT_COMPONENT_COLOR = new Color(null, 176, 176, 176); // TODO shouldn't we be
																						// disposing of these correctly?
	public static final Color ASSEMBLY_CONTROLLER_COLOR = new Color(null, 255, 218, 105); // TODO shouldn't we be
																							// disposing of these
																							// correctly?
	// public static final Font START_ORDER_FONT = new Font(null, "Arial", 12, SWT.BOLD); // TODO shouldn't we be
	// disposing of these correctly?

	// FONTS
	// private final static String ARIAL_FONT = "Arial";
	private static final String SANS_FONT = "Sans";
	private static final String DEFAULT_FONT = SANS_FONT;

	public static final Font getOuterTitleFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 8, false, true);
	}

	public static final Font getInnerTitleFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 11, false, false);
	}

	public static final Font getErrorConnectionFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 8, false, false);
	}

	public static final Font getStartOrderFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 8, false, false);
	}

	public static final Font getPortFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 8, false, false);
	}

	// returns component outer rectangle style
	public static Style getStyleForComponentOuter(Diagram diagram) {
		final String styleId = "ComponentOuter";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setTransparency(.09d);
			style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
			style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
			style.setLineWidth(0);
			style.setLineVisible(false);
		}
		return style;
	}

	// returns component inner rectangle style
	public static Style getStyleForComponentInner(Diagram diagram) {
		final String styleId = "ComponentInner";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteAdaptions());
			style.setLineWidth(2);
		}
		return style;
	}

	// returns findby outer rectangle style
	public static Style getStyleForFindByOuter(Diagram diagram) {
		final String styleId = "FindByOuter";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setTransparency(.99d);
			style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
			style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
			style.setLineWidth(0);
			style.setLineVisible(false);
		}
		return style;
	}

	// returns host collocation rectangle style
	public static Style getStyleForHostCollocation(Diagram diagram) {
		final String styleId = "HostCollocation";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setTransparency(.99d);
			style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
			style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
			style.setLineWidth(1);
			style.setLineVisible(true);
		}
		return style;
	}

	// returns find by inner rectangle style
	public static Style getStyleForFindByInner(Diagram diagram) {
		final String styleId = "FindByInner";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setBackground(gaService.manageColor(diagram, new ColorConstant(255, 0, 0)));
			style.setLineStyle(LineStyle.DASH);
			gaService.setRenderingStyle(style, FindByColoredAreas.getCopperWhiteAdaptions());
			style.setLineWidth(2);
		}
		return style;
	}

	// returns outer text style
	public static Style getStyleForOuterText(Diagram diagram) {
		final String styleId = "OuterText";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setFont(getOuterTitleFont(diagram));
			style.setLineWidth(2);
		}
		return style;
	}

	// returns inner text style
	public static Style getStyleForInnerText(Diagram diagram) {
		final String styleId = "InnerText";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setFont(getInnerTitleFont(diagram));
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setLineWidth(2);
		}
		return style;
	}

	// returns error message font for improper connections
	public static Style getStyleForErrorTextConnections(Diagram diagram) {
		final String styleId = "ErrorTextConnections";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setFont(getErrorConnectionFont(diagram));
			style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
			style.setForeground(gaService.manageColor(diagram, RED));
			style.setLineWidth(2);
		}
		return style;
	}

	// returns style for provides port
	public static Style getStyleForProvidesPort(Diagram diagram) {
		final String styleId = "ProvidesPort";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, WHITE));
			style.setFont(getPortFont(diagram));
			style.setLineWidth(2);
			style.setLineVisible(true);
		}
		return style;
	}

	// returns style for provides port
	public static Style getStyleForExternalProvidesPort(Diagram diagram) {
		final String styleId = "ExternalProvidesPort";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, BLUE));
			style.setFont(getPortFont(diagram));
			style.setLineWidth(2);
			style.setLineVisible(true);
		}
		return style;
	}

	// returns style for uses port
	public static Style getStyleForUsesPort(Diagram diagram) {
		final String styleId = "UsesPort";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, BLACK));
			style.setFont(getPortFont(diagram));
			style.setLineWidth(2);
			style.setLineVisible(true);
		}
		return style;
	}

	// returns style for uses external port
	public static Style getStyleForExternalUsesPort(Diagram diagram) {
		final String styleId = "UsesExternalPort";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, BLUE));
			style.setFont(getPortFont(diagram));
			style.setLineWidth(2);
			style.setLineVisible(true);
		}
		return style;
	}

	// returns style for uses port
	public static Style getStyleForUsesPortAnchor(Diagram diagram) {
		final String styleId = "UsesPortAnchor";
		Style style = findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setTransparency(100d);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, BLACK));
			style.setFont(getPortFont(diagram));
			style.setLineWidth(2);
			style.setLineVisible(true);
		}
		return style;
	}

//	//returns style for provides port
//	public static Style getStyleForProvidesPortAnchor(Diagram diagram){
//		final String styleId = "ProvidesPortAnchor";
//		Style style = findStyle(diagram, styleId);
//		
//		if(style == null){
//			IGaService gaService = Graphiti.getGaService();
//			style = gaService.createStyle(diagram, styleId);
//			style.setTransparency(100d);
//			style.setForeground(gaService.manageColor(diagram, BLACK));
//			style.setBackground(gaService.manageColor(diagram, WHITE));
//			style.setFont(getPortFont(diagram));
//			style.setLineWidth(2);
//			style.setLineVisible(true);
//		}
//		return style;
//	}

	// returns style for lollipop ellipse
	public static Style getStyleForLollipopEllipse(Diagram diagram) {
		final String styleId = "LollipopEllipse";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setLineWidth(1);
			style.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
			style.setTransparency(.99d);
		}
		return style;
	}

	// returns style for lollipop line
	public static Style getStyleForLollipopLine(Diagram diagram) {
		final String styleId = "LollipopLine";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setBackground(Graphiti.getGaService().manageColor(diagram, BLACK));
		}
		return style;
	}

	public static Style getStyleForStartOrderAssemblyControllerEllipse(Diagram diagram) {
		final String styleId = "StartOrderAssemblyControllerEllipse";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setLineWidth(1);
			style.setBackground(Graphiti.getGaService().manageColor(diagram, GOLD));
			style.setTransparency(.99d);
		}
		return style;
	}

	public static Style getStyleForStartOrderEllipse(Diagram diagram) {
		final String styleId = "StartOrderEllipse";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setLineWidth(1);
			style.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
			style.setTransparency(.99d);
		}
		return style;
	}

	public static Style getStyleForStartOrderText(Diagram diagram) {
		final String styleId = "StartOrderText";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, WHITE));
			style.setFont(getStartOrderFont(diagram));
		}
		return style;
	}

	// returns component text style
	public static Style getStyleForPortText(Diagram diagram) {
		final String styleId = "ComponentText";
		Style style = findStyle(diagram, styleId);

		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BLACK));
			style.setBackground(gaService.manageColor(diagram, WHITE));
			style.setFont(getOuterTitleFont(diagram));
		}
		return style;
	}

	// find the style with given id in style-container
	private static Style findStyle(StyleContainer styleContainer, String id) {
		// find and return style
		Collection<Style> styles = styleContainer.getStyles();
		if (styles != null) {
			for (Style style : styles) {
				if (id.equals(style.getId())) {
					return style;
				}
			}
		}
		return null;
	}

	private static Diagram findDiagram(ContainerShape containerShape) {
		return Graphiti.getPeService().getDiagramForShape(containerShape);
	}

}
