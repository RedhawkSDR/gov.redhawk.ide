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

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.swt.widgets.Display;

/**
 * Runs Graphiti refresh in a read-only transaction on the UI thread to ensure that the model is not modified from
 * another source while refreshing.
 */
public class RunExclusiveRefreshBehavior extends DefaultRefreshBehavior {
	public RunExclusiveRefreshBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
	}

	@Override
	public void refresh() {
		runExclusiveInUIThread(new Runnable() {
			@Override
			public void run() {
				doRefresh();
			}
		});
	}

	protected boolean runExclusiveInUIThread(final Runnable runnable) {
		try {
			final TransactionalEditingDomain editingDomain = diagramBehavior.getEditingDomain();
			editingDomain.runExclusive(new Runnable() {
				@Override
				public void run() {
					if (Display.getCurrent() == null) {
						Runnable wrapped = editingDomain.createPrivilegedRunnable(runnable);
						Display.getDefault().syncExec(wrapped);
					} else {
						runnable.run();
					}
				}
			});
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	private void doRefresh() {
		super.refresh();
	}
}