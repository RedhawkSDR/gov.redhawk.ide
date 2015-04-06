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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import gov.redhawk.ide.graphiti.ui.diagram.util.PaletteFilterUtil;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class ChalkboardPaletteTest extends AbstractGraphitiChalkboardTest {

	private SWTBotGefEditor editor;

	/**
	 * 
	 */
	@Test
	public void testPaletteFilter() {
		final String component1 = "SigGen";
		final String component2 = "HardLimit";
		// IDE-1112: test presence of namespaced component in palette
		final String component3 = "name.space.comp";

		final String errorMissing1 = "Component " + component1 + " is missing from the palette";
		final String errorMissing2 = "Component " + component2 + " is missing from the palette";
		final String errorMissing3 = "Component " + component3 + " is missing from the palette";
		final String errorShown1 = "Component " + component1 + " should be filtered out of the palette";
		final String errorShown2 = "Component " + component2 + " should be filtered out of the palette";
		final String errorShown3 = "Component " + component3 + " should be filtered out of the palette";

		editor = openChalkboardDiagram(gefBot);

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteFilterUtil.setFilter(editor, "s");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
		
		PaletteFilterUtil.setFilter(editor, "sh");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteFilterUtil.setFilter(editor, "h");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteFilterUtil.setFilter(editor, ".");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteFilterUtil.setFilter(editor, "");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
	}
	
	/**
	 * 
	 */
	@Test
	public void testMultipleImplementations() {
		final String component1 = "SigGen";
		final String component2 = "HardLimit";

		editor = openChalkboardDiagram(gefBot);
		Assert.assertTrue(PaletteUtils.hasMultipleImplementations(editor, component1));
		Assert.assertTrue(PaletteUtils.hasMultipleImplementations(editor, component2));
	}
	
}
