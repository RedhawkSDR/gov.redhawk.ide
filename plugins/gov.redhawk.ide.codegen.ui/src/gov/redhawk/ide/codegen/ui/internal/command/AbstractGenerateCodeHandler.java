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
package gov.redhawk.ide.codegen.ui.internal.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

/**
 * This handler is the main entry point to code generation in the UI.
 * It performs several checks, possibly upgrading project file(s) or settings, and then invokes the code generator.
 */
public abstract class AbstractGenerateCodeHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// If the user used a context menu, generate code on the selection(s)
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection != null && !selection.isEmpty()) {
			handleMenuSelection(event, selection);
			return null;
		}

		// If the user clicked the generate code button in an editor, generate code on the editor input
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor != null) {
			handleEditorSelection(event, editor);
			return null;
		}

		// If we get here, somehow the generate code handler was triggered from somewhere it shouldn't be - log this
		RedhawkCodegenUiActivator.logError("Generate node handler was triggered without a valid selection", null);
		return null;
	}

	protected abstract void handleEditorSelection(ExecutionEvent event, IEditorPart editor);

	protected abstract void handleMenuSelection(ExecutionEvent event, ISelection selection);

}
