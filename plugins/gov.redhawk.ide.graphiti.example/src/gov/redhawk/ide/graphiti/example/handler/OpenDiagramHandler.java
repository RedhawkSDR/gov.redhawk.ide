package gov.redhawk.ide.graphiti.example.handler;


import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class OpenDiagramHandler extends AbstractHandler implements IHandler {

	private static final String PARAM_FILE_NAME = "gov.redhawk.ide.graphiti.example.fileName";

	/**
	 * {@inheritDoc}
	 */

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		
		//get platform uri for diagram file (DIAGRAM FILE MUST EXIST)
//		final URI diagramURI = URI.createPlatformResourceURI("/home/mal/graphiti/multiShapes.diagram", true);
//		final URI diagramURI = URI.createPlatformResourceURI("W3/src/diagrams/oneShape.diagram", true);
		
		String fileName = event.getParameter(PARAM_FILE_NAME);
		
		URL url = null;
		try {
			url = new URL("platform:/plugin/gov.redhawk.draw2d.test/diagrams/" + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("diagram URL: " + url.toString());
		final URI diagramURI = URI.createURI(url.toString());

		//create editing domain
		ResourceSet resourceSet = new ResourceSetImpl();
		EditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);

		//get resource from diagram uri
		Resource diagramResource = editingDomain.getResourceSet().getResource(diagramURI, true);

		  

		//load diagram from resource     
		Diagram diagram = (Diagram) diagramResource.getContents().get(0);

		//open the diagram
		openDiagramEditor(diagram);

		return null;
	}
	
	protected void openDiagramEditor(Diagram diagram) {

	       // Found a diagram to open

	       String diagramTypeProviderId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());

	       GraphitiUiInternal.getWorkbenchService().openDiagramEditor(diagram, diagramTypeProviderId,

	               DiagramEditor.DIAGRAM_EDITOR_ID);
	   }	

}