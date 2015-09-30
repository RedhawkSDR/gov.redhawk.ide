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
import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

import gov.redhawk.ide.graphiti.internal.ui.resource.StyleResource;
import gov.redhawk.sca.util.PluginUtil;

public class StyleUtil { // SUPPRESS CHECKSTYLE INLINE

	public static final String OUTER_SHAPE = "gov.redhawk.style.OuterShape";

	// Font-based styles; all inherit from TEXT_BASE
	private static final String TEXT_BASE = "gov.redhawk.style.Text";
	public static final String OUTER_TEXT = "gov.redhawk.style.OuterText";
	public static final String INNER_TEXT = "gov.redhawk.style.InnerText";
	public static final String PORT_TEXT = "gov.redhawk.style.PortText";

	public static final String START_ORDER = "gov.redhawk.style.StartOrder";
	public static final String START_ORDER_ELLIPSE = "gov.redhawk.style.StartOrderEllipse";
	public static final String ASSEMBLY_CONTROLLER_ELLIPSE = "gov.redhawk.style.StartOrderAssemblyControllerEllipse";

	public static final String LOLLIPOP = "gov.redhawk.style.Lollipop";

	public static final String HOST_COLLOCATION = "gov.redhawk.style.HostCollocation";

	// Inner shape styles; all inherit from INNER_BASE
	private static final String INNER_BASE = "gov.redhawk.style.Inner";
	public static final String FIND_BY_INNER = "gov.redhawk.style.FindByInner";
	public static final String USES_DEVICE_INNER = "gov.redhawk.style.UsesDeviceInner";
	public static final String COMPONENT_INNER = "gov.redhawk.style.ComponentInner";
	public static final String COMPONENT_INNER_STARTED = "gov.redhawk.style.ComponentInnerStarted";
	public static final String COMPONENT_INNER_ERROR = "gov.redhawk.style.ComponentInnerError";
	public static final String COMPONENT_INNER_DISABLED = "gov.redhawk.style.ComponentInnerDisabled";

