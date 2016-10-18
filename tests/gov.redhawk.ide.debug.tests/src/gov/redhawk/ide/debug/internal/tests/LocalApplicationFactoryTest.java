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
package gov.redhawk.ide.debug.internal.tests;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;

import CF.DataType;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;

public class LocalApplicationFactoryTest {

	/**
	 * IDE-1703 Ensure we suffix our waveform naming contexts with "_n"
	 * @throws CoreException
	 * @throws NotEmpty
	 */
	@Test
	public void waveformContextNumbering() throws CoreException, NotEmpty {
		ScaDebugInstance.INSTANCE.init(null);
		LocalSca localSca = ScaDebugInstance.INSTANCE.getLocalSca();
		ILaunch launch = null;
		LocalApplicationFactory factory = new LocalApplicationFactory(new HashMap<String, String>(), localSca, "run", launch,
			new HashMap<String, List<DataType>>());
		NotifyingNamingContext abc1 = factory.createWaveformContext(localSca.getRootContext(), "abc");
		NotifyingNamingContext abc2 = factory.createWaveformContext(localSca.getRootContext(), "abc");
		Assert.assertEquals("abc_1", abc1.getName());
		Assert.assertEquals("abc_2", abc2.getName());
		abc1.destroy();
		abc2.destroy();
	}

}
