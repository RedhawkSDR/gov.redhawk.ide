package gov.redhawk.ide.graphiti.example.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.util.Util;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class AddAllClassesCommand extends RecordingCommand {

	private IProject project;
	private TransactionalEditingDomain editingDomain;
	private String diagramName;
	private Resource createdResource;

	public AddAllClassesCommand(IProject project, TransactionalEditingDomain editingDomain, String diagramName) {
		super(editingDomain);
		this.project = project;
		this.editingDomain = editingDomain;
		this.diagramName = diagramName;
	}

	@Override
	protected void doExecute() {
		// Get all EClasses
		EClass[] allClasses = Util.getAllClasses(project, editingDomain.getResourceSet());

		// Create the diagram and its file
		Diagram diagram = Graphiti.getPeCreateService().createDiagram("tutorial", diagramName, true); //$NON-NLS-1$
		IFolder diagramFolder = project.getFolder("src/diagrams/"); //$NON-NLS-1$
		IFile diagramFile = diagramFolder.getFile(diagramName + ".diagram"); //$NON-NLS-1$
		URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
		createdResource = editingDomain.getResourceSet().createResource(uri);
		createdResource.getContents().add(diagram);

		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
				"org.eclipse.graphiti.examples.tutorial.diagram.TutorialDiagramTypeProvider"); //$NON-NLS-1$
		IFeatureProvider featureProvider = dtp.getFeatureProvider();

		// Add all classes to diagram
		int x = 20;
		int y = 20;
		for (int i = 0; i < allClasses.length; i++) {
			// Create the context information
			AddContext addContext = new AddContext();
			addContext.setNewObject(allClasses[i]);
			addContext.setTargetContainer(diagram);
			addContext.setX(x);
			addContext.setY(y);
			x = x + 20;
			y = y + 20;

			IAddFeature addFeature = featureProvider.getAddFeature(addContext);
			if (addFeature.canAdd(addContext)) {
				addFeature.add(addContext);
			}
		}
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}
}