	// Port styles; all inherit from PORT_BASE
	private static final String PORT_BASE = "gov.redhawk.style.Port";
	public static final String USES_PORT = "gov.redhawk.style.UsesPort";
	public static final String EXTERNAL_USES_PORT = "gov.redhawk.style.UsesExternalPort";
	public static final String SUPER_USES_PORT = "gov.redhawk.style.SuperUsesPort";
	public static final String PROVIDES_PORT = "gov.redhawk.style.ProvidesPort";
	public static final String EXTERNAL_PROVIDES_PORT = "gov.redhawk.style.ExternalProvidesPort";
	public static final String SUPER_PROVIDES_PORT = "gov.redhawk.style.SuperProvidesPort";
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
		createStylesForText(diagram);
		createStyleForOuterShape(diagram);
		createStyleForLollipop(diagram);
		createStylesForStartOrder(diagram);
		createStylesForInnerShapes(diagram);
		createStylesForPorts(diagram);
		createStyleForHostCollocation(diagram);
	}

	public static void setStyle(GraphicsAlgorithm ga, String styleId) {
		// Find the (potentially nested) style via the StyleResource's URI
		URI uri = StyleResource.STYLE_URI.appendFragment(styleId);
		Style style = (Style) ga.eResource().getResourceSet().getEObject(uri, true);
		ga.setStyle(style);
	}
	
	// returns component outer rectangle style
	private static Style createStyleForOuterShape(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, OUTER_SHAPE);
		style.setForeground(gaService.manageColor(diagram, BLACK));
		style.setBackground(gaService.manageColor(diagram, OUTER_CONTAINER_BACKGROUND));
		style.setTransparency(0.5);
		style.setLineVisible(false);
		return style;
	}

	private static void createStylesForText(Diagram diagram) {
		// Disable the background and set left alignment--these are the default settings for Text algorithms, but need
		// to be set in the style when using createPlainText(). Unfortunately, it appears that you have to set filled
		// on the child styles, otherwise it overrides the parent setting with a default value of true.
		IGaService gaService = Graphiti.getGaService();
		Style baseStyle = gaService.createStyle(diagram, TEXT_BASE);
		baseStyle.setForeground(gaService.manageColor(diagram, BLACK));
		baseStyle.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);

		// Outer text style
		Style outerStyle = gaService.createPlainStyle(baseStyle, OUTER_TEXT);
		outerStyle.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, true));
		outerStyle.setFilled(false);

		// Inner text style
		Style innerStyle = gaService.createPlainStyle(baseStyle, INNER_TEXT);
		innerStyle.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 11, false, false));
		innerStyle.setFilled(false);

		// Port name text
		Style portStyle = gaService.createPlainStyle(baseStyle, PORT_TEXT);
		portStyle.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false));
		portStyle.setFilled(false);
	}

	private static void createStylesForInnerShapes(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style baseStyle = gaService.createStyle(diagram, INNER_BASE);
		baseStyle.setLineWidth(1);

		// Default component style
		Style defaultStyle = gaService.createPlainStyle(baseStyle, COMPONENT_INNER);
		gaService.setRenderingStyle(defaultStyle, PredefinedColoredAreas.getBlueWhiteAdaptions());

		// Started component style (runtime only)
		Style startedStyle = gaService.createPlainStyle(baseStyle, COMPONENT_INNER_STARTED);
		gaService.setRenderingStyle(startedStyle, RHContainerColoredAreas.getGreenWhiteAdaptions());

		// Style for component in an error state
		Style errorStyle = gaService.createPlainStyle(baseStyle, COMPONENT_INNER_ERROR);
		gaService.setRenderingStyle(errorStyle, RHContainerColoredAreas.getYellowWhiteAdaptions());

		// Disabled component style
		Style disabledStyle = gaService.createPlainStyle(baseStyle, COMPONENT_INNER_DISABLED);
		gaService.setRenderingStyle(disabledStyle, PredefinedColoredAreas.getLightGrayAdaptions());

		// FindBy style
		Style findbyStyle = gaService.createPlainStyle(baseStyle, FIND_BY_INNER);
		findbyStyle.setLineStyle(LineStyle.DASH);
		gaService.setRenderingStyle(findbyStyle, FindByColoredAreas.getCopperWhiteAdaptions());

		// UsesDevice style
		Style usesDeviceStyle = gaService.createPlainStyle(baseStyle, USES_DEVICE_INNER);
		usesDeviceStyle.setLineStyle(LineStyle.DASH);
		gaService.setRenderingStyle(usesDeviceStyle, FindByColoredAreas.getLightGrayAdaptions());
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

	public static boolean needsUpdateForProvidesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(PROVIDES_PORT, style.getId());
		return !result;
	}

	/**
	 * Creates all of the styles used for drawing ports in the given diagram
	 * @param diagram
	 */
	private static void createStylesForPorts(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style baseStyle = gaService.createStyle(diagram, PORT_BASE);
		baseStyle.setForeground(gaService.manageColor(diagram, BLACK));
		baseStyle.setLineWidth(1);
		baseStyle.setLineVisible(true);

		Style providesStyle = gaService.createPlainStyle(baseStyle, PROVIDES_PORT);
		providesStyle.setBackground(gaService.manageColor(diagram, WHITE));

		Style externalProvidesStyle = gaService.createPlainStyle(baseStyle, EXTERNAL_PROVIDES_PORT);
		externalProvidesStyle.setBackground(gaService.manageColor(diagram, BLUE));

		Style superProvidesStyle = gaService.createPlainStyle(baseStyle, SUPER_PROVIDES_PORT);
		superProvidesStyle.setBackground(gaService.manageColor(diagram, WHITE));

		Style usesStyle = gaService.createPlainStyle(baseStyle, USES_PORT);
		usesStyle.setBackground(gaService.manageColor(diagram, BLACK));

		Style externalUsesStyle = gaService.createPlainStyle(baseStyle, EXTERNAL_USES_PORT);
		externalUsesStyle.setBackground(gaService.manageColor(diagram, BLUE));

		Style superUsesStyle = gaService.createPlainStyle(baseStyle, SUPER_USES_PORT);
		superUsesStyle.setBackground(gaService.manageColor(diagram, BLACK));

		// Style for a port which is compatible as the other end of a connection.
		Style compatibleStyle = gaService.createPlainStyle(baseStyle, PORT_STYLE_COMPATIBLE);
		compatibleStyle.setBackground(gaService.manageColor(diagram, COMPATIBLE_PORT));

		// Style for port statistics - no errors
		Style okStyle = gaService.createPlainStyle(baseStyle, PORT_STYLE_OK);
		okStyle.setBackground(gaService.manageColor(diagram, PORT_OK));

		// Style for port statistics - error level 1
		Style warn1Style = gaService.createPlainStyle(baseStyle, PORT_STYLE_WARN1);
		warn1Style.setBackground(gaService.manageColor(diagram, PORT_WARNING_1));

		// Style for port statistics - error level 2
		Style warn2Style = gaService.createPlainStyle(baseStyle, PORT_STYLE_WARN2);
		warn2Style.setBackground(gaService.manageColor(diagram, PORT_WARNING_2));

		// Style for port statistics - error level 3
		Style warn3Style = gaService.createPlainStyle(baseStyle, PORT_STYLE_WARN3);
		warn3Style.setBackground(gaService.manageColor(diagram, PORT_WARNING_3));

		// Style for port statistics - error level 4
		Style warn4Style = gaService.createPlainStyle(baseStyle, PORT_STYLE_WARN4);
		warn4Style.setBackground(gaService.manageColor(diagram, PORT_WARNING_4));
	}

	public static boolean needsUpdateForExternalProvidesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(EXTERNAL_PROVIDES_PORT, style.getId());
		return !result;
	}

	public static boolean needsUpdateForUsesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(USES_PORT, style.getId());
		return !result;
	}

	public static boolean needsUpdateForExternalUsesPort(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(EXTERNAL_USES_PORT, style.getId());
		return !result;
	}

	// returns style for lollipop ellipse
	private static Style createStyleForLollipop(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style style = gaService.createStyle(diagram, LOLLIPOP);
		style.setLineWidth(1);
		style.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
		style.setForeground(Graphiti.getGaService().manageColor(diagram, BLACK));
		return style;
	}

	public static boolean needsUpdateForStartOrderAssemblyControllerEllipse(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(style.getId(), ASSEMBLY_CONTROLLER_ELLIPSE);
		return !result;
	}

	private static void createStylesForStartOrder(Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		Style baseStyle = gaService.createStyle(diagram, START_ORDER);
		baseStyle.setLineWidth(1);
		baseStyle.setTransparency(.99d);
		baseStyle.setForeground(gaService.manageColor(diagram, BLACK));
		Font font = gaService.manageFont(diagram, DEFAULT_FONT, 8, false, false);
		baseStyle.setFont(font);

		Style acStyle = gaService.createPlainStyle(baseStyle, ASSEMBLY_CONTROLLER_ELLIPSE);
		acStyle.setBackground(Graphiti.getGaService().manageColor(diagram, GOLD));

		Style soStyle = gaService.createPlainStyle(baseStyle, START_ORDER_ELLIPSE);
		soStyle.setBackground(Graphiti.getGaService().manageColor(diagram, WHITE));
	}

	public static boolean needsUpdateForStartOrderEllipse(Diagram diagram, Style style) {
		if (style == null) {
			return true;
		}
		boolean result = PluginUtil.equals(style.getId(), START_ORDER_ELLIPSE);
		return !result;
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
				Style child = findStyle(style, id);
				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

}
