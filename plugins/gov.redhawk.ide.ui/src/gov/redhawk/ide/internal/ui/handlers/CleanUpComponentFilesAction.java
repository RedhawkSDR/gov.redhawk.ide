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
package gov.redhawk.ide.internal.ui.handlers;

import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFiles;
import mil.jpeojtrs.sca.partitioning.Partitioning;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;

/**
 * @since 6.0
 */
public class CleanUpComponentFilesAction extends Action {

	private EObject root;

	private EditingDomain editingDomain;

	private boolean dirtied = false;

	private void cleanUpComponentFiles() {
		final EditingDomain localEditingDomain = getEditingDomain(this.root);
		final CompoundCommand command = new CompoundCommand();
		final ComponentFiles compFiles;
		final Partitioning< ? > partitioning;

		if (this.root instanceof SoftwareAssembly) {
			final SoftwareAssembly sad = (SoftwareAssembly) this.root;
			compFiles = sad.getComponentFiles();
			partitioning = sad.getPartitioning();
		} else if (this.root instanceof DeviceConfiguration) {
			final DeviceConfiguration dcd = (DeviceConfiguration) this.root;
			compFiles = dcd.getComponentFiles();
			partitioning = dcd.getPartitioning();
		} else {
			compFiles = null;
			partitioning = null;
		}

		if (compFiles != null && partitioning != null) {
			if (localEditingDomain != null) {
				for (final ComponentFile cf : compFiles.getComponentFile()) {
					if (EcoreUtil.UsageCrossReferencer.find(cf, partitioning).isEmpty()) {
						final SoftPkg spd = cf.getSoftPkg();
						if (spd != null) {
							final Resource spdResource = spd.eResource();
							final Resource scdResource = (spd.getDescriptor() != null && spd.getDescriptor().getComponent() != null) ? spd.getDescriptor().getComponent().eResource() : null;
							final Resource prfResource = (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) ? spd.getPropertyFile().getProperties().eResource() : null;
							command.append(new DeleteCommand(localEditingDomain, Collections.singleton(cf)));
							command.append(new ScaModelCommand() {

								@Override
								public void execute() {
									if (spdResource != null && spdResource.getResourceSet() != null) {
										spdResource.getResourceSet().getResources().remove(spdResource);
									}
									if (scdResource != null) {
										scdResource.getResourceSet().getResources().remove(scdResource);
									}
									if (prfResource != null) {
										prfResource.getResourceSet().getResources().remove(prfResource);
									}
								}
							});
						} else {
							// This should ALMOST never happen.  See issue #115 for details.
							// If the component was deleted from the SDRROOT during a previous instance of the IDE running, the 
							// spd will be null but the component will still be in the SAD model.
							command.append(new DeleteCommand(localEditingDomain, Collections.singleton(cf)));
						}
					}
				}
				if (compFiles.getComponentFile().isEmpty()) {
					command.append(new DeleteCommand(localEditingDomain, Collections.singleton(compFiles)));
				}
			} else {
				final Iterator<ComponentFile> iter = compFiles.getComponentFile().iterator();
				final ArrayList<ComponentFile> removeList = new ArrayList<ComponentFile>();
				while (iter.hasNext()) {
					final ComponentFile cf = iter.next();
					if (EcoreUtil.UsageCrossReferencer.find(cf, partitioning).isEmpty()) {
						removeList.add(cf);
					}
				}

				for (final ComponentFile cf : removeList) {
					final Resource spdResource = cf.getSoftPkg().eResource();
					final Resource scdResource = cf.getSoftPkg().getDescriptor().getComponent().eResource();
					final Resource prfResource = cf.getSoftPkg().getPropertyFile().getProperties().eResource();
					EcoreUtil.delete(cf);
					spdResource.getResourceSet().getResources().remove(spdResource);
					scdResource.getResourceSet().getResources().remove(scdResource);
					prfResource.getResourceSet().getResources().remove(prfResource);
					this.dirtied = true;
				}
				if (compFiles.getComponentFile().isEmpty()) {
					EcoreUtil.delete(compFiles);
				}
			}

			if (localEditingDomain != null) {
				if (command.canExecute()) {
					localEditingDomain.getCommandStack().execute(command);
					this.dirtied = true;
				}
			}
		}
	}

	@Override
	public void run() {
		cleanUpComponentFiles();
	}

	private EditingDomain getEditingDomain(final Object object) {
		if (this.editingDomain == null) {
			return TransactionUtil.getEditingDomain(object);
		}
		return this.editingDomain;
	}

	/**
	 * @since 8.1
	 */
	public void setRoot(final EObject root) {
		this.root = root;
	}

	/**
	 * @deprecated Use {@link #setRoot(EObject)} instead
	 */
	@Deprecated
	public void setSoftwareAssembly(final SoftwareAssembly sad) {
		setRoot(sad);
	}

	/**
	 * @deprecated Use {@link #setRoot(EObject)} instead
	 */
	@Deprecated
	public void setDeviceConfiguration(final DeviceConfiguration dcd) {
		setRoot(dcd);
	}

	public boolean isResourceDirty() {
		return this.dirtied;
	}
}
