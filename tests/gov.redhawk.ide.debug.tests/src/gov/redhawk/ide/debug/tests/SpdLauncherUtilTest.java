/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.debug.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;

import CF.DataType;
import CF.InvalidIdentifier;
import CF.InvalidObjectReference;
import CF.PropertiesHolder;
import CF.PropertyEmitterOperations;
import CF.UnknownProperties;
import CF.PropertyEmitterPackage.AlreadyInitialized;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.util.SdrPluginLoader;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class SpdLauncherUtilTest {

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";
	private static final String TEST_SDR_PATH = "testFiles/sdr";

	private static SdrRoot sdrRoot;

	@BeforeClass
	public static void loadSdrRoot() throws URISyntaxException, IOException {
		sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);
	}

	@Test
	public void initializeProperties() {
		final String[] ALL_PROPS = new String[] { "simple_no_value", "simple_with_value", "simpleseq_no_values", "simpleseq_values", "struct_novalue1",
			"struct_novalue2", "struct_partialconfig", "struct_value1", "struct_value2", "structseq_no_values", "structseq_with_values" };
		final String[] INIT_PROPS = new String[] { "simple_with_value", "simpleseq_no_values", "simpleseq_values", "struct_value1", "struct_value2",
			"structseq_no_values", "structseq_with_values" };

		// Load property values for a component
		final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/resources/initializeProperties/initializeProperties.spd.xml", true);
		propHolder.setProfileURI(spdUri);
		propHolder.fetchProfileObject(new NullProgressMonitor());
		propHolder.fetchProperties(new NullProgressMonitor());

		// Sanity check - all props are present
		for (String id : ALL_PROPS) {
			String msg = String.format("Didn't find property '%s' after loading", id);
			Assert.assertNotNull(msg, propHolder.getProperty(id));
		}

		// Simulate a component and call initializeProperties on it
		final Map<String, Any> receivedProps = new HashMap<>();
		PropertyEmitterOperations propEmitter = new PropertyEmitterOperations() {

			@Override
			public void configure(DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
			}

			@Override
			public void query(PropertiesHolder configProperties) throws UnknownProperties {
			}

			@Override
			public void initializeProperties(DataType[] initialProperties) throws AlreadyInitialized, InvalidConfiguration, PartialConfiguration {
				for (DataType dt : initialProperties) {
					receivedProps.put(dt.id, dt.value);
				}
			}

			@Override
			public String registerPropertyListener(Object obj, String[] propIds, float interval) throws UnknownProperties, InvalidObjectReference {
				return null;
			}

			@Override
			public void unregisterPropertyListener(String id) throws InvalidIdentifier {
			}

		};
		SpdLauncherUtil.initializeProperties(propEmitter, propHolder, null, null, new NullProgressMonitor());

		// Verify the correct properties were passed to initializeProperties()
		for (String id : INIT_PROPS) {
			String msg = String.format("Property '%s' was not passed to initializeProperties()", id);
			Assert.assertNotNull(msg, receivedProps.remove(id));
		}
		Assert.assertEquals(0, receivedProps.size());
	}

	/**
	 * IDE-1445 Test that XML validation catches missing PRF and SCD files that are referenced.
	 */
	@Test
	public void validateAllXML_SpdMissingPrfAndScd() {
		SoftPkg spd = getSpd(sdrRoot, "SpdMissingPrfAndScd");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Errors in SPD for component SpdMissingPrfAndScd", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("Invalid Property File"));

		Assert.assertTrue(status.getChildren()[1].getMessage().contains("Invalid Component File"));
	}

	/**
	 * IDE-1445 Test that XML validation catches errors in the SPD file.
	 */
	@Test
	public void validateAllXML_SpdWithErrors() {
		SoftPkg spd = getSpd(sdrRoot, "SpdWithErrors");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Errors in SPD for component SpdWithErrors", status.getMessage());
		Assert.assertEquals(1, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("author"));
	}

	/**
	 * IDE-1445 Test that XML validation catches errors in dependencies (PRFs, SCDs).
	 */
	@Test
	public void validateAllXML_SpdWithPrfAndScdErrors() {
		SoftPkg spd = getSpd(sdrRoot, "SpdWithPrfAndScdErrors");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Some XML file(s) have errors", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertEquals("Errors in PRF for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.prf.xml)", status.getChildren()[0].getMessage());
		Assert.assertEquals(1, status.getChildren()[0].getChildren().length);

		Assert.assertEquals("Errors in SCD for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.scd.xml)", status.getChildren()[1].getMessage());
		Assert.assertEquals(1, status.getChildren()[1].getChildren().length);
	}

	private SoftPkg getSpd(SdrRoot sdrRoot, String name) {
		for (SoftPkg spd : sdrRoot.getComponentsContainer().getComponents()) {
			if (name.equals(spd.getName())) {
				return spd;
			}
		}
		return null;
	}
}
