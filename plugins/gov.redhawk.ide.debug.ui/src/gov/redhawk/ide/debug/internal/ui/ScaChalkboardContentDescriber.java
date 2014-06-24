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
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import gov.redhawk.sca.ui.editors.IScaContentDescriber;
import gov.redhawk.sca.ui.editors.ScaObjectWrapperContentDescriber;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

public class ScaChalkboardContentDescriber implements IScaContentDescriber {

	private static class DelayedEditorInput extends ScaFileStoreEditorInput {

		private LocalScaWaveform proxy;

		public DelayedEditorInput(ScaWaveform scaElement) throws CoreException {
			super(scaElement, ScaObjectWrapperContentDescriber.getFileStore(scaElement));
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

		@Override
		public java.net.URI getURI() {
			LocalScaWaveform scaObject = getScaObject();
			try {
				IFileStore store = ScaObjectWrapperContentDescriber.getFileStore(scaObject);
				return store.toURI();
			} catch (CoreException e) {
				// PASS
			}
			return super.getURI();
		}

		private LocalScaWaveform getLocalScaWaveform(final ScaWaveform remoteWaveform) {
			if (proxy != null) {
				return proxy;
			}
			LocalScaWaveform waveform = null;
			final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
			for (ScaWaveform localWaveform : localSca.getWaveforms()) {
				if (localWaveform.getIdentifier().equals(remoteWaveform.getIdentifier()) && localWaveform instanceof LocalScaWaveform) {
					proxy = (LocalScaWaveform) localWaveform;
					return proxy;
				}
			}

			waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();

			final NotifyingNamingContext rootContext = localSca.getRootContext();

			final NotifyingNamingContext context;
			try {
				context = LocalApplicationFactory.createWaveformContext(rootContext, remoteWaveform.getIdentifier());
			} catch (CoreException e) {
				throw new IllegalStateException("Failed to create waveform naming context", e);
			}

			waveform.setNamingContext(context);
			waveform.setProfile(remoteWaveform.getProfile());
			@SuppressWarnings("restriction")
			final ApplicationImpl app = new ApplicationImpl(waveform, remoteWaveform.getIdentifier(), remoteWaveform.getName(), remoteWaveform.getObj());
			waveform.setLocalApp(app);
			waveform.setProfileURI(remoteWaveform.getProfileURI());
			try {
				// Set the profile to the new URI generated with the new proxy local application object
				IFileStore store = ScaObjectWrapperContentDescriber.getFileStore(waveform);
				waveform.setProfileURI(URI.createURI(store.toURI().toString()));
			} catch (CoreException e) {
				throw new IllegalStateException("Failed to create waveform uri", e);
			}

			final LocalScaWaveform tmpLocalScaWaveform = waveform;
			// Create local copy
			ScaModelCommand.execute(localSca, new ScaModelCommand() {

				@Override
				public void execute() {
					localSca.getWaveforms().add(tmpLocalScaWaveform);
				}
			});

			if (Display.getCurrent() != null) {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
				try {
					dialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							try {
								CorbaUtils.invoke(new Callable<Object>() {

									@Override
									public Object call() throws Exception {
										LocalApplicationFactory.bindApp(app);
										return null;
									}

								}, monitor);
							} catch (CoreException e1) {
								throw new InvocationTargetException(e1);
							}

						}
					});
				} catch (InvocationTargetException e) {
					throw new IllegalStateException("Failed to bind waveform", e);
				} catch (InterruptedException e) {
					// PASS
				}
			} else {
				try {
					LocalApplicationFactory.bindApp(app);
				} catch (CoreException e) {
					throw new IllegalStateException("Failed to bind waveform", e);
				}
			}

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
			proxy = waveform;
			return waveform;
		}

	}

	@Override
	public int describe(final Object contents) throws IOException {
		if (contents instanceof LocalScaWaveform) {
			return IScaContentDescriber.INVALID;
		} else if (contents instanceof ScaWaveform) {
			return IScaContentDescriber.VALID;
		}
		return IScaContentDescriber.INVALID;
	}

	@Override
	public IEditorInput getEditorInput(final Object contents) {
		if (contents == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
			return LocalScaElementFactory.getLocalScaInput();
		} else if (contents instanceof ScaWaveform) {
			try {
				return new DelayedEditorInput((ScaWaveform) contents);
			} catch (CoreException e) {
				ScaDebugUiPlugin.getDefault().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugUiPlugin.PLUGIN_ID, "Failed to get Editor Input for " + contents, e));
				return null;
			}
		}

		return null;
	}
}
