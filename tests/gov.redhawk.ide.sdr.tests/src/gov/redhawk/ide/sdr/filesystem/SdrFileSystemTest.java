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
package gov.redhawk.ide.sdr.filesystem;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

public class SdrFileSystemTest {

	@Test
	public void sdr() throws CoreException, URISyntaxException {
		common(ScaFileSystemConstants.SCHEME_TARGET_SDR);
	}

	@Test
	public void sdrdom() throws CoreException, URISyntaxException {
		common(ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM);
	}

	@Test
	public void sdrDom_deprecated() throws CoreException, URISyntaxException {
		common("sdrDom");
	}

	@Test
	public void sdrdev() throws CoreException, URISyntaxException {
		common(ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV);
	}

	@Test
	public void sdrDev_deprecated() throws CoreException, URISyntaxException {
		common("sdrDev");
	}

	private void common(String scheme) throws CoreException, URISyntaxException {
		// We should be able to get the file system from EFS
		IFileSystem fs = EFS.getFileSystem(scheme);
		Assert.assertNotNull(fs);
		Assert.assertEquals("SdrFileSystem", fs.getClass().getSimpleName());

		// We should be able to get a file store for a specific path from EFS
		IFileStore store = EFS.getStore(new URI(scheme, null, "/foo", null));
		Assert.assertNotNull(store);
		Assert.assertEquals("SdrWrappedFileStore", store.getClass().getSimpleName());
		Assert.assertEquals("foo", store.getName());
	}
}
