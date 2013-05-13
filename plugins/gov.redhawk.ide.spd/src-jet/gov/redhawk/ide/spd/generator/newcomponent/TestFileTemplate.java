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
// BEGIN GENERATED CODE
package gov.redhawk.ide.spd.generator.newcomponent;

import gov.redhawk.ide.spd.generator.newcomponent.GeneratorArgs;

public class TestFileTemplate
{
  protected static String nl;
  public static synchronized TestFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    TestFileTemplate result = new TestFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "#!/usr/bin/env python" + NL + "import unittest" + NL + "import ossie.utils.testing" + NL + "import os" + NL + "from omniORB import any" + NL + "" + NL + "class ComponentTests(ossie.utils.testing.ScaComponentTestCase):" + NL + "    \"\"\"Test for all component implementations in ";
  protected final String TEXT_2 = "\"\"\"" + NL + "" + NL + "    def testScaBasicBehavior(self):" + NL + "        #######################################################################" + NL + "        # Launch the component with the default execparams" + NL + "        execparams = self.getPropertySet(kinds=(\"execparam\",), modes=(\"readwrite\", \"writeonly\"), includeNil=False)" + NL + "        execparams = dict([(x.id, any.from_any(x.value)) for x in execparams])" + NL + "        self.launch(execparams)" + NL + "        " + NL + "        #######################################################################" + NL + "        # Verify the basic state of the component" + NL + "        self.assertNotEqual(self.comp, None)" + NL + "        self.assertEqual(self.comp.ref._non_existent(), False)" + NL + "        self.assertEqual(self.comp.ref._is_a(\"IDL:CF/Resource:1.0\"), True)" + NL + "        # self.assertEqual(self.spd.get_id(), self.comp.ref._get_identifier())" + NL + "        " + NL + "        #######################################################################" + NL + "        # Simulate regular component startup" + NL + "        # Verify that initialize nor configure throw errors" + NL + "        self.comp.initialize()" + NL + "        configureProps = self.getPropertySet(kinds=(\"configure\",), modes=(\"readwrite\", \"writeonly\"), includeNil=False)" + NL + "        self.comp.configure(configureProps)" + NL + "        " + NL + "        #######################################################################" + NL + "        # Validate that query returns all expected parameters" + NL + "        # Query of '[]' should return the following set of properties" + NL + "        expectedProps = []" + NL + "        expectedProps.extend(self.getPropertySet(kinds=(\"configure\", \"execparam\"), modes=(\"readwrite\", \"readonly\"), includeNil=True))" + NL + "        expectedProps.extend(self.getPropertySet(kinds=(\"allocate\",), action=\"external\", includeNil=True))" + NL + "        props = self.comp.query([])" + NL + "        props = dict((x.id, any.from_any(x.value)) for x in props)" + NL + "        # Query may return more than expected, but not less" + NL + "        for expectedProp in expectedProps:" + NL + "            self.assertEquals(props.has_key(expectedProp.id), True)" + NL + "        " + NL + "        #######################################################################" + NL + "        # Verify that all expected ports are available" + NL + "        for port in self.scd.get_componentfeatures().get_ports().get_uses():" + NL + "            port_obj = self.comp.getPort(str(port.get_usesname()))" + NL + "            self.assertNotEqual(port_obj, None)" + NL + "            self.assertEqual(port_obj._non_existent(), False)" + NL + "            self.assertEqual(port_obj._is_a(\"IDL:CF/Port:1.0\"),  True)" + NL + "            " + NL + "        for port in self.scd.get_componentfeatures().get_ports().get_provides():" + NL + "            port_obj = self.comp.getPort(str(port.get_providesname()))" + NL + "            self.assertNotEqual(port_obj, None)" + NL + "            self.assertEqual(port_obj._non_existent(), False)" + NL + "            self.assertEqual(port_obj._is_a(port.get_repid()),  True)" + NL + "            " + NL + "        #######################################################################" + NL + "        # Make sure start and stop can be called without throwing exceptions" + NL + "        self.comp.start()" + NL + "        self.comp.stop()" + NL + "        " + NL + "        #######################################################################" + NL + "        # Simulate regular component shutdown" + NL + "        self.comp.releaseObject()" + NL + "        " + NL + "    # TODO Add additional tests here" + NL + "    #" + NL + "    # See:" + NL + "    #   ossie.utils.bulkio.bulkio_helpers," + NL + "    #   ossie.utils.bluefile.bluefile_helpers" + NL + "    # for modules that will assist with testing components with BULKIO ports" + NL + "    " + NL + "if __name__ == \"__main__\":" + NL + "    ossie.utils.testing.main(\"../";
  protected final String TEXT_3 = "\") # By default tests all implementations";
  protected final String TEXT_4 = NL;

    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     GeneratorArgs args = (GeneratorArgs)argument; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getProjectName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getSoftPkgFile());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE