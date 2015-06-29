package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
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

	protected abstract Object getContainer(Object feature);
	protected abstract Object createContainer(Object feature, Object value);

	protected EStructuralFeature getChildFeature(Object object, Object child) {
		return null;
	}

	protected Command createCommand(EditingDomain domain, Class< ? > commandClass, Object feature, Object value) {
		Object peer = getContainer(feature);
		if (peer == null && (commandClass == AddCommand.class || commandClass == SetCommand.class)) {
			return createParentCommand(domain, feature, createContainer(feature, value));
		}
		if (commandClass == AddCommand.class) {
			return createAddCommand(domain, peer, getChildFeature(peer, value), value);
		} else if (commandClass == SetCommand.class){
			return createSetCommand(domain, peer, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createParentCommand(EditingDomain domain, Object feature, Object value) {
		ViewerItemProvider parentItemProvider = (ViewerItemProvider) getParent(this);
		if (parentItemProvider != null) {
			return parentItemProvider.createCommand(domain, AddCommand.class, feature, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createAddCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		return AddCommand.create(domain, owner, feature, value);
	}

	protected Command createSetCommand(EditingDomain domain, Object owner, Object value) {
		return SetCommand.create(domain, owner, getChildFeature(owner, value), value);
	}

	protected Command createRemoveCommand(EditingDomain domain, Object owner, Object value) {
		return RemoveCommand.create(domain, value);
	}
	
}
