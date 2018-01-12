/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/* package */ class FindByPortLabelProvider extends ColumnLabelProvider {

	private static final String SCD_EDIT_PLUGIN_ID = "mil.jpeojtrs.sca.scd.edit"; //$NON-NLS-1$

	private Image icon;

	public FindByPortLabelProvider(String scdEditIconPath) {
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(SCD_EDIT_PLUGIN_ID, scdEditIconPath);
		icon = desc.createImage();
	}

	@Override
	public String getText(Object element) {
		return (String) element;
	}

	@Override
	public Image getImage(Object element) {
		return icon;
	}

	@Override
	public void dispose() {
		super.dispose();
		icon.dispose();
	}
}
