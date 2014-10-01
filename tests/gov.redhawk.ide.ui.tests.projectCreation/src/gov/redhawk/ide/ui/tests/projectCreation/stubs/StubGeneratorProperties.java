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
package gov.redhawk.ide.ui.tests.projectCreation.stubs;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 6.0
 * 
 */
public final class StubGeneratorProperties {
	public static final String PROP_PACKAGE = "java_package";

	private StubGeneratorProperties() {

	}

	public static String getPackage(final SoftPkg spd, final Implementation impl, final ImplementationSettings implSettings) {
		String pkg = null;
		for (final Property prop : implSettings.getProperties()) {
			if (StubGeneratorProperties.PROP_PACKAGE.equals(prop.getId()) && (prop.getValue() != null) && !"".equals(prop.getValue())) {
				pkg = prop.getValue();
				break;
			}
		}
		if (pkg == null) {
			final String basePkg = StubGeneratorProperties.getBasePackageName(spd);
			pkg = basePkg + "." + CodegenFileHelper.safeGetImplementationName(impl, implSettings);
			if (pkg.endsWith(".")) {
				pkg = pkg.substring(0, pkg.length() - 1);
			}
		}
		return pkg;
	}

	private static String getBasePackageName(final SoftPkg spd) {
		String result = spd.getName();
		result = result.replaceAll(" ", "_");
		return result;
	}

	/**
	 * WARNING - THIS IS FRAGILE BECAUSE THERE IS NOTHING THAT SAYS A GENERATOR TEMPLATE NEEDS
	 * TO PRODUCE A PARTICULAR MAIN CLASS NAME.
	 * 
	 * @param impl
	 * @param implementationSettings
	 * @return
	 */
	public static String getMainClass(final Implementation impl, final ImplementationSettings implementationSettings) {
		final ImplementationSettings implSettings = CodegenUtil.getImplementationSettings(impl);
		final String srcPackage = StubGeneratorProperties.getPackage((SoftPkg) impl.eContainer(), impl, implSettings);
		final SoftPkg spd = (SoftPkg) impl.eContainer();
		final String prefix = CodegenFileHelper.getPreferredFilePrefix((SoftPkg) impl.eContainer(), implSettings);
		return srcPackage + "." + prefix;
	}

}
