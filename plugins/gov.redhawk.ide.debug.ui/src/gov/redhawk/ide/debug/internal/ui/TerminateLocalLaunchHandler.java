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
package gov.redhawk.ide.debug.internal.ui;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.TerminateJob;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.sca.util.PluginUtil;

public class TerminateLocalLaunchHandler extends AbstractHandler {

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
				List<ScaComponent> components = localSca.getSandboxWaveform().getComponents();
				for (Object component : components.toArray()) {
					if (component instanceof LocalLaunch) {
						terminate((LocalLaunch) component);
					}
				}
			} else if (localLaunch == localSca.getSandboxDeviceManager()) {
				List<ScaDevice< ? >> devices = localSca.getSandboxDeviceManager().getAllDevices();
				for (Object device : devices.toArray()) {
					if (device instanceof LocalLaunch) {
						terminate((LocalLaunch) device);
					}
				}
			} else {
				terminate(localLaunch);
			}
		}
	}

	private void terminate(LocalLaunch localLaunch) {
		ILaunch launch = localLaunch.getLaunch();
		Job job = new TerminateJob(launch, launch.getLaunchConfiguration().getName());
		job.setUser(true);
		job.schedule();
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
