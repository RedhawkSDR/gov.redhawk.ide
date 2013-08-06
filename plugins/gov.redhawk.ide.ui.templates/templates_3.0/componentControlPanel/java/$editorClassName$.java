/**
 * 
 */
package $packageName$;

import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.sca.ui.editors.AbstractScaContentEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * An example showing how to create a control panel.
 */
public class $editorClassName$ extends AbstractScaContentEditor<ScaComponent> {
	private $compositeName$ composite;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite main) {
		this.composite = new $compositeName$(main, SWT.None);
		this.composite.setInput(getInput());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		if (this.composite != null) {
			composite.setFocus();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// init override necessary for WindowBuilder support
		super.init(site, input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<ScaComponent> getInputType() {
		return ScaComponent.class;
	}
}
