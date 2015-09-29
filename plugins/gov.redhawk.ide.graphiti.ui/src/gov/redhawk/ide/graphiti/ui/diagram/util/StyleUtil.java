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
package gov.redhawk.ide.graphiti.ui.diagram.util;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.sca.util.PluginUtil;

public class StyleUtil { // SUPPRESS CHECKSTYLE INLINE

	public static final String START_ORDER_TEXT = "gov.redhawk.style.StartOrderText";
	public static final String START_ORDER_ELLIPSE = "gov.redhawk.style.StartOrderEllipse";
	public static final String ASSEMBLY_CONTROLLER_ELLIPSE = "gov.redhawk.style.StartOrderAssemblyControllerEllipse";
	public static final String LOLLIPOP = "gov.redhawk.style.Lollipop";
	public static final String USES_PORT = "gov.redhawk.style.UsesPort";
	public static final String EXTERNAL_USES_PORT = "gov.redhawk.style.UsesExternalPort";
	public static final String SUPER_USES_PORT = "gov.redhawk.style.SuperUsesPort";
	public static final String PROVIDES_PORT = "gov.redhawk.style.ProvidesPort";
	public static final String EXTERNAL_PROVIDES_PORT = "gov.redhawk.style.ExternalProvidesPort";
	public static final String SUPER_PROVIDES_PORT = "gov.redhawk.style.SuperProvidesPort";
	public static final String INNER_TEXT = "gov.redhawk.style.InnerText";
	public static final String OUTER_TEXT = "gov.redhawk.style.OuterText";
	public static final String HOST_COLLOCATION = "gov.redhawk.style.HostCollocation";
	public static final String FIND_BY_INNER = "gov.redhawk.style.FindByInner";
	public static final String FIND_BY_OUTER = "gov.redhawk.style.FindByOuter";
	public static final String USES_DEVICE_INNER = "gov.redhawk.style.UsesDeviceInner";
	public static final String USES_DEVICE_OUTER = "gov.redhawk.style.UsesDeviceOuter";
	public static final String COMPONENT_INNER = "gov.redhawk.style.ComponentInner";
	public static final String COMPONENT_INNER_STARTED = "gov.redhawk.style.ComponentInnerStarted";
	public static final String COMPONENT_INNER_ERROR = "gov.redhawk.style.ComponentInnerError";
	public static final String COMPONENT_INNER_DISABLED = "gov.redhawk.style.ComponentInnerDisabled";
	public static final String COMPONENT_OUTER = "gov.redhawk.style.ComponentOuter";
	public static final String PORT_STYLE_COMPATIBLE = "gov.redhawk.style.PortCompatible";
	public static final String PORT_STYLE_OK = "gov.redhawk.style.PortOK";
	public static final String PORT_STYLE_WARN1 = "gov.redhawk.style.PortWarning1";
	public static final String PORT_STYLE_WARN2 = "gov.redhawk.style.PortWarning2";
	public static final String PORT_STYLE_WARN3 = "gov.redhawk.style.PortWarning3";
	public static final String PORT_STYLE_WARN4 = "gov.redhawk.style.PortWarning4";

	public static final IColorConstant WHITE = IColorConstant.WHITE;
	public static final IColorConstant BLACK = IColorConstant.BLACK;
	public static final IColorConstant BLUE = new ColorConstant(0, 0, 194);
	public static final IColorConstant GOLD = new ColorConstant(255, 218, 105);
	public static final IColorConstant OUTER_CONTAINER_BACKGROUND = new ColorConstant(250, 250, 250);

	// Colors for connection state
	public static final IColorConstant COLOR_OK = IColorConstant.GREEN;
	public static final IColorConstant COLOR_WARN = IColorConstant.YELLOW;

	// Colors for port statistics feature
	private static final IColorConstant PORT_OK = COLOR_OK;
	private static final IColorConstant PORT_WARNING_1 = COLOR_WARN;
	private static final IColorConstant PORT_WARNING_2 = new ColorConstant(255, 170, 0);
	private static final IColorConstant PORT_WARNING_3 = new ColorConstant(255, 85, 0);
	private static final IColorConstant PORT_WARNING_4 = IColorConstant.RED;

	// Colors for port connection helpers
	private static final IColorConstant COMPATIBLE_PORT = COLOR_OK;

	// COMPONENT
	public static final int DEFAULT_LINE_WIDTH = 2;
	public static final int ASSEMBLY_CONTROLLER_LINE_WIDTH = 3;

