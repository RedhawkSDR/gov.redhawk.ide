package gov.redhawk.ide.sad.graphiti.ui.diagram.wizards;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.ui.IdlInterfaceSelectionDialog;
import gov.redhawk.ide.sad.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage.CORBANameModel;

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

public class FindByServiceWizardPage extends WizardPage{

	//inner class model used to store user selections
	public static class Model {

		public static final String ENABLE_SERVICE_NAME = "enableServiceName";
		public static final String SERVICE_NAME = "serviceName";
		public static final String ENABLE_SERVICE_TYPE = "enableServiceType";
		public static final String SERVICE_TYPE = "serviceType";
		public static final String ENABLE_USES_PORT = "enableUsesPort";
		public static final String USES_PORT_NAME = "usesPortName";
		public static final String PROVIDES_PORT_NAME = "providesPortName";
		public static final String ENABLE_PROVIDES_PORT = "enableProvidesPort";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
		private boolean enableServiceName = true;
		private boolean enableServiceType;
		private String serviceName;
		private String serviceType;		
		private boolean enableUsesPort;
		private boolean enableProvidesPort;
		private String usesPortName;
		private String providesPortName;
		
		private boolean serviceTypeSupportsPorts = false;
		

		public Model() {
		}


		public boolean isServiceTypeSupportsPorts() {
			return serviceTypeSupportsPorts;
		}


		public void setServiceTypeSupportsPorts(boolean serviceTypeSupportsPorts) {
			this.serviceTypeSupportsPorts = serviceTypeSupportsPorts;
		}


		public boolean getEnableServiceName() {
			return enableServiceName;
		}

