package gov.redhawk.ide.ui.tests.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Ignore;
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
		
		SWTBotTable kindTable = editor.bot().tableWithLabel("Kind:");
		Assert.assertFalse("Structs don't support kind type execparam." , kindTable.containsItem("execparam"));
		
		kindTable.getTableItem("configure (default)").check();
		assertFormValid();
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("configure (default)").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").uncheck();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();

		
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("configure (default)").uncheck();
		kindTable.getTableItem("message").check();
		assertFormValid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
	}
	
	
	/**
	 * Ignore for now until we figure out why the context menu doesn't want to work.
	 */
	@Ignore
	@Test
	public void testAddSecondSimple() {
		selectStruct();
		editor.bot().tree().contextMenu("New").menu("Simple").click();
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
		selectStruct();
		super.testMode();
	}


}
