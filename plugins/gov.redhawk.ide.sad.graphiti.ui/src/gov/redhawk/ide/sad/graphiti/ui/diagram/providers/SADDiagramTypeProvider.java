package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public class SADDiagramTypeProvider extends AbstractDiagramTypeProvider implements
		IDiagramTypeProvider {

	public final static String DIAGRAM_TYPE_ID = "SADDiagram"; 
	public final static String DIAGRAM_EXT = ".sad_GDiagram"; 
	public final static String PROVIDER_ID = "gov.redhawk.ide.sad.graphiti.ui.FactoryProvider";
	
	private IToolBehaviorProvider[] toolBehaviorProviders;
	
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
			IStatus status = new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, e.getMessage(), e);
		}
	}
	
	/**
	 * Provide a custom Behavior Provider
	 */
	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders(){
		if(toolBehaviorProviders == null){
			toolBehaviorProviders = new IToolBehaviorProvider[] { new RHToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
	
	
	/**
	 * On startup scan the model and update our diagram using the registered update feature providers
	 */
	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}
}
