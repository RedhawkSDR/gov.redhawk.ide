package gov.redhawk.ide.sad.graphiti.ui.diagram.features.delete;

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteSADConnectInterface extends DefaultDeleteFeature{

	public DeleteSADConnectInterface(IFeatureProvider fp) {
	    super(fp);
    }
	
	@Override
	public boolean canDelete(IDeleteContext context){
		return true;
	}
	
	@Override 
	public void delete(IDeleteContext context){
		PictogramElement pe = context.getPictogramElement();
		
		
		preDelete(context);
		
		//delete business objects
		SadConnectInterface connectInterface = null;
		for(EObject eObj: pe.getLink().getBusinessObjects()){
			if(eObj instanceof SadConnectInterface){
				connectInterface = (SadConnectInterface)eObj;
				break;
			}
		}
		final SadConnectInterface finalConnectInterface = connectInterface;

		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
			protected void doExecute() {

				//assembly controller may reference componentInstantiation
				//delete reference if applicable
				if(sad.getConnections() != null){
					sad.getConnections().getConnectInterface().remove(finalConnectInterface);
				}
				
			}
		});
		
		//remove graphical components
		IRemoveContext rc = new RemoveContext(pe);
		IFeatureProvider featureProvider = getFeatureProvider();
		IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
		if (removeFeature != null) {
			removeFeature.remove(rc);
			setDoneChanges(true);
		}
		
		postDelete(context);
	}

}
