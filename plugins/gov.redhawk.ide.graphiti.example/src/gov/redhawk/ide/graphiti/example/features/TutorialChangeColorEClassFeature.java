package gov.redhawk.ide.graphiti.example.features;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.examples.common.ExampleUtil;
import gov.redhawk.ide.graphiti.example.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * See chapter "Using Styles" in the tutorial.
 */
public class TutorialChangeColorEClassFeature extends AbstractCustomFeature {

	public TutorialChangeColorEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Change &foreground color";
	}

	@Override
	public String getDescription() {
		return "Change the foreground color";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length == 0) { // nothing selected
			return false;
		}
		// return true, if all elements are EClasses
		// note, that in execute() the selected elements are not even accessed,
		// so theoretically it would be possible that canExecute() always
		// returns true. But for usability reasons it is better to check
		// if the selected elements are EClasses.
		for (PictogramElement pe : pes) {
			final Object bo = getBusinessObjectForPictogramElement(pe);
			if (!(bo instanceof EClass)) {
				return false;
			}
		}
		return true;
	}

	public void execute(ICustomContext context) {
		Style style = StyleUtil.getStyleForEClass(getDiagram());

		// let the user choose the new color
		Color currentColor = style.getForeground();
		Color newColor = ExampleUtil.editColor(currentColor);
		if (newColor == null) { // user did not choose new color
			return;
		}
		style.setForeground(newColor);
	}
}
