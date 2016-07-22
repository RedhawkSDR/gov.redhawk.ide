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

import gov.redhawk.eclipsecorba.library.IdlLibrary;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @since 4.2
 * 
 */
public class SdrViewerSorter extends ViewerSorter {

	/**
	 * 
	 */
	public SdrViewerSorter() {
	}

	/**
	 * @param collator
	 */
	public SdrViewerSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if (e1 instanceof IdlLibrary) {
			if (e2 instanceof IdlLibrary) {
				return super.compare(viewer, e1, e2);
			} else {
				return -1;
			}
		} else if (e2 instanceof IdlLibrary) {
			return 1;
		}
		return super.compare(viewer, e1, e2);
	}

}
