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

import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.swt.widgets.Display;

/**
 * Runs Graphiti refresh on the UI thread synchronously for the editing domain, to ensure the model is not modified
 * from another source while refreshing.
 */
public class SynchronizedRefreshBehavior extends DefaultRefreshBehavior {
	public SynchronizedRefreshBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
	}

	@Override
	public void refresh() {
		runInUIThread(new Runnable() {
			@Override
			public void run() {
				doRefresh();
			}
		});
	}

	protected boolean runInUIThread(final Runnable runnable) {
		synchronized (diagramBehavior.getEditingDomain().getCommandStack()) {
			if (Display.getCurrent() == null) {
				Display.getDefault().syncExec(runnable);
			} else {
				runnable.run();
			}
		}
		return true;
	}

	private void doRefresh() {
		super.refresh();
	}
}