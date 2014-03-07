/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.LocationType;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.util.IGradientType;
import org.eclipse.graphiti.util.IPredefinedRenderingStyle;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

/**
 * Gradient definition for ID {@link #LIME_WHITE_ID} with adaptations
 * {@link #STYLE_ADAPTATION_DEFAULT} {@link #STYLE_ADAPTATION_PRIMARY_SELECTED},
 * {@link #STYLE_ADAPTATION_SECONDARY_SELECTED}.
 * 
 * @see StyleUtil
 */
public class TutorialColoredAreas extends PredefinedColoredAreas implements ITutorialRenderingStyle {

	private static GradientColoredAreas getLimeWhiteDefaultAreas() {
		final GradientColoredAreas gradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
		final EList<GradientColoredArea> gcas = gradientColoredAreas.getGradientColor();

		addGradientColoredArea(gcas, "CCFFCC", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "CCFFCC", 1, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "CCFF99", 1, LocationType.LOCATION_TYPE_ABSOLUTE_START, "CCFF99", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "CCFF66", 2, LocationType.LOCATION_TYPE_ABSOLUTE_START, "CCFF66", 3, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "66FF00", 3, LocationType.LOCATION_TYPE_ABSOLUTE_START, "CCFFCC", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		addGradientColoredArea(gcas, "CCFFCC", 2, LocationType.LOCATION_TYPE_ABSOLUTE_END, "CCFFCC", 0, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		gradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
		return gradientColoredAreas;
	}

	private static GradientColoredAreas getLimeWhitePrimarySelectedAreas() {
		final GradientColoredAreas gradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
		gradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_PRIMARY_SELECTED);
		final EList<GradientColoredArea> gcas = gradientColoredAreas.getGradientColor();

		addGradientColoredArea(gcas, "66CCCC", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "66CCCC", 1, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "66CC99", 1, LocationType.LOCATION_TYPE_ABSOLUTE_START, "66CC99", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "66CC66", 2, LocationType.LOCATION_TYPE_ABSOLUTE_START, "66CC66", 3, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "00CC00", 3, LocationType.LOCATION_TYPE_ABSOLUTE_START, "00CC66", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		addGradientColoredArea(gcas, "00CC99", 2, LocationType.LOCATION_TYPE_ABSOLUTE_END, "00CC99", 0, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		return gradientColoredAreas;
	}

	private static GradientColoredAreas getLimeWhiteSecondarySelectedAreas() {
		final GradientColoredAreas gradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
		gradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_SECONDARY_SELECTED);
		final EList<GradientColoredArea> gcas = gradientColoredAreas.getGradientColor();

		addGradientColoredArea(gcas, "33CCCC", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "33CCCC", 1, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "33CC99", 1, LocationType.LOCATION_TYPE_ABSOLUTE_START, "33CC99", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "33CC66", 2, LocationType.LOCATION_TYPE_ABSOLUTE_START, "33CC66", 3, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "33CC00", 3, LocationType.LOCATION_TYPE_ABSOLUTE_START, "33CC99", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		addGradientColoredArea(gcas, "66CC99", 2, LocationType.LOCATION_TYPE_ABSOLUTE_END, "66CC99", 0, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		return gradientColoredAreas;
	}

	public static AdaptedGradientColoredAreas getLimeWhiteAdaptions() {
		final AdaptedGradientColoredAreas agca = StylesFactory.eINSTANCE.createAdaptedGradientColoredAreas();
		agca.setDefinedStyleId(LIME_WHITE_ID);
		agca.setGradientType(IGradientType.VERTICAL);
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT, getLimeWhiteDefaultAreas());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_PRIMARY_SELECTED,
				getLimeWhitePrimarySelectedAreas());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_SECONDARY_SELECTED,
				getLimeWhiteSecondarySelectedAreas());
		return agca;
	}
}
