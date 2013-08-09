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
package gov.redhawk.ide.snapshot.internal.ui;

import gov.redhawk.ide.snapshot.ui.handlers.SnapshotWizard;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.sad.diagram.edit.parts.UsesPortStubEditPart;
import gov.redhawk.sca.util.PluginUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import BULKIO.dataCharHelper;
import BULKIO.dataDoubleHelper;
import BULKIO.dataFloatHelper;
import BULKIO.dataLongHelper;
import BULKIO.dataLongLongHelper;
import BULKIO.dataOctetHelper;
import BULKIO.dataShortHelper;
import BULKIO.dataUlongHelper;
import BULKIO.dataUlongLongHelper;
import BULKIO.dataUshortHelper;

public class SnapshotHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SnapshotHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;

			Object obj = ss.getFirstElement();
			if (obj instanceof ScaUsesPort && checkPort((ScaUsesPort) obj)) {
				SnapshotWizard wizard = new SnapshotWizard((ScaUsesPort) obj);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			} else if (obj instanceof UsesPortStubEditPart) {
				//get ScaUsesPort from UsesPortStubEditPart and continue
				ScaUsesPort port = PluginUtil.adapt(ScaUsesPort.class, (UsesPortStubEditPart) obj);
				if (this.checkPort(port)) {
					SnapshotWizard wizard = new SnapshotWizard(port);
					WizardDialog dialog = new WizardDialog(shell, wizard);
					dialog.open();
				}
			}
		}
		return null;
	}

	private boolean checkPort(ScaUsesPort port) {
		if (port.getRepid().equals(dataCharHelper.id()) || port.getRepid().equals(dataDoubleHelper.id()) || port.getRepid().equals(dataFloatHelper.id())
			|| port.getRepid().equals(dataLongHelper.id()) || port.getRepid().equals(dataLongLongHelper.id()) || port.getRepid().equals(dataOctetHelper.id())
			|| port.getRepid().equals(dataShortHelper.id()) || port.getRepid().equals(dataUlongHelper.id()) || port.getRepid().equals(dataUlongLongHelper.id())
			|| port.getRepid().equals(dataUshortHelper.id())) {
			return true;
		}
		return false;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		final IEvaluationContext context = (IEvaluationContext) evaluationContext;
		Object obj = context.getVariable("activeMenuSelection");
		if (obj instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) obj;
			Object element = ss.getFirstElement();
			if (element instanceof ScaUsesPort) {
				setBaseEnabled(checkPort((ScaUsesPort) element));
				return;
			} else if (element instanceof UsesPortStubEditPart) {
				//get ScaUsesPort from UsesPortStubEditPart and continue
				ScaUsesPort port = PluginUtil.adapt(ScaUsesPort.class, (UsesPortStubEditPart) element);
				setBaseEnabled(checkPort(port));
				return;
			}
		}
		setBaseEnabled(false);
	}
}
