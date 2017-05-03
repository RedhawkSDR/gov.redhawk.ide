/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.swtbot.diagram;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

import gov.redhawk.core.graphiti.ui.diagram.patterns.ProvidesPortPattern;
import gov.redhawk.core.graphiti.ui.diagram.patterns.UsesPortPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

@SuppressWarnings("restriction")
public class PortUtils {

	private PortUtils() {
	}

	public enum PortState {
		NORMAL_PROVIDES(new RGBA(255, 255, 255, 255)),
		NORMAL_USES(new RGBA(0, 0, 0, 255)),
		HIGHLIGHT_FOR_CONNECTION(new RGBA(0, 255, 0, 255)),
		EXTERNAL_PORT(new RGBA(0, 0, 194, 255)),
		MONITOR_DATA_GOOD(new RGBA(0, 255, 0, 255)),
		MONITOR_DATA_BAD(new RGBA(255, 0, 0, 255));

		private RGBA color;

		private PortState(RGBA color) {
			this.color = color;
		}

		public RGBA getColor() {
			return color;
		}
	}

	/**
	 * Checks the styling of a port based on the state it should be in
	 * @param portBot The bot for the port
	 * @param portState The state the port should be in
	 */
	public static void assertPortStyling(SWTBotGefEditPart portBot, PortState portState) {
		RGBA color = getPortColor(portBot);
		Assert.assertEquals("Expected port color of " + portState.toString(), portState.getColor(), color);
	}

	/**
	 * Get the port's actual displayed color (not its default color).
	 * @param port The bot for the port
	 */
	public static RGBA getPortColor(SWTBotGefEditPart port) {
		SWTBotGefEditPart childBot = port.children().get(0);
		EditPart visibleEditPart = childBot.part();
		IFigure visibleFigure = ((GraphicalEditPart) visibleEditPart).getFigure();
		return visibleFigure.getBackgroundColor().getRGBA();
	}

	/**
	 * Returns bots for each provides port's container edit part. All edit parts which are descendants of the provided
	 * bot edit part are checked.
	 * @param editPart A bot for an ancestor edit part
	 * @return Bots for all found provides port edit parts
	 */
	public static List<SWTBotGefEditPart> getProvidesPortContainerBots(SWTBotGefEditPart editPart) {
		return editPart.descendants(new BaseMatcher<EditPart>() {
			@Override
			public boolean matches(Object item) {
				if (!(item instanceof GraphitiShapeEditPart)) {
					return false;
				}
				GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) item;
				PictogramElement pe = editPart.getPictogramElement();
				String propValue = Graphiti.getPeService().getPropertyValue(pe, DUtil.SHAPE_TYPE);
				return ProvidesPortPattern.SHAPE_PROVIDES_PORT_CONTAINER.equals(propValue);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("provides port edit parts");
			}
		});
	}

	/**
	 * Returns bots for each uses port's container edit part. All edit parts which are descendants of the provided
	 * bot edit part are checked.
	 * @param editPart A bot for an ancestor edit part
	 * @return Bots for all found uses port edit parts
	 */
	public static List<SWTBotGefEditPart> getUsesPortContainerBots(SWTBotGefEditPart editPart) {
		return editPart.descendants(new BaseMatcher<EditPart>() {
			@Override
			public boolean matches(Object item) {
				if (!(item instanceof GraphitiShapeEditPart)) {
					return false;
				}
				GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) item;
				PictogramElement pe = editPart.getPictogramElement();
				String propValue = Graphiti.getPeService().getPropertyValue(pe, DUtil.SHAPE_TYPE);
				return UsesPortPattern.SHAPE_USES_PORT_CONTAINER.equals(propValue);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("provides port edit parts");
			}
		});
	}
}
