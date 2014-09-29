package gov.redhawk.ide.ui.tests.projectcreation;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class GeneralTest extends UITest {
	
	private void testNewProjectWizardAccess(String menuItem, String shellTitle) {
		// Check File new menu shortcut
		bot.menu("File").menu("New").menu(menuItem).click();
		SWTBotShell wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
		
		// Check wizard shortcut
		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
		
		// Check SCA Category
		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem("SCA").expand().getNode(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
		
		// Check context menu
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(view.bot().tree(), "New", "Project..."));
		menu.click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
	}
	
	@Test
	public void testNewComponentProject() {
		testNewProjectWizardAccess("SCA Component Project", "New Component Project");
	}
	
	@Test
	public void testNewWaveformProject() {
		testNewProjectWizardAccess("SCA Waveform Project", "New Waveform Project");
	}
	
	@Test
	public void testNewDeviceProject() {
		testNewProjectWizardAccess("SCA Device Project", "New Device Project");
	}
	
	@Test
	public void testNewServiceProject() {
		testNewProjectWizardAccess("SCA Service Project", "New Service Project");
	}
	
	@Test
	public void testNewNodeProject() {
		testNewProjectWizardAccess("SCA Node Project", "Node Project");
	}
	
	@Test
	public void testNewIDLProject() {
		testNewProjectWizardAccess("SCA IDL Project", "IDL Project");
	}
	
	@Test
	public void testNewControlPanelProject() {
		testNewProjectWizardAccess("SCA Control Panel Project", "New Plug-in Project");
	}
	
}
