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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.Test;

public class PropertiesTabStructTest extends PropertiesTabSimpleTest {

	@Override
	protected void createType() {
		bot.button("Add Struct").click();
	}
	
	@Override
	public void before() throws Exception {
		super.before();
		
		selectSimple();
		editor.bot().textWithLabel("ID*:").setText("Simple");
		selectStruct();
	}
	
	protected void selectStruct() {
		editor.bot().tree().getTreeItem("ID").select();
	}
	
	protected void selectSimple() {
		editor.bot().tree().getTreeItem("ID").expand().getNode("Simple").select();
	}

	@Test
	public void testSimpleName() {
		selectSimple();
		super.testName();
	}
	
	@Test
	public void testSimpleID() throws CoreException {
		selectSimple();
		super.testID();
	}
	
	@Test
	@Override
	public void testValue() throws CoreException {
		selectSimple();
		super.testValue();
	}
	
	@Test
	@Override
	public void testEnum() throws CoreException {
		selectSimple();
		super.testEnum();
	}
	
	@Test
	@Override
	public void testUnits() {
		selectSimple();
		super.testUnits();
	}
	
	@Test
	@Override
	public void testRange() {
		selectSimple();
		super.testRange();
	}
	
	@Test
	public void testSimpleDescription() {
		selectSimple();
		super.testDescription();
	}
	
	@Override
	public void testUniqueID() {
		selectSimple();
		editor.bot().textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID");
		assertFormValid();
		
		super.testUniqueID();
	}
	
	@Test
	public void testKind() {
		assertFormValid();
		selectStruct();
		
		testKind(false);
	}
	
	

	@Test
	public void testAddSecondSimple() {
		bot.waitUntil(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				selectStruct();
				new SWTBotMenu(ContextMenuHelper.contextMenu(editor.bot().tree(), "New", "Simple")).click();
				return true;
			}
			
			@Override
			public String getFailureMessage() {
				return "Could not find context menu: New->Simple";
			}
		});
		
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID2");
		assertFormValid();
	}
	
	@Override
	public void testAction() {
		// No Action element available for structs or simples within structs
	}
	
	@Test
	public void testMode() {
		bot.sleep(500);
		selectStruct();
		bot.sleep(500);
		super.testMode();
	}


}
