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
package gov.redhawk.ide.swtbot.tests.utils;

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByServicePattern;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class FindByUtils {

	public static final String FIND_BY_CORBA_NAME = FindByCORBANamePattern.NAME, FIND_BY_SERVICE = FindByServicePattern.NAME, 
			FIND_BY_DOMAIN_MANAGER = FindByDomainManagerPattern.NAME, FIND_BY_EVENT_CHANNEL = FindByEventChannelPattern.NAME, 
			FIND_BY_FILE_MANAGER = FindByFileManagerPattern.NAME;
	
	protected FindByUtils() {
	}

	/**
	 * Add a FindBy object to the Diagram from the palette
	 * @param editor
	 * @param findByType
	 */
	public static void completeFindByWizard(SWTBot bot, String findByType) {
		String[] provides = {"p1", "p2"};
		String[] uses = {"u1", "u2"};
		String name = removeSpaces(findByType);
		switch (findByType) {
		case FIND_BY_CORBA_NAME:
			addFindByCorbaName(bot, name, provides, uses);
			break;
		case FIND_BY_EVENT_CHANNEL:
			addFindByEventChannel(bot, name);
			break;
		case FIND_BY_SERVICE:
			addFindByService(bot, name, provides, uses);
			break;
		default:
			break;
		}
	}
	
	public static void addFindByCorbaName(SWTBot bot, String name, String[] providesPortNames, String[] usesPortNames) {
		bot.textWithLabel("CORBA Name:").setText(name);
		for (String s : providesPortNames) {
			bot.textInGroup("Port Options", 0).setText(s);
			bot.button("Add Provides Port").click();
		}
		for (String s : usesPortNames) {
			bot.textInGroup("Port Options", 1).setText(s);
			bot.button("Add Uses Port").click();
		}
		bot.button("Finish").click();
	}
	
	public static void addFindByService(SWTBot bot, String name, String[] providesPortNames, String[] usesPortNames) {
		bot.textWithLabel("Service Name:").setText(name);
		for (String s : providesPortNames) {
			bot.textInGroup("Port Options", 0).setText(s);
			bot.button("Add Provides Port").click();
		}
		for (String s : usesPortNames) {
			bot.textInGroup("Port Options", 1).setText(s);
			bot.button("Add Uses Port").click();
		}
		bot.button("Finish").click();
	}
	
	public static void addFindByEventChannel(SWTBot bot, String name) {
		bot.textWithLabel("Event Channel:").setText(name);
		bot.button("OK").click();
	}
	
	public static String getFindByDefaultName(String findByType) {
		if (findByType.equals(FIND_BY_CORBA_NAME) || findByType.equals(FIND_BY_EVENT_CHANNEL) || findByType.equals(FIND_BY_SERVICE)) {
			return removeSpaces(findByType);
		}
		return findByType;
	}
	
	private static String removeSpaces(String str) {
		if (str.contains(" ")) {
			return str.replaceAll(" ", "");
		}
		return str;
	}
	
}
