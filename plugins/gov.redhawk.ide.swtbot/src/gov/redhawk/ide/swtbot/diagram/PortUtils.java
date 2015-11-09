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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementDelegate;
import org.eclipse.graphiti.ui.internal.parts.IPictogramElementEditPart;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.ProvidesPortPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

@SuppressWarnings("restriction")
public class PortUtils {

	private PortUtils() {
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
				return RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER.equals(propValue);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("provides port edit parts");
			}
		});
	}

	public enum PortState {
		NORMAL_PROVIDES,
		NORMAL_USES,
		HIGHLIGHT_FOR_CONNECTION
	}

	/**
	 * Checks the styling of a port based on the state it should be in
	 * @param portBot The bot for the port (either container or the rectangle)
	 * @param portState The state the port should be in
	 */
	public static void assertPortStyling(SWTBotGefEditPart portBot, PortState portState) {
		// If this is the port container, get the port rectangle (child element)
		GraphitiShapeEditPart graphitiEditPart = (GraphitiShapeEditPart) portBot.part();
		PictogramElement pe = graphitiEditPart.getPictogramElement();
		String propValue = Graphiti.getPeService().getPropertyValue(pe, DUtil.SHAPE_TYPE);
		if (ProvidesPortPattern.SHAPE_PROVIDES_PORT_CONTAINER.equals(propValue)) {
			portBot = portBot.children().get(0);
		}

		IPictogramElementEditPart editPart = (IPictogramElementEditPart) portBot.part();
		IPictogramElementDelegate delegate = editPart.getPictogramElementDelegate();
		IFigure figure = delegate.getFigureForGraphicsAlgorithm(editPart.getPictogramElement().getGraphicsAlgorithm());
		switch (portState) {
		case NORMAL_PROVIDES:
			Assert.assertEquals(new RGB(255, 255, 255), figure.getBackgroundColor().getRGB());
			break;
		case NORMAL_USES:
			Assert.assertEquals(new RGB(0, 0, 0), figure.getBackgroundColor().getRGB());
			break;
		case HIGHLIGHT_FOR_CONNECTION:
			Assert.assertEquals(new RGB(0, 255, 0), figure.getBackgroundColor().getRGB());
			break;
		default:
			Assert.fail();
		}
	}
}
