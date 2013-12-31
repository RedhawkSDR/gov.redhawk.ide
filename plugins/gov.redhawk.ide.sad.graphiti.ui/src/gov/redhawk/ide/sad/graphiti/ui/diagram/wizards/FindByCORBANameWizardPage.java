package gov.redhawk.ide.sad.graphiti.ui.diagram.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FindByCORBANameWizardPage extends WizardPage{

	//inner class model used to store user selections
	public static class CORBANameModel {

		public static final String CORBA_NAME = "corbaName";
		public static final String ENABLE_USES_PORT = "enableUsesPort";
		public static final String USES_PORT_NAME = "usesPortName";
		public static final String PROVIDES_PORT_NAME = "providesPortName";
		public static final String ENABLE_PROVIDES_PORT = "enableProvidesPort";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
		private String corbaName;
		private boolean enableUsesPort;
		private boolean enableProvidesPort;
		private String usesPortName;
		private String providesPortName;
		

		public CORBANameModel() {
		}

		public String getCorbaName() {
			return corbaName;
		}
		public void setCorbaName(String corbaName) {
			final String oldValue = this.corbaName;
			this.corbaName = corbaName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.CORBA_NAME, oldValue, corbaName));
		}

		public boolean setEnableUsesPort() {
			return enableUsesPort;
		}

		public boolean getEnableUsesPort() {
			return enableUsesPort;
		}

		public boolean getEnableProvidesPort() {
			return enableProvidesPort;
		}

		public void setEnableUsesPort(boolean enableUsesPort) {
			final boolean oldValue = this.enableUsesPort;
			this.enableUsesPort = enableUsesPort;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.ENABLE_USES_PORT, oldValue, enableUsesPort));
		}

		public boolean setEnableProvidesPort() {
			return enableProvidesPort;
		}

		public void setEnableProvidesPort(boolean enableProvidesPort) {
			final boolean oldValue = this.enableProvidesPort;
			this.enableProvidesPort = enableProvidesPort;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.ENABLE_PROVIDES_PORT, oldValue, enableProvidesPort));
		}

		public String getUsesPortName() {
			return usesPortName;
		}

		public void setUsesPortName(String usesPortName) {
			final String oldValue = this.usesPortName;
			this.usesPortName = usesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.USES_PORT_NAME, oldValue, usesPortName));
		}

		public String getProvidesPortName() {
			return providesPortName;
		}

		public void setProvidesPortName(String providesPortName) {
			final String oldValue = this.providesPortName;
			this.providesPortName = providesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.PROVIDES_PORT_NAME, oldValue, providesPortName));
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		public boolean isComplete() {
			if(this.enableUsesPort && this.usesPortName.length() == 0){
				return false;
			}
			if(this.enableProvidesPort && this.providesPortName.length() == 0){
				return false;
			}
			if(this.corbaName.length() == 0){
				return false;
			}
			return true;
		}
	};
	
	private static final ImageDescriptor TITLE_IMAGE = null;
	
	private CORBANameModel model;
	private DataBindingContext dbc;
	
	Button usesPortBtn,providesPortBtn;
	Text usesPortNameText,providesPortNameText;
	
	public FindByCORBANameWizardPage() {
		super("findByCorbaName", "Find By CORBA Name", TITLE_IMAGE);
		this.setDescription("Enter CORBA Name and port information");
		
		model = new CORBANameModel();
		dbc = new DataBindingContext();
	}

	@Override
    public void createControl(Composite parent) {
	   
		WizardPageSupport.create(this, dbc);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1,false));
		
		//CORBA Name
		Label corbaNameLabel = new Label(composite, SWT.NONE);
		corbaNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		corbaNameLabel.setText("CORBA Name:");
		
		Text corbaNameText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		corbaNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		corbaNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e){
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(corbaNameText, SWT.Modify),
				BeansObservables.observeValue(model, CORBANameModel.CORBA_NAME),
				new UpdateValueStrategy().setAfterGetValidator(new IValidator(){
					@Override
					public IStatus validate(Object value){
						if(((String)value).length() < 1){
							return ValidationStatus.error("CORBA Name must not be empty");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		
		
		//port group
		final Group portOptions = new Group(composite, SWT.NONE);
		portOptions.setLayout(new GridLayout());
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port Options");
		
		//uses port checkbox
		usesPortBtn = new Button(portOptions, SWT.CHECK);
		usesPortBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		usesPortBtn.setText("Uses Port");
		usesPortBtn.setSelection(model.getEnableUsesPort());
		dbc.bindValue(WidgetProperties.selection().observe(usesPortBtn), 
				BeansObservables.observeValue(model, CORBANameModel.ENABLE_USES_PORT));
		
		//uses port name
		final Label usesPortNameLabel = new Label(portOptions, SWT.NONE);
		usesPortNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		usesPortNameLabel.setText("Uses Port Name:");
		
		
		usesPortNameText = new Text(portOptions, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		usesPortNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		usesPortNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e){
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(usesPortNameText, SWT.Modify),
				BeansObservables.observeValue(model, CORBANameModel.USES_PORT_NAME),
				new UpdateValueStrategy().setAfterGetValidator(new IValidator(){
					@Override
					public IStatus validate(Object value){
						if(value instanceof String && usesPortBtn.getSelection() && ((String)value).length() < 1){
							return ValidationStatus.error("Uses Port Name must not be empty");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		usesPortBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				usesPortNameText.setEnabled(usesPortBtn.getSelection());
			}
		});
		
		//provides port checkbox
		providesPortBtn = new Button(portOptions, SWT.CHECK);
		providesPortBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		providesPortBtn.setText("Provides Port");
		providesPortBtn.setSelection(model.getEnableProvidesPort());
		dbc.bindValue(WidgetProperties.selection().observe(providesPortBtn), 
				BeansObservables.observeValue(model, CORBANameModel.ENABLE_PROVIDES_PORT));
		
		//provides port name
		final Label providesPortNameLabel = new Label(portOptions, SWT.NONE);
		providesPortNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		providesPortNameLabel.setText("Provides Port Name:");
		
		providesPortNameText = new Text(portOptions, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		providesPortNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		providesPortNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e){
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(providesPortNameText, SWT.Modify),
				BeansObservables.observeValue(model, CORBANameModel.PROVIDES_PORT_NAME),
				new UpdateValueStrategy().setAfterGetValidator(new IValidator(){
					@Override
					public IStatus validate(Object value){
						if(value instanceof String && providesPortBtn.getSelection() && ((String)value).length() < 1){
							return ValidationStatus.error("Provides Port Name must not be empty");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		providesPortBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				providesPortNameText.setEnabled(providesPortBtn.getSelection());
			}
		});
		
		//disable text boxes when ports not enabled
		usesPortNameText.setEnabled(model.getEnableUsesPort());
		providesPortNameText.setEnabled(model.getEnableProvidesPort());
		
		setControl(composite);
		
		dbc.updateModels();
	    
    }
	
	
	public CORBANameModel getModel() {
		return model;
	}
	
}
