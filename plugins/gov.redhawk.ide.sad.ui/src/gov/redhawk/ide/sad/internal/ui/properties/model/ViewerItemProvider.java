package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

public abstract class ViewerItemProvider implements ITreeItemContentProvider {

	public ViewerItemProvider() {
		super();
	}

	@Override
	public Collection< ? > getElements(Object object) {
		return getChildren(object);
	}

	@Override
	public Collection< ? > getChildren(Object object) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean hasChildren(Object object) {
		return !getChildren(object).isEmpty();
	}

	@Override
	public Object getParent(Object object) {
		return null;
	}

	public abstract EditingDomain getEditingDomain();

	protected EStructuralFeature getChildFeature(Object object, Object child) {
		return null;
	}

	public Command createAddCommand(EditingDomain editingDomain, Object owner, Object value) {
		return null;
	}

	public Command createSetCommand(EditingDomain domain, Object owner, Object value) {
		return null;
	}

	public Command createRemoveCommand(EditingDomain domain, Object owner, Object value) {
		return null;
	}
	
}
