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

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.sca.ui.ScaUiPlugin;
import gov.redhawk.sca.util.PluginUtil;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.omg.CORBA.SystemException;

import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.ReleaseError;

/**
 * 
 */
public class ResetHandler extends AbstractHandler implements IHandler {

	private static class ResetJob extends Job {

		private final ScaComponent component;

		public ResetJob(final ScaComponent obj) {
			super("Reseting " + obj.getIdentifier());
			this.component = obj;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final ScaWaveform waveform = this.component.getWaveform();
			monitor.beginTask("Reseting Component " + this.component.getName(), 2);

			if (waveform instanceof LocalScaWaveform) {
				try {
					LocalScaWaveform localWaveform = (LocalScaWaveform) waveform;
					((ApplicationImpl) localWaveform.getLocalApp()).reset(this.component.getInstantiationIdentifier());
					monitor.worked(1);
				} catch (final ReleaseError e) {
					return new Status(IStatus.ERROR, ScaUiPlugin.PLUGIN_ID, "Failed to reset component: " + this.component.getName() + " "
						+ Arrays.toString(e.errorMessages), e);
				} catch (final ExecuteFail e) {
					return new Status(IStatus.ERROR, ScaUiPlugin.PLUGIN_ID, "Failed to reset component: " + this.component.getName() + " " + e.msg, e);
				} catch (final SystemException e) {
					return new Status(IStatus.ERROR, ScaUiPlugin.PLUGIN_ID, "Failed to reset component: " + this.component.getName() + " " + e.getMessage(), e);
				} finally {
					monitor.done();
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				final ScaComponent comp = PluginUtil.adapt(ScaComponent.class, obj);
				if (comp != null) {
					final ScaWaveform waveform = comp.getWaveform();
					if (waveform != null && waveform instanceof LocalScaWaveform) {
						new ResetJob(comp).schedule();
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		if (evaluationContext instanceof IEvaluationContext) {
			final IEvaluationContext context = (IEvaluationContext) evaluationContext;
			final Object sel = context.getVariable("selection");
			if (sel instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) sel;
				for (final Object obj : ss.toList()) {
					final ScaComponent comp = PluginUtil.adapt(ScaComponent.class, obj);
					if (comp != null && comp.getWaveform() != null) {
						final ScaWaveform waveform = comp.getWaveform();
						setBaseEnabled(waveform instanceof LocalScaWaveform);
						return;
					}
				}
			}
		}
		setBaseEnabled(false);
	}

}
