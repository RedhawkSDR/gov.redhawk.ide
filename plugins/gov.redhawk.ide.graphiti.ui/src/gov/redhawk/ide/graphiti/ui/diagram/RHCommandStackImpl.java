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
package gov.redhawk.ide.graphiti.ui.diagram;

import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.workspace.EMFCommandOperation;
import org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl;

@SuppressWarnings("restriction")
public class RHCommandStackImpl extends GFWorkspaceCommandStackImpl {

	public RHCommandStackImpl(IOperationHistory history) {
		super(history);
	}

	@Override
	public boolean isSaveNeeded() {
		//If the last command was NonDirtying, return false so that we don't dirty the editor
		IUndoableOperation nextUndoableOperation = getOperationHistory().getUndoOperation(getDefaultUndoContext());
		Command command = null;
		if (nextUndoableOperation instanceof EMFCommandOperation) {
			command = ((EMFCommandOperation) nextUndoableOperation).getCommand();
			if (command instanceof AbstractCommand.NonDirtying) {
				return false;
			}
		}

		return super.isSaveNeeded();
	}

	/*
	 * Override execute just for the special case where another thread owns the current transaction, to ensure that a
	 * new transaction is created to serialize access (see Eclipse Bugzilla bug 477083)
	 */
	@Override
	public void execute(Command command, Map<?, ?> options) throws InterruptedException, RollbackException {
		if (getDomain().getActiveTransaction() != null && getDomain().getActiveTransaction().getOwner() != Thread.currentThread()) {
			InternalTransaction tx = createTransaction(command, options);
			try {
				super.execute(command, options);
			} finally {
				tx.commit();
			}
		} else {
			super.execute(command, options);
		}
	}
}
