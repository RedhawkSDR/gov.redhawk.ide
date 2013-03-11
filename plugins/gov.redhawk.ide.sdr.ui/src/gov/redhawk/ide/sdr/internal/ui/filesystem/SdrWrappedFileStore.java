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
package gov.redhawk.ide.sdr.internal.ui.filesystem;

import gov.redhawk.sca.efs.WrappedFileStore;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;

public class SdrWrappedFileStore extends WrappedFileStore {

	public SdrWrappedFileStore(final URI uri, final IFileStore fileStore) {
		super(uri, fileStore);
	}

	@Override
	protected WrappedFileStore createFileStore(final URI uri, final IFileStore child) {
		return new SdrWrappedFileStore(uri, child);
	}

}
