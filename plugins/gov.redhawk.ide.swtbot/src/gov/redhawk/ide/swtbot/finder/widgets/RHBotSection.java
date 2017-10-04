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
package gov.redhawk.ide.swtbot.finder.widgets;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.eclipse.ui.forms.widgets.Section;
import org.hamcrest.SelfDescribing;

/**
 * This represents a {@link Section} widget.
 */
public class RHBotSection extends AbstractSWTBotControl<Section> {

	public RHBotSection(Section w) throws WidgetNotFoundException {
		super(w, null);
	}
	
	public RHBotSection(Section w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
	}
	
	/**
	 * Expand the section.
	 */
	public RHBotSection expand() {
		log.debug(MessageFormat.format("Expanding section {0}", SWTUtils.getText(widget))); //$NON-NLS-1$
		waitForEnabled();
		if (widget.isExpanded()) {
			log.debug(MessageFormat.format("Section {0} is already expanded", SWTUtils.getText(widget))); //$NON-NLS-1$
		}
		syncExec(new VoidResult() {
			public void run() {
				widget.setExpanded(true);
			}
		});
		log.debug(MessageFormat.format("Expanded section {0}", SWTUtils.getText(widget))); //$NON-NLS-1$
		return this;
	}

	/**
	 * Returns a SWTBot instance that matches the contents of this workbench part.
	 *
	 * @return SWTBot
	 */
	public SWTBot bot() {
		return new SWTBot(widget);
	}
}
