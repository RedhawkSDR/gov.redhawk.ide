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
package gov.redhawk.ide.swtbot.condition;

import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForJobs;

import org.eclipse.swtbot.swt.finder.waits.ICondition;

import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

public class JobConditions {

	private JobConditions() {
	}

	/**
	 * Returns a condition that tests if all generate code jobs are finished.
	 * @return
	 */
	public static ICondition generateCode() {
		return waitForJobs(GenerateCode.FAMILY_GENERATE_CODE, "Geneate code");
	}

	/**
	 * Returns a condition that tests if all export to SDR root jobs are finished.
	 * @return
	 */
	public static ICondition exportToSdr() {
		return waitForJobs(SdrUiPlugin.FAMILY_EXPORT_TO_SDR, "Export to SDR");
	}

}
