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
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.ui.ScaContentProvider;
import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

/**
 * This content provider contributes children ({@link gov.redhawk.ide.debug.LocalScaComponent LocalScaComponent}s) to
 * a {@link ScaWaveform} in the navigator, provided the {@link LocalScaWaveform} that the components belong to has the
 * same identifier as the {@link ScaWaveform}.
 * <p/>
 * This is used to make components started in the chalkboard editor for a domain waveform appear under that waveform in
 * the SCA Explorer view.
 * <p/>
 * The provider also triggers a refresh on the {@link org.eclipse.jface.viewers.Viewer Viewer} for the
 * {@link ScaWaveform} whenever components are changed in the corresponding {@link LocalScaWaveform}.
 */
public class LocalComponentContentProvider extends ScaContentProvider {

	private EContentAdapter listener = new EContentAdapter() {

		protected void addAdapter(org.eclipse.emf.common.notify.Notifier notifier) {
			// Only listen to LocalSca and waveforms (i.e. its waveform children)
			if (notifier instanceof ScaWaveform || notifier instanceof LocalSca) {
				super.addAdapter(notifier);
			}
		}

		public void notifyChanged(org.eclipse.emf.common.notify.Notification notification) {
			super.notifyChanged(notification);
			if (notification.getNotifier() instanceof LocalScaWaveform) {
				switch (notification.getFeatureID(LocalScaWaveform.class)) {
				case ScaDebugPackage.LOCAL_SCA_WAVEFORM__COMPONENTS:
					final ScaWaveform domainWaveform = findDomainWaveform((ScaWaveform) notification.getNotifier());
					if (domainWaveform != null && !viewer.getControl().isDisposed()) {
						UIJob job = new UIJob("Refresh...") {

							@Override
							public IStatus runInUIThread(IProgressMonitor monitor) {
								if (viewer.getControl().isDisposed()) {
									return Status.CANCEL_STATUS;
								}
								((TreeViewer) viewer).refresh(domainWaveform);
								return Status.OK_STATUS;
							}

						};
						job.schedule();
					}
					break;
				default:
					break;
				}
			}
		}

	};

	private LocalSca localSca;

	/**
	 * Upon instantiation, begins adapting the {@link LocalScal}
	 */
	public LocalComponentContentProvider() {
		super(ScaDebugContentProvider.createAdapterFactory());
		final ScaDebugPlugin activator = ScaDebugPlugin.getInstance();
		localSca = activator.getLocalSca();
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			@Override
			public void execute() {
				localSca.eAdapters().add(listener);
			}
		});
	}

	/**
	 * Stops adapting the {@link LocalScal}.
	 *
	 * @see gov.redhawk.sca.ui.ScaContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			@Override
			public void execute() {
				localSca.eAdapters().remove(listener);
			}
		});
		super.dispose();
	}

	/**
	 * Find the domain waveform that matches the specified local proxy waveform.
	 *
	 * @param localWaveform The local proxy waveform
	 * @return The matching domain waveform, or null if none
	 */
	protected ScaWaveform findDomainWaveform(ScaWaveform localWaveform) {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		for (ScaDomainManager dom : registry.getDomains()) {
			for (ScaWaveform waveform : dom.getWaveforms()) {
				if (PluginUtil.equals(waveform.getIdentifier(), localWaveform.getIdentifier())) {
					return waveform;
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object object) {
		return true;
	}

	@Override
	public Object[] getElements(Object object) {
		return getChildren(object);
	}

	/**
	 * For a given {@link ScaWaveform}, finds children of a matching {@link LocalScaWaveform} which the
	 * {@link ScaWaveform} doesn't have (these are components started locally in a sandbox waveform).
	 *
	 * @see ScaContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object object) {
		if (object instanceof LocalScaWaveform) {
			return new Object[0];
		}
		if (object instanceof ScaWaveform) {
			ScaWaveform remoteWaveform = (ScaWaveform) object;

			for (ScaWaveform waveform : localSca.getWaveforms()) {
				List<ScaComponent> components = new ArrayList<ScaComponent>();
				if (PluginUtil.equals(waveform.getIdentifier(), remoteWaveform.getIdentifier())) {
					for (ScaComponent comp : waveform.getComponents()) {
						boolean found = false;
						for (ScaComponent remoteComponent : remoteWaveform.getComponents()) {
							if (PluginUtil.equals(remoteComponent.getInstantiationIdentifier(), comp.getInstantiationIdentifier())) {
								found = true;
								break;
							}
						}
						if (!found) {
							components.add(comp);
						}
					}
					return components.toArray();
				}
			}
		}
		return new Object[0];
	}

}