	// FONTS
	private static final String SANS_FONT = "Sans";
	private static final String DEFAULT_FONT = SANS_FONT;

	public static void createAllStyles(Diagram diagram) {
		createStyleForComponentOuter(diagram);
		createStyleForComponentInner(diagram);
		createStyleForComponentInnerStarted(diagram);
		createStyleForComponentInnerError(diagram);
		createStyleForComponentInnerDisabled(diagram);
		createStyleForHostCollocation(diagram);
		createStyleForFindByOuter(diagram);
		createStyleForFindByInner(diagram);
		createStyleForUsesDeviceOuter(diagram);
		createStyleForUsesDeviceInner(diagram);
		createStyleForOuterText(diagram);
		createStyleForInnerText(diagram);

		createStyleForLollipop(diagram);

		createStyleForStartOrderAssemblyControllerEllipse(diagram);
		createStyleForStartOrderEllipse(diagram);
		createStyleForStartOrderText(diagram);

		createStyleForUsesPort(diagram);
		createStyleForExternalUsesPort(diagram);
		createStyleForSuperUsesPort(diagram);
		createStyleForProvidesPort(diagram);
		createStyleForExternalProvidesPort(diagram);
		createStyleForSuperProvidesPort(diagram);

		createStyleForCompatiblePort(diagram);
		createStyleForPortOK(diagram);
		createStyleForPortWarning1(diagram);
		createStyleForPortWarning2(diagram);
		createStyleForPortWarning3(diagram);
		createStyleForPortWarning4(diagram);
	}

	private static Diagram getStyleDiagram(EObject object) {
		URI uri = URI.createPlatformPluginURI(GraphitiUIPlugin.PLUGIN_ID + "/style", false);
		Resource resource = object.eResource().getResourceSet().getResource(uri, true);
		return (Diagram) resource.getContents().get(0);
	}

	public static void setStyle(GraphicsAlgorithm ga, String styleId) {
		Diagram styleDiagram = getStyleDiagram(ga);
		ga.setStyle(findStyle(styleDiagram, styleId));
	}
	
