/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot.diagram;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

public class UsesDeviceTestUtils {

	protected UsesDeviceTestUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The dragUseFrontEndTunerDeviceToDiagram method must be called first.
	 * Completes the uses device wizard with the Listen to Existing Tuner by ID option.
	 * Any number of port names can be provided.
	 * @param bot
	 * @param findByType
	 * @param name
	 * @param provides
	 * @param uses
	 */
	public static void completeUsesDeviceWizard(SWTBot gefBot, String existingAllocId, String newAllocId, String[] provides, String[] uses) {
		SWTBotShell allocateTunerShell = gefBot.shell("Allocate Tuner");
		allocateTunerShell.setFocus();
		// click next, Generic FrontEnd Device already selected
		gefBot.button("&Next >").click();
		// stick with the default values
		gefBot.button("&Next >").click();
		// switch to Listen by id
		SWTBotCombo comboField = gefBot.comboBox(0); // Allocation
		comboField.setFocus();
		comboField.setSelection("Listen to Existing Tuner by Id");
		// provide existing tuner allocation id
		SWTBotText existingTunerAllocationIdText = gefBot.textWithLabel("Existing Tuner Allocation ID");
		existingTunerAllocationIdText.setFocus();
		existingTunerAllocationIdText.typeText(existingAllocId);
		// provide allocation id
		SWTBotText newAllocationIdText = gefBot.textWithLabel("New Allocation ID");
		newAllocationIdText.setFocus();
		newAllocationIdText.typeText(newAllocId);
		gefBot.button("&Next >").click();
		
		//provides ports
		for (int i = 0; i < provides.length; i++) { 
			gefBot.textInGroup("Port(s) to use for connections", 0).setText(provides[i]);
			gefBot.button(0).click(); //add provides port
		}
		
		//uses ports
		for (int i = 0; i < uses.length; i++) { 
			gefBot.textInGroup("Port(s) to use for connections", 1).setText(uses[i]);
			gefBot.button(2).click(); //add uses port
		}
		
		//finish
		gefBot.button("&Finish").click();
	}

}
