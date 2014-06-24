package gov.redhawk.ide.swtbot.tests.editor;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;


public class EditorUtils {
	/**
	 * @param waveformName - name of waveform that will be opened
	 */
	public void openSadDiagram(String waveformName) {
		// TODO put code here that will open 
	}
	
	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component to grab from palette
	 * @param xTargetPosition - x coordinate for drop location 
	 * @param yTargetPosition - y coordinate for drop location
	 */
	public static void dragFromPaletteToDiagram(SWTBotGefEditor editor, String componentName, int xTargetPosition, int yTargetPosition) {
		editor.activateTool(componentName);
		editor.drag(xTargetPosition, yTargetPosition, xTargetPosition, yTargetPosition);
	}
	
	

}
