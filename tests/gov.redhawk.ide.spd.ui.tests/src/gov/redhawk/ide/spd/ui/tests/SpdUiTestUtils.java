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
package gov.redhawk.ide.spd.ui.tests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;

public class SpdUiTestUtils {

	private static int nameIndex = 0;

	private SpdUiTestUtils() {
		//Prevent instantiation
	}

	public static URI getURI(final String filePath) throws IOException {
		final URL url = FileLocator.toFileURL(FileLocator.find(Platform.getBundle("gov.redhawk.ide.spd.ui.tests"), new Path(filePath), null));
		return URI.createURI(url.toString());
	}

	public static Provides createProvides(final String repID) {
		final Provides provides = ScdFactory.eINSTANCE.createProvides();
		provides.setRepID(repID);
		final Interface iFace = ScdFactory.eINSTANCE.createInterface();
		iFace.setRepid(repID);
		provides.setInterface(iFace);
		provides.setProvidesName(getPortName());
		provides.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(PortType.DATA));
		return provides;
	}

	public static Uses createUses(final String repID) {
		final Uses uses = ScdFactory.eINSTANCE.createUses();
		final Interface iFace = ScdFactory.eINSTANCE.createInterface();
		iFace.setRepid(repID);
		uses.setInterface(iFace);
		uses.setRepID(repID);
		uses.setUsesName(getPortName());
		uses.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(PortType.DATA));
		return uses;
	}

	public static void testNoDuplicateOrMissingInterfaces(Ports ports, SoftwareComponent scd) {
		List<Interface> myList = scd.getInterfaces().getInterface();
		//Test for missing 
		for (Provides provides : ports.getProvides()) {
			List<String> repIds = new ArrayList<String>();
			for (Interface iFace : myList) {
				repIds.add(iFace.getRepid());
			}
			Assert.assertEquals("Expected to find the interface: " + provides.getRepID() + " in the SoftwareComponent interface list", true,
			        repIds.contains(provides.getRepID()));
		}
		//Test for duplicate
		for (Interface iFace : myList) {
			int equalsCount = 0;
			for (Interface other : myList) {
				if (other.equals(iFace)) {
					equalsCount++;
				}
				Assert.assertEquals("Should be only be one " + iFace.getRepid() + " interface in the SoftwareComponent", true, equalsCount <= 1);
			}

			//Test for missing inherited
			for (InheritsInterface inheritsInterface : iFace.getInheritsInterfaces()) {
				Assert.assertEquals("Expected to find the inherited interface: " + inheritsInterface.getRepid() + " in the SoftwareComponent interface list",
				        true, myList.contains(inheritsInterface.getInterface()));
			}
		}
	}

	private static String getPortName() {
		return "testPort" + ++nameIndex;
	}
}
