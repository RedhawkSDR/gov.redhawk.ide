/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

import gov.redhawk.ide.swtbot.ViewUtils;

public class ExplorerNodeSelected extends DefaultCondition {

	private SWTBotView view;
	private String label;

	public ExplorerNodeSelected(String label) {
		this.label = label;
	}

	@Override
	public void init(SWTBot bot) {
		super.init(bot);
		view = ViewUtils.getExplorerView((SWTWorkbenchBot) bot);
	}

	@Override
	public boolean test() throws Exception {
		if (!view.isActive()) {
			return false;
		}
		TableCollection selection = view.bot().tree().selection();
		return selection.rowCount() == 1 && label.equals(selection.get(0, 0));
	}

	@Override
	public String getFailureMessage() {
		return String.format("Explorer view did not become active or the node '%s' was not selected", label);
	}

}
