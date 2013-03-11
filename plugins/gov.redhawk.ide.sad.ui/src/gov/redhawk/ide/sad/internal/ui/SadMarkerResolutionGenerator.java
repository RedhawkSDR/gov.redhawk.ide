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
package gov.redhawk.ide.sad.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class SadMarkerResolutionGenerator implements IMarkerResolutionGenerator {

	/**
	 * {@inheritDoc}
	 */
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		final List<IMarkerResolution> retVal = new ArrayList<IMarkerResolution>();
		if (SadMarkerResolution.canHandle(marker)) {
			retVal.add(new SadMarkerResolution(marker));
		}
		return retVal.toArray(new IMarkerResolution[retVal.size()]);
	}

}
