package gov.redhawk.ide.sad.graphiti.ui.diagram.wizards;

import gov.redhawk.ide.sad.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage.CORBANameModel;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

public class FindByCORBANameWizard extends Wizard implements IWizard{

	FindByCORBANameWizardPage findByCORBANameWizardPage;
	
	public FindByCORBANameWizard(){
		super();
		setWindowTitle("New Find By CORBA Name");
	}
	
	public void addPages(){
		addPage(findByCORBANameWizardPage = new FindByCORBANameWizardPage());
	}
	
	public CORBANameModel getCORBANameModel(){
		return findByCORBANameWizardPage.getModel();
	}
	
	@Override
    public boolean performFinish() {
		return true;
    }

}
