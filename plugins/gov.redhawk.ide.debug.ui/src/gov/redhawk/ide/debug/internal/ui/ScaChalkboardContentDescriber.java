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
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.util.ScaFileSystemUtil;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import gov.redhawk.sca.ui.editors.IScaContentDescriber;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

/**
 * This {@link IScaContentDescriber} describes {@link ScaWaveform}s, but returns a {@link LocalScaWaveform} that
 * serves as a proxy for it. This makes it suitable for use as input to the chalkboard diagram.
 */
public class ScaChalkboardContentDescriber implements IScaContentDescriber {

	private static class DelayedEditorInput extends ScaFileStoreEditorInput {

		private LocalScaWaveform proxy;

		public DelayedEditorInput(ScaWaveform scaElement) throws CoreException {
			super(scaElement, ScaFileSystemUtil.getFileStore(scaElement));
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
				IFileStore store = ScaFileSystemUtil.getFileStore(scaObject);
				return store.toURI();
			} catch (CoreException e) {
				// PASS
			}
			return super.getURI();
		}

		private LocalScaWaveform getLocalScaWaveform(final ScaWaveform remoteWaveform) {
			// If we've already cached the proxy object, return it
			if (proxy != null) {
				return proxy;
			}

			// Try to find a LocalScaWaveform object with the same identifier as the domain waveform. If found, it's
			// the proxy
			final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
			for (ScaWaveform localWaveform : localSca.getWaveforms()) {
				if (localWaveform.getIdentifier().equals(remoteWaveform.getIdentifier()) && localWaveform instanceof LocalScaWaveform) {
					proxy = (LocalScaWaveform) localWaveform;
					return proxy;
				}
			}

			// Create a new ScaLocalWaveform from the ScaWaveform
			final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform(remoteWaveform);
			ScaModelCommand.execute(localSca, new ScaModelCommand() {

				@Override
				public void execute() {
					localSca.getWaveforms().add(waveform);
				}
			});

			// Bind the application
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
										LocalApplicationFactory.bindApp((ApplicationImpl) waveform.getLocalApp());
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
					LocalApplicationFactory.bindApp((ApplicationImpl) waveform.getLocalApp());
				} catch (CoreException e) {
					throw new IllegalStateException("Failed to bind waveform", e);
				}
			}

			// Cache the proxy and return it
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
