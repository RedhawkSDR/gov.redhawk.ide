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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.junit.Assert;

public class ConnectionUtils {

	private ConnectionUtils() {
	}

	public enum ConnectionState {
		NORMAL,
		ERROR
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
			Assert.assertEquals("Connection should be black", new RGB(0, 0, 0), figure.getForegroundColor().getRGB());
			break;
		case ERROR:
			Assert.assertEquals("Connection should be red", new RGB(255, 0, 0), figure.getForegroundColor().getRGB());
			Assert.assertTrue("Connection should have an error tooltip", figure.getToolTip() instanceof Label);
			String tooltipText = ((Label) figure.getToolTip()).getText();
			Assert.assertEquals("Connection should have an error tooltip", "Incompatible interface", tooltipText);
			break;
		default:
			Assert.fail();
		}
	}
}
