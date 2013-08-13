package gov.redhawk.ide.sad.graphiti;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public class SADDiagramTypeProvider extends AbstractDiagramTypeProvider implements
		IDiagramTypeProvider {

	
	public SADDiagramTypeProvider(){
		super();
		
		setFeatureProvider(new SADDiagramFeatureProvider(this));
		
		//open properties view
		try{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				"org.eclipse.ui.views.PropertySheet",
				"org.eclipse.ui.views.PropertySheet",
				IWorkbenchPage.VIEW_VISIBLE);
			
		}catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, "gov.redhawk.ide.sad.ui", e.getMessage(), e);
		}
	}
}