		public boolean getEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceName(boolean enableServiceName) {
			final boolean oldValue = this.enableServiceName;
			this.enableServiceName = enableServiceName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.ENABLE_SERVICE_NAME, oldValue, enableServiceName));
		}

		public boolean setEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceType(boolean enableServiceType) {
			final boolean oldValue = this.enableServiceType;
			this.enableServiceType = enableServiceType;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.ENABLE_SERVICE_TYPE, oldValue, enableServiceType));
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String usesPortName) {
			final String oldValue = this.serviceName;
			this.serviceName = usesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SERVICE_NAME, oldValue, usesPortName));
		}

		public String getServiceType() {
			return serviceType;
		}

		public void setServiceType(String providesPortName) {
			final String oldValue = this.serviceType;
			this.serviceType = providesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SERVICE_TYPE, oldValue, providesPortName));
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
			if(this.enableServiceType && this.serviceType.length() == 0){
				return false;
			}
			if(this.enableServiceName && this.serviceName.length() == 0){
				return false;
			}
			return true;
		}
	};
	
	private static final ImageDescriptor TITLE_IMAGE = null;
	
	private Model model;
	private DataBindingContext dbc;
	
	Button serviceNameBtn,serviceTypeBtn,usesPortBtn,providesPortBtn;
	Text serviceNameText,serviceTypeText,usesPortNameText,providesPortNameText;
	
	public FindByServiceWizardPage() {
		super("findByService", "Find By Service", TITLE_IMAGE);
		this.setDescription("Enter Service Identification Information");
		
		model = new Model();
		dbc = new DataBindingContext();
	}

	@Override
    public void createControl(Composite parent) {
	   
		WizardPageSupport.create(this, dbc);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1,false));

		
		//service name checkbox
		serviceNameBtn = new Button(composite, SWT.RADIO);
		serviceNameBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameBtn.setText("Service Name");
		serviceNameBtn.setSelection(model.getEnableServiceName());
		dbc.bindValue(WidgetProperties.selection().observe(serviceNameBtn), 
				BeansObservables.observeValue(model, Model.ENABLE_SERVICE_NAME));
		
		//service name
		final Label serviceNameLabel = new Label(composite, SWT.NONE);
		serviceNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameLabel.setText("Service Name:");
		
		
		serviceNameText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		serviceNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e){
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(serviceNameText, SWT.Modify),
				BeansObservables.observeValue(model, Model.SERVICE_NAME),
				new UpdateValueStrategy().setAfterGetValidator(new IValidator(){
					@Override
					public IStatus validate(Object value){
						if(value instanceof String && serviceNameBtn.getSelection() && ((String)value).length() < 1){
							return ValidationStatus.error("Service Name must not be empty");
						}
						if(value instanceof String && serviceNameBtn.getSelection() && ((String)value).contains(" ")){
							return ValidationStatus.error("Service Name must not have spaces in the name");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		serviceNameBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				serviceNameText.setEnabled(serviceNameBtn.getSelection());
				updateEnablePortsButtons();
				dbc.updateModels();
			}
		});
		
		//service type checkbox
		serviceTypeBtn = new Button(composite, SWT.RADIO);
		serviceTypeBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBtn.setText("Service Type");
		serviceTypeBtn.setSelection(model.getEnableServiceType());
		dbc.bindValue(WidgetProperties.selection().observe(serviceTypeBtn), 
				BeansObservables.observeValue(model, Model.ENABLE_SERVICE_TYPE));
		
		//service type
		final Label serviceTypeLabel = new Label(composite, SWT.NONE);
		serviceTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeLabel.setText("Service Type:");
		
		Composite serviceTypeComposite = new Composite(composite, SWT.NONE);
		serviceTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		serviceTypeComposite.setLayout(new GridLayout(2,false));
		
		serviceTypeText = new Text(serviceTypeComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		serviceTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceTypeText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e){
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(serviceTypeText, SWT.Modify),
				BeansObservables.observeValue(model, Model.SERVICE_TYPE),
				new UpdateValueStrategy().setAfterGetValidator(new IValidator(){
					@Override
					public IStatus validate(Object value){
						if(value instanceof String && serviceTypeBtn.getSelection() && ((String)value).length() < 1){
							return ValidationStatus.error("Service Type must not be empty");
						}
						if(value instanceof String && serviceTypeBtn.getSelection() && ((String)value).contains(" ")){
							return ValidationStatus.error("Service Type must not have spaces in the name");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		serviceTypeBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				serviceTypeText.setEnabled(serviceTypeBtn.getSelection());
				updateEnablePortsButtons();
				dbc.updateModels();
			}
		});
		Button serviceTypeBrowseBtn = new Button(serviceTypeComposite, SWT.BUTTON1);
		serviceTypeBrowseBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBrowseBtn.setText("Browse");
		serviceTypeBrowseBtn.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e){
				IdlInterfaceDcl result = IdlInterfaceSelectionDialog.create(getShell());
				if (result != null) {
					serviceTypeText.setText(result.getRepId());
					//if the interface selected inherits from PortSupplier than allow user to
					//specify port information
					if(extendsPortSupplier(result)){
						model.setServiceTypeSupportsPorts(true);
						updateEnablePortsButtons();
					}
				}
			}
			
		});
		
		//disable text boxes when service name/type not enabled
		serviceNameText.setEnabled(model.getEnableServiceName());
		serviceTypeText.setEnabled(model.getEnableServiceType());
		
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
						if(value instanceof String && usesPortBtn.getSelection() && ((String)value).contains(" ")){
							return ValidationStatus.error("Uses Port Name must not have spaces in the name");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		usesPortBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				usesPortNameText.setEnabled(usesPortBtn.getSelection());
				dbc.updateModels();
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
						if(value instanceof String && providesPortBtn.getSelection() && ((String)value).contains(" ")){
							return ValidationStatus.error("Provides Port Name must not have spaces in the name");
						}
						return ValidationStatus.ok();
					}
				}), null
		);
		providesPortBtn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				providesPortNameText.setEnabled(providesPortBtn.getSelection());
				dbc.updateModels();
			}
		});
		
		//disable text boxes when ports not enabled
		usesPortNameText.setEnabled(model.getEnableUsesPort());
		providesPortNameText.setEnabled(model.getEnableProvidesPort());
		
		setControl(composite);
		
		dbc.updateModels();
	    
    }
	
	/**
	 * Return true if interface extends PortSupplier interface
	 * @param idlInterfaceDcl
	 * @return
	 */
	public boolean extendsPortSupplier(IdlInterfaceDcl idlInterfaceDcl){
		if(idlInterfaceDcl.getInheritedInterfaces() != null){
			for(IdlInterfaceDcl inheritedInterface: idlInterfaceDcl.getInheritedInterfaces()){
				if(inheritedInterface.getRepId().startsWith("IDL:CF/PortSupplier") ||
						extendsPortSupplier(inheritedInterface)){
					return true;
				}
			}
		}
		return false;
	}
	
	//enable/disable enable port buttons
	public void updateEnablePortsButtons(){
		//TODO: We want to limit ports to service types that extend portSupplier
//		if(model.getEnableServiceName() || model.getEnableServiceType() && model.isServiceTypeSupportsPorts()){
		if(model.getEnableServiceName() || model.getEnableServiceType()){
			providesPortBtn.setEnabled(true);
			usesPortBtn.setEnabled(true);
		}else{
			providesPortBtn.setEnabled(false);
			usesPortBtn.setEnabled(false);
		}
		
	}
	
	public Model getModel() {
		return model;
	}
	
}
