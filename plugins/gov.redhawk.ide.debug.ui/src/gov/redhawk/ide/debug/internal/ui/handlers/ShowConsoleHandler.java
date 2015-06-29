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
package gov.redhawk.ide.debug.internal.ui.handlers;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.sca.util.PluginUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Handles the "Show Console" action for anything launched locally in the sandbox.
 */
public class ShowConsoleHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel == null) {
			sel = HandlerUtil.getCurrentSelection(event);
		}
		if (sel instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) sel;
			for (final Object obj : ss.toList()) {
				try {
					handleShowConsole(obj, event);
				} catch (final CoreException e) {
					StatusManager.getManager().handle(e, ScaDebugUiPlugin.PLUGIN_ID);
				}
			}
		}
		return null;
	}

	private void handleShowConsole(final Object obj, final ExecutionEvent event) throws CoreException {
		final LocalLaunch localLaunch = PluginUtil.adapt(LocalLaunch.class, obj);
		if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
			final IConsole console = DebugUIPlugin.getDefault().getProcessConsoleManager().getConsole(localLaunch.getLaunch().getProcesses()[0]);
			final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			consoleManager.showConsoleView(console);
		}
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		if (evaluationContext instanceof IEvaluationContext) {
			final IEvaluationContext context = (IEvaluationContext) evaluationContext;
			final Object sel = context.getVariable("selection");
			if (sel instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) sel;
				for (final Object obj : ss.toList()) {
					final LocalLaunch ll = PluginUtil.adapt(LocalLaunch.class, obj);
					if (ll != null && ll.getLaunch() != null) {
						setBaseEnabled(true);
						return;
					}
				}
			}
		}
		setBaseEnabled(false);
	}
}
