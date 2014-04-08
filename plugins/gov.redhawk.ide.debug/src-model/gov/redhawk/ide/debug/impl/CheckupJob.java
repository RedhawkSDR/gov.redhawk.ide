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
// BEGIN GENERATED CODE
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;

/**
 * 
 */
public class CheckupJob extends Job {

	private final IDisposable container;

	public CheckupJob(final IDisposable container) {
		super("Checkup Job");
		setSystem(true);
		this.container = container;
	}

	@Override
	public boolean shouldRun() {
		return super.shouldRun() && !this.container.isDisposed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final List<CorbaObjWrapper< ? >> itemsToCheck = new ArrayList<CorbaObjWrapper< ? >>();
		final List<EObject> itemsToRemove = new ArrayList<EObject>();
		try {
			ScaModelCommand.runExclusive(this.container, new RunnableWithResult.Impl<Object>() {

				@Override
				public void run() {
					for (final TreeIterator<EObject> iterator = container.eAllContents(); iterator.hasNext();) {
						final EObject obj = iterator.next();
						if (obj == container) {
							continue;
						}
						if (obj instanceof CorbaObjWrapper< ? >) {
							final CorbaObjWrapper< ? > wrapper = (CorbaObjWrapper< ? >) obj;
							itemsToCheck.add(wrapper);
						}
						if (obj instanceof LocalFileManager) {
							iterator.prune();
						} else if (obj instanceof ScaComponent) {
							iterator.prune();
						}
					}
				}
			});
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		for (CorbaObjWrapper< ? > obj : itemsToCheck) {
			if (!obj.exists()) {
				itemsToRemove.add(obj);
			}
		}

		if (!itemsToRemove.isEmpty()) {
			ScaModelCommand.execute(this.container, new ScaModelCommand() {

				@Override
				public void execute() {
					for (final EObject obj : itemsToRemove) {
						EcoreUtil.delete(obj);
					}
				}
			});
		}
		if (!monitor.isCanceled()) {
			schedule(5000);
		}
		return Status.OK_STATUS;
	}

}
