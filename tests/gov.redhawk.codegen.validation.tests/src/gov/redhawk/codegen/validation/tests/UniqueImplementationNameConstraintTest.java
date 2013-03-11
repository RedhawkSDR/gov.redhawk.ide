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
package gov.redhawk.codegen.validation.tests;

import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UniqueImplementationNameConstraintTest {

	private ResourceSet resourceSet;
	private WaveDevSettings waveSettings;

	@Before
	public void setUp() throws Exception {
		final ResourceSet set = new ResourceSetImpl();
		this.resourceSet = set;
	}

	@After
	public void tearDown() throws Exception {
		this.resourceSet = null;
		this.waveSettings = null;
	}

	@Test
	public void testUniqueImplementationNameConstraints() throws Exception {
		this.waveSettings = (WaveDevSettings) this.resourceSet.getResource(UniqueImplementationNameConstraintTest.getURI("testFiles/component.wavedev"), true)
		        .getEObject("/");

		final Collection<ImplementationSettings> implSettingsColl = this.waveSettings.getImplSettings().values();
		final List<ImplementationSettings> myList = new ArrayList<ImplementationSettings>();
		myList.addAll(implSettingsColl);
		final Set<String> nameSet = new HashSet<String>();
		for (final ImplementationSettings i : myList) {
			final String name = i.getName();
			if (nameSet.contains(name)) {
				Assert.fail();
				break;
			} else {
				nameSet.add(name);
			}
		}
	}

	@Test
	public void testNotUniqueImplementationNameConstraints() throws Exception {
		this.waveSettings = (WaveDevSettings) this.resourceSet.getResource(
		        UniqueImplementationNameConstraintTest.getURI("testFiles/notUniqueComponent.wavedev"), true).getEObject("/");
		boolean duplicateName = false;

		final Collection<ImplementationSettings> implSettingsColl = this.waveSettings.getImplSettings().values();
		final List<ImplementationSettings> myList = new ArrayList<ImplementationSettings>();
		myList.addAll(implSettingsColl);
		final Set<String> nameSet = new HashSet<String>();
		for (final ImplementationSettings i : myList) {
			final String name = i.getName();

			if (nameSet.contains(name)) {
				duplicateName = true;
				break;
			} else {
				nameSet.add(name);
			}
		}

		if (!duplicateName) {
			Assert.fail();
		}
	}

	public static URI getURI(final String filePath) throws IOException {
		final URL url = FileLocator.toFileURL(FileLocator.find(Platform.getBundle("gov.redhawk.codegen.validation.tests"), new Path(filePath), null));
		return URI.createURI(url.toString());
	}
}
