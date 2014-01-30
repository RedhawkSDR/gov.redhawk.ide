package gov.redhawk.ide.sad.graphiti.ui.diagram;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class RHGraphitiDiagramEditor extends DiagramEditor{

	private EditingDomain editingDomain;
	
	public RHGraphitiDiagramEditor(EditingDomain editingDomain){
		this.editingDomain = editingDomain;
	}
	
	@Override
	protected DiagramBehavior createDiagramBehavior() {
	    return new DiagramBehavior(this) {
	    	
	    	@Override
	    	protected DefaultUpdateBehavior createUpdateBehavior() {
	    		return new DefaultUpdateBehavior(this) {

	    			//we need to provide our own editing domain so that all editors are working on the 
	    			//same resource.  In order to work with a Graphiti diagram our form creates an editing domain
	    			//with the Graphiti supplied Command stack.
	    			@Override
	    			protected void createEditingDomain() {
	    				initializeEditingDomain((TransactionalEditingDomain) editingDomain);
	    			}
	    		};
	    	}
	    	
	    };
	}
	
	
}
