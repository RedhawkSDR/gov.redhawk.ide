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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.junit.Assert;

import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

public class ConnectionUtils {

	private static final RGB COLOR_NORMAL = ConnectionUtils.convertColor(IColorConstant.BLACK);
	private static final RGB COLOR_ERROR = ConnectionUtils.convertColor(IColorConstant.RED);
	private static final RGB COLOR_WARNING = ConnectionUtils.convertColor(StyleUtil.GOLD);

	public enum ConnectionState {
		NORMAL,
		ERROR,
		WARNING
	}

	private static RGB convertColor(IColorConstant expected) {
		return new RGB(expected.getRed(), expected.getGreen(), expected.getBlue());
	}

	/**
	 * Checks styling for a connection based on the state it should be in
	 * @param connectionBot The bot for the connection
	 * @param connectionState The state the connection should be in
	 */
	public static void assertConnectionStyling(SWTBotGefConnectionEditPart connectionBot, ConnectionState connectionState) {
		IFigure figure = connectionBot.part().getFigure();
		switch (connectionState) {
		case NORMAL:
			Assert.assertEquals("Connection should be black", COLOR_NORMAL, figure.getForegroundColor().getRGB());
			break;
		case ERROR:
			Assert.assertEquals("Connection should be red", COLOR_ERROR, figure.getForegroundColor().getRGB());
			Assert.assertTrue("Connection should have an error tooltip", figure.getToolTip() instanceof Label);
			String tooltipText = ((Label) figure.getToolTip()).getText();
			Assert.assertEquals("Connection should have an error tooltip", "Incompatible interface", tooltipText);
			break;
		case WARNING:
			Assert.assertEquals("Connection should be yellow", COLOR_WARNING, figure.getForegroundColor().getRGB());
			Assert.assertTrue("Connection should have an warning tooltip", figure.getToolTip() instanceof Label);
			tooltipText = ((Label) figure.getToolTip()).getText();
			Assert.assertEquals("Connection should have an warning tooltip", "Duplicate connection", tooltipText);
			break;
		default:
			Assert.fail();
		}
	}
}
