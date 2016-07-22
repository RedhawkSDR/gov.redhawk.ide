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
package gov.redhawk.ide.sdr.ui.navigator;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrLabelProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.sca.efs.WrappedFileStore;
import gov.redhawk.sca.ui.ITooltipProvider;

/**
 * @since 4.2
 */
public class SdrNavigatorLabelProvider extends SdrLabelProvider implements IDescriptionProvider, ICommonLabelProvider, ITooltipProvider {

	@Override
	public String getDescription(final Object anElement) {
		if (anElement instanceof EObject) {
			final EObject eObj = (EObject) anElement;
			final IItemLabelProvider provider = (IItemLabelProvider) getAdapterFactory().adapt(eObj, IItemLabelProvider.class);
			return provider.getText(anElement);
		}
		return null;
	}

	@Override
	public void restoreState(final IMemento aMemento) {

	}

	@Override
	public void saveState(final IMemento aMemento) {

	}

	@Override
	public void init(final ICommonContentExtensionSite aConfig) {

	}

	@Override
	public Image getToolTipImage(final Object object) {
		if (object instanceof SdrRoot) {
			final SdrRoot root = (SdrRoot) object;
			final IStatus status = root.getLoadStatus();
			if (status != null && !status.isOK()) {
				switch (status.getSeverity()) {
				case IStatus.INFO:
					return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
				case IStatus.WARNING:
					return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
				case IStatus.ERROR:
					return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
				default:
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public String getToolTipText(final Object element) {
		if (element instanceof SdrRoot) {
			final SdrRoot root = (SdrRoot) element;
			final IStatus status = root.getLoadStatus();
			if (status != null && !status.isOK()) {
				if (status.isMultiStatus()) {
					final IStatus[] children = status.getChildren();
					if (children.length == 1) {
						return children[0].getMessage();
					}
				}
				return status.getMessage();
			}
		} else if (element instanceof EObject) {
			final EObject object = (EObject) element;
			IFileStore store = null;

			if (object.eResource() != null) {
				try {
					store = EFS.getStore(new java.net.URI(object.eResource().getURI().toString()));
				} catch (final CoreException e) {
					SdrUiPlugin.getDefault().logError("Unable to get file store for resource.");
				} catch (final URISyntaxException e) {
					SdrUiPlugin.getDefault().logError("Unable to resolve file store for resource.");
				}
			}

			store = WrappedFileStore.unwrap(store);
			if (store != null) {
				try {
					final File localFile = store.toLocalFile(0, new NullProgressMonitor());
					if (localFile != null) {
						final String filePath = localFile.toURI().getPath();

						if (filePath != null) {
							return filePath;
						}
					}
				} catch (final CoreException e) {
					SdrUiPlugin.getDefault().logError("Unable to get path for file store: " + store, e);
				}
			}
		}

		return null;
	}

	@Override
	public Color getToolTipBackgroundColor(final Object object) {
		return null;
	}

	@Override
	public Color getToolTipForegroundColor(final Object object) {
		return null;
	}

	@Override
	public Font getToolTipFont(final Object object) {
		return null;
	}

	@Override
	public Point getToolTipShift(final Object object) {
		return new Point(5, 5); // SUPPRESS CHECKSTYLE MagicNumber
	}

	@Override
	public boolean useNativeToolTip(final Object object) {
		return false;
	}

	@Override
	public int getToolTipTimeDisplayed(final Object object) {
		return 5000; // SUPPRESS CHECKSTYLE MagicNumber
	}

	@Override
	public int getToolTipDisplayDelayTime(final Object object) {
		return 0;
	}

	@Override
	public int getToolTipStyle(final Object object) {
		return 0;
	}

}
