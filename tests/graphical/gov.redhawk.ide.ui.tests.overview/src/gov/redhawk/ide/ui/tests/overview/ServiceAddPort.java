package gov.redhawk.ide.ui.tests.overview;

import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Assert;
import org.junit.Test;

/**
 * IDE-980. Ensures that if a service implements an interface that derives from CF/PortSupplier then the button to add a
 * port in the component editor will be enabled.
 */
public class ServiceAddPort extends UITest {

	/**
	 * Tests that the add button is enabled for a service project with the CF/PortSupplier IDL.
	 */
	@Test
	public void testAddForPortSupplier() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/PortSupplier:1.0", "C++");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add...").isEnabled());
	}

	/**
	 * Tests that the add button is enabled for a service project with an IDL type that inherits from CF/PortSupplier.
	 */
	@Test
	public void testAddForPortSupplierChild() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/Device:1.0", "Python");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add...").isEnabled());
	}

	/**
	 * Tests that the add button is disabled for a service project with an IDL type that does not inherit from
	 * CF/PortSupplier.
	 */
	@Test
	public void testAddForNonPortSupplier() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/LifeCycle:1.0", "Java");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertFalse("Add port button should be disabled", editorBot.button("Add...").isEnabled());
	}

}
