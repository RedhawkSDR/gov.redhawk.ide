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
package gov.redhawk.ide.swtbot.finder.widgets;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.eclipse.ui.forms.widgets.FormText;
import org.hamcrest.SelfDescribing;

/**
 * This represents a {@link FormText} widget.
 */
public class RHBotFormText extends AbstractSWTBotControl<FormText> {

	public RHBotFormText(FormText w) throws WidgetNotFoundException {
		super(w, null);
	}

	public RHBotFormText(FormText w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
	}

}
