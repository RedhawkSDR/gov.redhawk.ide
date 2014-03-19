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

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import gov.redhawk.sca.ui.editors.IScaContentDescriber;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.util.ProtectedThreadExecutor;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.ui.IEditorInput;

public class ScaChalkboardContentDescriber implements IScaContentDescriber {

	private static class DelayedEditorInput extends ScaFileStoreEditorInput {

		public DelayedEditorInput(ScaWaveform scaElement) throws CoreException {
			super(scaElement, DelayedEditorInput.getFileStore(scaElement));
		}

		private static IFileStore getFileStore(ScaWaveform waveform) throws CoreException {
			org.eclipse.emf.common.util.URI uri = waveform.getProfileObj().eResource().getURI();
			final URI resolvedURI;
			if (uri.isPlatform() || uri.isFile()) {
				uri = uri.trimQuery();
			}
			if (uri.isPlatform()) {
				resolvedURI = URI.create(CommonPlugin.resolve(uri).toString());
			} else {
				resolvedURI = URI.create(uri.toString());
			}
			return EFS.getStore(resolvedURI);
		}

		@Override
		public LocalScaWaveform getScaObject() {
			ScaWaveform retVal = (ScaWaveform) super.getScaObject();
			if (retVal instanceof LocalScaWaveform) {
				return (LocalScaWaveform) retVal;
			} else {
				return getLocalScaWaveform(retVal);
			}
		}

		private LocalScaWaveform getLocalScaWaveform(final ScaWaveform remoteWaveform) {
			// Remote Waveform
			LocalScaWaveform waveform = null;
			waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();

			final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

			final NotifyingNamingContext rootContext = localSca.getRootContext();

			final NotifyingNamingContext context;
			try {
				context = LocalApplicationFactory.createWaveformContext(rootContext, remoteWaveform.getIdentifier());
			} catch (CoreException e) {
				throw new IllegalStateException("Failed to create local Chalkboard naming context", e);
			}

			waveform.setNamingContext(context);
			waveform.setIor(remoteWaveform.getIor());
			waveform.setProfile(remoteWaveform.getProfile());
			waveform.setProfileURI(remoteWaveform.getProfileURI());
			waveform.setProfileObj(remoteWaveform.getProfileObj());
			waveform.setStarted(remoteWaveform.getStarted());

			final ApplicationImpl app = new ApplicationImpl(waveform, remoteWaveform.getIdentifier(), remoteWaveform.getName(), remoteWaveform.getObj());
			waveform.setLocalApp(app);

			final LocalScaWaveform tmpLocalScaWaveform = waveform;
			// Create local copy
			ScaModelCommand.execute(localSca, new ScaModelCommand() {

				@Override
				public void execute() {
					localSca.getWaveforms().add(tmpLocalScaWaveform);
				}
			});

			try {
				final ScaWaveform tmpWaveform = waveform;
				ProtectedThreadExecutor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						LocalApplicationFactory.bindApp(app);
						tmpWaveform.refresh(null, RefreshDepth.FULL);
						return null;
					}
				});

				ScaModelCommand.execute(remoteWaveform, new ScaModelCommand() {

					@Override
					public void execute() {
						remoteWaveform.eAdapters().add(new AdapterImpl() {
							@Override
							public void notifyChanged(Notification msg) {
								switch (msg.getFeatureID(ScaWaveform.class)) {
								case ScaPackage.SCA_WAVEFORM__DISPOSED:
									if (msg.getNewBooleanValue()) {
										tmpLocalScaWaveform.dispose();
									}
									break;
								default:
									break;
								}
							}
						});
					}
				});
			} catch (InterruptedException e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID, "Failed to refresh waveform.", e));
			} catch (ExecutionException e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID, "Failed to refresh waveform.", e));
			} catch (TimeoutException e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID, "Failed to refresh waveform.", e));
			}
			return waveform;
		}

	}

	@Override
	public int describe(final Object contents) throws IOException {
		if (contents instanceof ScaWaveform) {
			return IScaContentDescriber.VALID;
		}
		return IScaContentDescriber.INVALID;
	}

	@Override
	public IEditorInput getEditorInput(final Object contents) {
		if (contents == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
			return LocalScaElementFactory.getLocalScaInput();
		} else if (contents instanceof LocalScaWaveform) {
			try {
				return new DelayedEditorInput((LocalScaWaveform) contents);
			} catch (CoreException e) {
				ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
				return null;
			}
		} else if (contents instanceof ScaWaveform) {
			try {
				return new DelayedEditorInput((ScaWaveform) contents);
			} catch (CoreException e) {
				ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
				return null;
			}
		}

		return null;
	}
}
