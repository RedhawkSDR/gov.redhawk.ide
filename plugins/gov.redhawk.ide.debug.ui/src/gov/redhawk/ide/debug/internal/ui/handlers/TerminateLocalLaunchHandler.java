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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import gov.redhawk.sca.util.PluginUtil;

/**
 * Handles the "Terminate" action for anything launched locally in the sandbox.
 */
public class TerminateLocalLaunchHandler extends AbstractHandler {

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
					handleTerminate(obj, event);
				} catch (final CoreException e) {
					StatusManager.getManager().handle(e, ScaDebugUiPlugin.PLUGIN_ID);
				}
			}
		}
		return null;
	}

	private void handleTerminate(final Object obj, final ExecutionEvent event) throws CoreException {
		final LocalLaunch localLaunch = PluginUtil.adapt(LocalLaunch.class, obj);
		if (localLaunch != null) {
			LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
			if (localLaunch == localSca.getSandboxWaveform()) {
				List<ScaComponent> components = localSca.getSandboxWaveform().getComponentsCopy();
				for (ScaComponent component : components) {
					if (component instanceof LocalLaunch) {
						SpdLauncherUtil.terminate((LocalLaunch) component);
					}
				}
			} else if (localLaunch == localSca.getSandboxDeviceManager()) {
				final LocalScaDeviceManager sandboxDevMgr = (LocalScaDeviceManager) localLaunch;
				List<LocalLaunch> launches;
				try {
					launches = ScaModelCommandWithResult.runExclusive(sandboxDevMgr, new RunnableWithResult.Impl<List<LocalLaunch>>() {
						@Override
						public void run() {
							List<LocalLaunch> launches = new ArrayList<>();
							for (ScaDevice< ? > device : sandboxDevMgr.getAllDevices()) {
								launches.add((LocalLaunch) device);
							}
							for (ScaService service : sandboxDevMgr.getServices()) {
								launches.add((LocalLaunch) service);
							}
							setResult(launches);
						}
					});
				} catch (InterruptedException e) {
					return;
				}

				for (LocalLaunch launch : launches) {
					SpdLauncherUtil.terminate(launch);
				}
			} else {
				SpdLauncherUtil.terminate(localLaunch);
			}
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
					if (ll != null) {
						LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
						if (ll == localSca.getSandboxWaveform() || ll == localSca.getSandboxDeviceManager() || ll.getLaunch() != null) {
							setBaseEnabled(true);
							return;
						}
					}
				}
			}
		}
		setBaseEnabled(false);
	}

}
