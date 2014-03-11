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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.layout;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ZestLayoutAlgorithmnChoiceDialog extends ElementListSelectionDialog {
	public ZestLayoutAlgorithmnChoiceDialog(Shell parent) {
		super(parent, new LabelProvider() {
			public String getText(Object element) {
				Integer idx = (Integer) element;
				return ZestLayoutDiagramFeature.getLayouts().get(idx - 1);
			}
		});
		Object[] elements = new Object[ZestLayoutDiagramFeature.getLayouts().size()];
		for (int i = 0; i < ZestLayoutDiagramFeature.getLayouts().size(); i++) {
			elements[i] = Integer.valueOf(i + 1);
		}
		setElements(elements);
		setTitle("Select Layout");
		setMultipleSelection(false);
	}

	@Override
	public int open() {
		int result = super.open();
		if (result < 0) {
			return result;
		}
		return (Integer) getFirstResult();
	}

}
