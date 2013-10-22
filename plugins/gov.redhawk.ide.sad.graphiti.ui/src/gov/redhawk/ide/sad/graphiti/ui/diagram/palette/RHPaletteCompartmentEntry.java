package gov.redhawk.ide.sad.graphiti.ui.diagram.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.AbstractPaletteEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

public class RHPaletteCompartmentEntry extends AbstractPaletteEntry implements IPaletteCompartmentEntry {

	private List<IToolEntry> toolEntries = new ArrayList<IToolEntry>();

	private boolean initiallyOpen = true;

	/**
	 * Creates a new {@link PaletteCompartmentEntry}.
	 * 
	 * @param label
	 *            the text label
	 * @param iconId
	 *            the icon which is displayed
	 */
	public RHPaletteCompartmentEntry(String label, String iconId) {
		super(label, iconId);
	}

	/**
	 * Gets the tool entries.
	 * 
	 * @return the tools contained in the compartment
	 */
	public List<IToolEntry> getToolEntries() {
		return this.toolEntries;
	}

	/**
	 * adds a tool entry to the compartment.
	 * 
	 * @param toolEntry
	 *            the tool entry
	 */
	public void addToolEntry(IToolEntry toolEntry) {
		this.toolEntries.add(toolEntry);
	}
	
	public void removeToolEntry(IToolEntry toolEntry){
		this.toolEntries.remove(toolEntry);
	}

	public void setInitiallyOpen(boolean initiallyOpen) {
		this.initiallyOpen = initiallyOpen;
	}

	public boolean isInitiallyOpen() {
		return this.initiallyOpen;
	}
}
