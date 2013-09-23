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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * 
 */
public class SdrRootDecorator extends LabelProvider implements ILightweightLabelDecorator {

	private final Adapter sdrRootListener = new AdapterImpl() {
		@Override
		public void notifyChanged(final org.eclipse.emf.common.notify.Notification msg) {
			if (SdrRootDecorator.this.disposed) {
				if (msg.getNotifier() instanceof EObject) {
					((EObject) msg.getNotifier()).eAdapters().remove(this);
				}
				return;
			}
			if (msg.isTouch()) {
				return;
			}
			switch (msg.getFeatureID(SdrRoot.class)) {
			case SdrPackage.SDR_ROOT__LOAD_STATUS:
				final Object oldValue = msg.getOldValue();
				final Object newValue = msg.getNewValue();
				if (oldValue instanceof IStatus && newValue instanceof IStatus) {
					final IStatus newStatus = (IStatus) newValue;
					final IStatus oldStatus = (IStatus) oldValue;
					if (newStatus.getSeverity() != oldStatus.getSeverity()) {
						fireStatusChanged(msg.getNotifier());
					}
				} else {
					fireStatusChanged(msg.getNotifier());
				}
				break;
			default:
				break;
			}
		}
	};

	private boolean disposed;

	private void fireStatusChanged(final Object object) {
		final LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, object);
		fireLabelProviderChanged(event);
	}

	/**


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.disposed = true;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if (element instanceof SdrRoot) {
			final SdrRoot sdrRoot = (SdrRoot) element;
			ScaModelCommand.execute(sdrRoot, new ScaModelCommand() {

				@Override
				public void execute() {
					if (!sdrRoot.eAdapters().contains(sdrRootListener)) {
						sdrRoot.eAdapters().add(sdrRootListener);
					}
				}
			});
			if (sdrRoot.getLoadStatus() != null) {
				final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
				final IStatus status = sdrRoot.getLoadStatus();
				if (status == null) {
					return;
				}
				switch (status.getSeverity()) {
				case IStatus.WARNING:
					decoration.addOverlay(sharedImages.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING), IDecoration.BOTTOM_RIGHT);
					break;
				case IStatus.ERROR:
					decoration.addOverlay(sharedImages.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR), IDecoration.BOTTOM_RIGHT);
					break;
				default:
					break;
				}
			}
		}

	}

}
