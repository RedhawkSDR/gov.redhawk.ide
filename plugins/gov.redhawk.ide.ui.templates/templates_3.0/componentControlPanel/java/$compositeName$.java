/**
 * 
 */
package $packageName$;

import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.observables.SCAObservables;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class $compositeName$ extends Composite {

	private ScaItemProviderAdapterFactory adapterFactory = new ScaItemProviderAdapterFactory();
	private ScaComponent input;
	private TreeViewer viewer;
	private Text propertyTextField;
	private EMFDataBindingContext context;

	public $compositeName$(Composite parent, int style) {
		super(parent, style);
		createPartControl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(final Composite main) {
		main.setLayout(new GridLayout(2, false));

		Group controlGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		controlGroup.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		controlGroup.setText("Controls");
		createControlGroup(controlGroup);

		Group viewerGroup = new Group(main, SWT.SHADOW_ETCHED_OUT);
		viewerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewerGroup.setText("Viewer");
		createViewer(viewerGroup);

	}

	/**
	 * TODO: Add additional controls for other properties
	 */
	private void createControlGroup(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		Label label = new Label(parent, SWT.None);
		label.setText("Component:");

		Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		text.setText(input.getIdentifier());
		
		/**
		 * <b> SAMPLE CODE</b>
		 * There is some sample code of how to tie a text field to a property value
		 */
		
		label = new Label(parent, SWT.None);
		label.setText("Property:");
		propertyTextField = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		propertyTextField.setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, false).create());
		
	}

	/**
	 * TODO: Sample use of Viewer and adapter Factories, safe to delete this method
	 */
	private void createViewer(Composite parent) {
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		parent.setLayout(layout);
		// Sample Viewer
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.setInput(getInput());
	}

	public TreeViewer getViewer() {
		return viewer;
	}
	
	@Override
	public void dispose() {
		if (this.context != null) {
			context.dispose();
			context = null;
		}
		super.dispose();
	}

	public void setInput(ScaComponent input) {
		this.input = input;
		if (this.context != null) {
			context.dispose();
			context = null;
		}
		if (this.input != null) {
			context = new EMFDataBindingContext();
			addBindings();
		}
	}

	/**
	 * TODO: Add additional control bindings for simple properties
	 */
	private void addBindings() {
		/**
		 * EXAMPLE
		 */
		/* 
		ScaSimpleProperty simpleProp = (ScaSimpleProperty) input.getProperty("propid");
		context.bindValue(
			WidgetProperties.text(SWT.Modify).observeDelayed(500, propertyTextField),
			SCAObservables.observeSimpleProperty(simpleProp));
		*/
	}

	public ScaComponent getInput() {
		return input;
	}

}