	// returns component outer rectangle style
	private static Style createStyleForComponentOuter(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, COMPONENT_OUTER);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
		style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
		style.setLineWidth(0);
		style.setLineVisible(false);
		return style;
	}

	// returns component inner rectangle style
	private static Style createStyleForComponentInner(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, COMPONENT_INNER);
		gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteAdaptions());
		style.setLineWidth(2);
		return style;
	}

	// updates component inner rectangle style
	private static Style createStyleForComponentInnerStarted(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, COMPONENT_INNER_STARTED);
		gaService.setRenderingStyle(style, RHContainerColoredAreas.getGreenWhiteAdaptions());
		style.setLineWidth(2);
		return style;
	}

	// updates component inner rectangle style when it is in an error state
	private static Style createStyleForComponentInnerError(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, COMPONENT_INNER_ERROR);
		gaService.setRenderingStyle(style, RHContainerColoredAreas.getYellowWhiteAdaptions());
		style.setLineWidth(2);
		return style;
	}

	// updates component inner rectangle style when it is in a disabled state
	private static Style createStyleForComponentInnerDisabled(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, COMPONENT_INNER_DISABLED);
		gaService.setRenderingStyle(style, PredefinedColoredAreas.getLightGrayAdaptions());
		style.setLineWidth(2);
		return style;
	}

	private static Style createStyleForFindByOuter(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, FIND_BY_OUTER);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setTransparency(.99d);
		style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
		style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
		style.setLineWidth(0);
		style.setLineVisible(false);
		return style;
	}

	private static Style createStyleForFindByInner(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, FIND_BY_INNER);
		style.setBackground(gaService.manageColor(diagram, new ColorConstant(255, 0, 0)));
		style.setLineStyle(LineStyle.DASH);
		gaService.setRenderingStyle(style, FindByColoredAreas.getCopperWhiteAdaptions());
		style.setLineWidth(2);
		return style;
	}

	// returns uses device outer rectangle style
	private static Style createStyleForUsesDeviceOuter(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, USES_DEVICE_OUTER);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setTransparency(.99d);
		style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
		style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
		style.setLineWidth(0);
		style.setLineVisible(false);
		return style;
	}

	// returns uses device inner rectangle style
	private static Style createStyleForUsesDeviceInner(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, USES_DEVICE_INNER);
		style.setBackground(gaService.manageColor(diagram, new ColorConstant(255, 0, 0)));
		style.setLineStyle(LineStyle.DASH);
		gaService.setRenderingStyle(style, FindByColoredAreas.getLightGrayAdaptions());
		style.setLineWidth(2);
		return style;
	}

	// returns host collocation rectangle style
	private static Style createStyleForHostCollocation(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, HOST_COLLOCATION);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setTransparency(.99d);
		style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
		style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
		style.setLineWidth(1);
		style.setLineVisible(true);
		return style;
	}

	// returns outer text style
	private static Style createStyleForOuterText(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, OUTER_TEXT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		Font font = gaService.manageFont(diagram, DEFAULT_FONT, 8, false, true);
		style.setFont(font);
		style.setLineWidth(2);
		return style;
	}

	// returns inner text style
	private static Style createStyleForInnerText(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, INNER_TEXT);
		Font font = gaService.manageFont(diagram, DEFAULT_FONT, 11, false, false);
		style.setFont(font);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setLineWidth(2);
		return style;
	}

	public static boolean needsUpdateForProvidesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(PROVIDES_PORT, style.getId());
		return !result;
	}

	private static final Font getPortFont(Diagram diagram) {
		return Graphiti.getGaService().manageFont(diagram, DEFAULT_FONT, 8, false, false);
	}

	// returns style for provides port
	private static Style createStyleForProvidesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PROVIDES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, WHITE));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	/**
	 * Style for a port which is compatible as the other end of a connection.
	 * @param diagram
	 * @return
	 */
	private static Style createStyleForCompatiblePort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_COMPATIBLE);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, COMPATIBLE_PORT));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for port statistics - no errors
	private static Style createStyleForPortOK(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_OK);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, PORT_OK));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for port statistics - error level 1
	private static Style createStyleForPortWarning1(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_WARN1);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, PORT_WARNING_1));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for port statistics - error level 2
	private static Style createStyleForPortWarning2(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_WARN2);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, PORT_WARNING_2));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for port statistics - error level 3
	private static Style createStyleForPortWarning3(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_WARN3);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, PORT_WARNING_3));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for port statistics - error level 4
	private static Style createStyleForPortWarning4(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, PORT_STYLE_WARN4);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, PORT_WARNING_4));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	public static boolean needsUpdateForExternalProvidesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(EXTERNAL_PROVIDES_PORT, style.getId());
		return !result;
	}

	// returns style for provides port
	private static Style createStyleForExternalProvidesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, EXTERNAL_PROVIDES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, BLUE));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	public static boolean needsUpdateForUsesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(USES_PORT, style.getId());
		return !result;
	}

	// returns style for uses port
	private static Style createStyleForUsesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, USES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, BLACK));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for super uses port
	private static Style createStyleForSuperUsesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, SUPER_USES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, BLACK));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for super uses port
	private static Style createStyleForSuperProvidesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, SUPER_PROVIDES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, WHITE));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	public static boolean needsUpdateForExternalUsesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(EXTERNAL_USES_PORT, style.getId());
		return !result;
	}

	// returns style for uses external port
	private static Style createStyleForExternalUsesPort(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, EXTERNAL_USES_PORT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, BLUE));
		style.setFont(getPortFont(diagram));
		style.setLineWidth(2);
		style.setLineVisible(true);
		return style;
	}

	// returns style for lollipop ellipse
	private static Style createStyleForLollipop(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, LOLLIPOP);
		style.setLineWidth(1);
		style.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
		style.setForeground(Graphiti.getGaService().manageColor(diagram, BLACK));
		style.setTransparency(.99d);
		return style;
	}

	public static boolean needsUpdateForStartOrderAssemblyControllerEllipse(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(style.getId(), ASSEMBLY_CONTROLLER_ELLIPSE);
		return !result;
	}

	private static Style createStyleForStartOrderAssemblyControllerEllipse(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, ASSEMBLY_CONTROLLER_ELLIPSE);
		style.setLineWidth(1);
		style.setBackground(Graphiti.getGaService().manageColor(diagram, GOLD));
		style.setTransparency(.99d);
		return style;
	}

	public static boolean needsUpdateForStartOrderEllipse(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(style.getId(), START_ORDER_ELLIPSE);
		return !result;
	}

	private static Style createStyleForStartOrderEllipse(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, START_ORDER_ELLIPSE);
		style.setLineWidth(1);
		style.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
		style.setTransparency(.99d);
		return style;
	}

	private static Style createStyleForStartOrderText(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, START_ORDER_TEXT);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, WHITE));
		Font font = gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false);
		style.setFont(font);
		return style;
	}

	// find the style with given id in style-container
	public static Style findStyle(StyleContainer styleContainer, String id) {
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

}
