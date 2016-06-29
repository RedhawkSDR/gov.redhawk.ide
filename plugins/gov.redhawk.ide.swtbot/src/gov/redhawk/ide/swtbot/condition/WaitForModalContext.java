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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

/**
 * Used to wait while the UI thread is somewhere in the SWT {@link ModalContext} class. This is useful when UI code
 * invokes a long-running operation via {@link IRunnableContext#run(boolean, boolean, IRunnableWithProgress)} on a
 * wizard, etc.
 */
public class WaitForModalContext extends DefaultCondition {

	@Override
	public boolean test() throws Exception {
		StackTraceElement[] elements = bot.getDisplay().getThread().getStackTrace();
		for (StackTraceElement element : elements) {
			if (element.getClassName().startsWith("org.eclipse.jface.operation.ModalContext")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getFailureMessage() {
		return "Modal context was still running";
	}
}
