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

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.PluginUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class TerminateLocalLaunchHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
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
			if (localLaunch == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
				EList<ScaComponent> components = ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform().getComponents();
				for (Object c : components.toArray()) {
					LocalScaComponent lc = (LocalScaComponent) c;
					terminate(lc);
				}
			} else {
				terminate(localLaunch);
			}
		}
	}

	/**
	 * @param localLaunch
	 */
	private void terminate(final LocalLaunch localLaunch) {
		ScaModelCommand.execute(localLaunch, new ScaModelCommand() {

			@Override
			public void execute() {
				EcoreUtil.delete(localLaunch);
			}
		});
		if (localLaunch.getLaunch() != null) {
			final Job job = new Job("Terminating") {
	
				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					try {
						localLaunch.getLaunch().terminate();
					} catch (final DebugException e) {
						return e.getStatus();
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		super.setEnabled(evaluationContext);
		if (evaluationContext instanceof EvaluationContext) {
			final EvaluationContext context = (EvaluationContext) evaluationContext;
			final Object sel = context.getVariable("selection");
			if (sel instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) sel;
				for (final Object obj : ss.toList()) {
					final LocalLaunch ll = PluginUtil.adapt(LocalLaunch.class, obj);
					if (ll != null) {
						setBaseEnabled(true);
						return;
					}
				}
			}
		}
		setBaseEnabled(false);
	}

}
