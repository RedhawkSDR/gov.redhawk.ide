/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.runtime;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.adapters.GraphitiAdapterUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;

public class TerminateComponentFeature extends AbstractCustomFeature {
	private static final int TIMEOUT_SECONDS = 30;

	public TerminateComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Hard terminate of the component from the model";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements()[0] instanceof ComponentShapeImpl) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		ICustomFeature[] customFeatures = getFeatureProvider().getCustomFeatures(context);
		for (ICustomFeature customFeature : customFeatures) {
			if (customFeature instanceof ReleaseComponentFeature) {
				final ReleaseComponentFeature releaseFeature = (ReleaseComponentFeature) customFeature;
				releaseComponent(releaseFeature, context);
			}
		}
	}

	private void releaseComponent(final ReleaseComponentFeature releaseFeature, final ICustomContext context) {
		Job job = new Job("Terminating component...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<Boolean> future = executor.submit(new ReleaseTask(releaseFeature, context));
				try {
					// First, try and release the object safely
					future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
				} catch (TimeoutException | InterruptedException | ExecutionException e) {
					// If we got here, the soft release failed or timed out and we need to terminate the process
					ComponentShapeImpl componentShape = (ComponentShapeImpl) context.getPictogramElements()[0];
					terminate(componentShape);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private void terminate(final ComponentShapeImpl componentShape) {
		// Manually terminate the component process
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		LocalLaunch localLaunch = null;
		if (ci != null && ci.eResource() != null) {
			final URI uri = ci.eResource().getURI();
			final Map<String, String> query = QueryParser.parseQuery(uri.query());
			final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
			final String myId = ci.getId();
			if (waveform != null) {
				for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
					final String scaComponentId = component.identifier();
					if (scaComponentId.startsWith(myId)) {
						if (component instanceof LocalLaunch) {
							localLaunch = (LocalLaunch) component;
						}
					}
				}
			}

			if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
				try {
					localLaunch.getLaunch().getProcesses()[0].terminate();
				} catch (DebugException e) {
					// PASS
					// TODO Seems like it would be worth pushing a notification to the error log if a
					// component fails to terminate.
				}
			}
		}

		// We also need to manually remove the graphical representation of the component
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				IRemoveContext rc = new RemoveContext(componentShape);
				IFeatureProvider featureProvider = getFeatureProvider();
				IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
				if (removeFeature != null) {
					removeFeature.remove(rc);
				}
			}
		});
	}

	class ReleaseTask implements Callable<Boolean> {
		private final ReleaseComponentFeature releaseFeature;
		private final ICustomContext context;

		public ReleaseTask(ReleaseComponentFeature releaseFeature, ICustomContext context) {
			this.releaseFeature = releaseFeature;
			this.context = context;
		}

		@Override
		public Boolean call() throws Exception {
			releaseFeature.execute(context);
			return true;
		}

	}
}
