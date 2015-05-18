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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;

public class WaitForWidgetEnablement implements ICondition {

	private final AbstractSWTBot< ? extends Widget> widget;
	private boolean enabled;

	public WaitForWidgetEnablement(AbstractSWTBot< ? extends Widget> widget, boolean enabled) {
		this.widget = widget;
		this.enabled = enabled;
	}

	@Override
	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		return widget.isEnabled() == enabled;
	}

	public String getFailureMessage() {
		return "The widget " + widget + "'s enabled state was not " + enabled;
	}

}
