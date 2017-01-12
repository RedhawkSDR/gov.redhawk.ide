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

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SelectIDL extends DefaultCondition {
	
	private final String module;
	private final String intf;
	
	public SelectIDL(String module, String intf) {
		this.module = module;
		this.intf = intf;
	}

	@Override
	public boolean test() throws Exception {
		SWTBotTree idlTree = bot.tree();
		if (module != null) {
			SWTBotTreeItem moduleItem = idlTree.getTreeItem(module).expand();
			try {
				moduleItem.getNode(intf).select();
			} catch (WidgetNotFoundException ex) {
				moduleItem.collapse();
				return false;
			}
		} else {
			try {
				idlTree.getTreeItem(intf).select();
			} catch (WidgetNotFoundException ex) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getFailureMessage() {
		return String.format("Unable to find / select interface '%s/%s' in the IDL selection dialog", module, intf);
	}

}
