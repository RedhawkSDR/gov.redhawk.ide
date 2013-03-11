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
package gov.redhawk.ide.debug.ui.tabs;

import mil.jpeojtrs.sca.sad.SadPackage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 */
public class LocalWaveformMainTab extends LocalAbstractMainTab {

	@Override
	protected String getProfileExtension() {
		return SadPackage.FILE_EXTENSION;
	}

	@Override
	protected Label createProfileLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.None);
		label.setText("SAD:");
		return label;
	}

}
