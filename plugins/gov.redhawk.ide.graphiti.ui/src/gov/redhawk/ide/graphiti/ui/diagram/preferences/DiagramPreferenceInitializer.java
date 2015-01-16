package gov.redhawk.ide.graphiti.ui.diagram.preferences;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DiagramPreferenceInitializer extends AbstractPreferenceInitializer{

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(DiagramPreferenceConstants.HIDE_DETAILS, false);
		store.setDefault(DiagramPreferenceConstants.HIDE_UNUSED_PORTS, false);
		
	}

}
