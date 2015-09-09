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
package gov.redhawk.ide.ui.tests.properties;

import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class PropertiesTabStructSequenceTest extends PropertiesTabSimpleTest {
	
	@Override
	public void before() throws Exception {
		super.before();
		
		editor.bot().textWithLabel("ID*:").setText("ID");
		editor.bot().sleep(600);
		editor.bot().tree().expandNode("ID").select("Struct");
		editor.bot().textWithLabel("ID*:").setText("Struct");
		editor.bot().sleep(600);
		editor.bot().tree().expandNode("ID", "Struct").select("Simple");
		editor.bot().textWithLabel("ID*:").setText("Simple");
		editor.bot().sleep(600);
		
		selectStructSequence();
	}

	@Override
	protected void createType() {
		editor.bot().button("Add StructSeq").click();
	}
	
	protected void selectStructSequence() {
		editor.bot().tree().select("ID");
	}
	
	protected void selectStruct() {
		editor.bot().tree().expandNode("ID").select("Struct");
	}
	
	protected void selectSimple() {
		editor.bot().tree().expandNode("ID", "Struct").select("Simple");
	}

	@Test
	public void testSelectStructSequence() {
		selectStructSequence();
		assertFormValid();
	}
	
	@Test
	public void testSelectStruct() {
		selectStruct();
		assertFormValid();
	}
	
	@Test
	public void testSimple() {
		selectSimple();
		assertFormValid();
	}

	@Test
	public void testIDSimple() throws CoreException {
		selectSimple();
		super.testID();
	}

	@Test
	public void testNameSimple() {
		selectSimple();
		super.testName();
	}
	
	@Test
	@Override
	public void testValue() throws CoreException {
		selectSimple();
		testValue("Default Value:");
	}
	
	@Test
	@Override
	public void testUnits() {
		selectSimple();
		super.testUnits();
	}
	
	@Test
	@Override
	public void testEnum() throws CoreException {
		selectSimple();
		super.testEnum();
	}
	
	@Test
	@Override
	public void testRange() {
		selectSimple();
		super.testRange();
	}

	@Test
	public void testDescriptionSimple() {
		selectSimple();
		super.testDescription();
	}
	
	@Test
	public void testIDStruct() throws CoreException {
		selectStruct();
		super.testID();
	}

	@Test
	public void testNameStruct() {
		selectStruct();
		super.testName();
	}
	
	@Test
	public void testDescriptionStruct() {
		selectStruct();
		super.testDescription();
	}
	
	@Test
	public void testStructValue() {
		SWTBotTree structValueTable = editor.bot().treeWithLabel("StructValue:");
		editor.bot().button("Add...").click();
		SWTBotTreeItem item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		SWTBotTreeItem subItem = item.getNode("Simple");
		subItem.select();
		
		editor.bot().button("Add...").click();
		SWTBotTreeItem item1 = structValueTable.getTreeItem("Struct[1]");
		item1.expand();
		SWTBotTreeItem subItem1 = item1.getNode("Simple");
		subItem1.select();
		
		StandardTestActions.writeToCell(bot, subItem1, 2, "hello");
		assertFormValid();
		
		item1.select();
		editor.bot().button("Remove", 1).click();
		
		selectSimple();
		editor.bot().comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		assertFormInvalid();
		selectStructSequence();
		
		structValueTable = editor.bot().treeWithLabel("StructValue:");
		item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		subItem = item.getNode("Simple");
		subItem.select();
		
		subItem.click(1);
		bot.sleep(500);
		StandardTestActions.writeToCell(bot, subItem, 2, "1.1");
		assertFormValid();
		
		
	}
	
	@Test
	public void testKind() {
		assertFormValid();
		
		testKind(false);
	}
	
	@Override
	public void testAction() {
		// Disable Action element for Struct sequences
	}
	
}
