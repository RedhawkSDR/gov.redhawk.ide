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
package gov.redhawk.ide.swtbot.matchers;

import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.hamcrest.Description;

public class PaletteFilterMatcher extends AbstractMatcher<EditPart> {

	@Override
	public void describeTo(Description description) {
		description.appendText("of type RHGraphitiPaletteFilterEditPart");
	}

	@Override
	protected boolean doMatch(Object item) {
		return "RHGraphitiPaletteFilterEditPart".equals(item.getClass().getSimpleName());
	}

}
