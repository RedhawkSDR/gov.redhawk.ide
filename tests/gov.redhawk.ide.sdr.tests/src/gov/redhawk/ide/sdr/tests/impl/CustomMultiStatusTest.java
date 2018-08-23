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
package gov.redhawk.ide.sdr.tests.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.impl.CustomMultiStatus;

public class CustomMultiStatusTest {

	@Test
	public void test() {
		IStatus[] children = new IStatus[] { //
			new Status(IStatus.WARNING, "j.k.l", "mno"), //
			Status.OK_STATUS, //
			new Status(Status.ERROR, "v.w", "xyz") };

		MultiStatus status = new CustomMultiStatus("a.b.c", 1, "def", new Exception("ghi"));
		status.add(children[0]);
		status.add(children[1]);
		status.add(children[2]);
		checkStatus(status);

		status = new CustomMultiStatus("a.b.c", 1, children, "def", new Exception("ghi"));
		checkStatus(status);
	}

	private void checkStatus(IStatus status) {
		Assert.assertEquals("a.b.c", status.getPlugin());
		Assert.assertEquals(1, status.getCode());
		Assert.assertEquals("def", status.getMessage());
		Assert.assertEquals("ghi", status.getException().getMessage());

		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertEquals(IStatus.WARNING, status.getChildren()[0].getSeverity());
		Assert.assertEquals("j.k.l", status.getChildren()[0].getPlugin());
		Assert.assertEquals("mno", status.getChildren()[0].getMessage());

		Assert.assertEquals(IStatus.ERROR, status.getChildren()[1].getSeverity());
		Assert.assertEquals("v.w", status.getChildren()[1].getPlugin());
		Assert.assertEquals("xyz", status.getChildren()[1].getMessage());
	}

}
