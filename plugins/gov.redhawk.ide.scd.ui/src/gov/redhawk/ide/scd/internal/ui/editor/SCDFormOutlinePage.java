package gov.redhawk.ide.scd.internal.ui.editor;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.ide.scd.ui.editor.page.PortsFormPage;
import gov.redhawk.ui.editor.FormOutlinePage;
import gov.redhawk.ui.editor.SCAFormEditor;

/**
 * Provides the outline for the SCD editor.
 */
public class SCDFormOutlinePage extends FormOutlinePage {

	public SCDFormOutlinePage(SCAFormEditor editor) {
		super(editor);

		// Create our own label provider that will decorate items that have warnings/errors.
		// We re-use the editor's adapter factory so that objects are the same between the editor & outline
		ILabelProvider provider = new AdapterFactoryLabelProvider(fEditor.getAdapterFactory());
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		super.setLabelProvider(new DecoratingLabelProvider(provider, decorator));
	}

	@Override
	protected void addItemProviders(ComposedAdapterFactory itemAdapterFactory) {
		// We override getAdapterFactory(), so this isn't necessary
		throw new IllegalStateException("Internal error - this method should never be called");
	}

	@Override
	public AdapterFactory getAdapterFactory() {
		// We re-use the editor's adapter factory
		return fEditor.getAdapterFactory();
	}

	@Override
	protected boolean getChildren(Object parent) {
		// Defer to the content provider
		return true;
	}

	@Override
	protected String getParentPageId(Object item) {
		// Only one page in the editor
		return PortsFormPage.PAGE_ID;
	}
}
