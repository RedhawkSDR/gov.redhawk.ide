/**
 * 
 */
package $packageName$;

import gov.redhawk.sca.util.PluginUtil;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * An example showing how to create a property section.
 */
public class $sectionClassName$ extends AbstractPropertySection {
	private $compositeName$ composite;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, true));
		super.createControls(c, aTabbedPropertySheetPage);
		composite = new $compositeName$(c, SWT.None);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			$resourceClassName$ component = PluginUtil.adapt($resourceClassName$.class, element);
			composite.setInput(component);
		}
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

}
