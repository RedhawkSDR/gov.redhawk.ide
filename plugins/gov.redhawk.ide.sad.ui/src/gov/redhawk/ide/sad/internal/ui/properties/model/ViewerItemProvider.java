/*******************************************************************************
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.internal.ui.properties.model;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemProvider;

public abstract class ViewerItemProvider extends ItemProvider {

	public ViewerItemProvider() {
		super();
	}

	public abstract EditingDomain getEditingDomain();

	protected abstract Object getContainer(EStructuralFeature feature);

	protected abstract Object createContainer(EStructuralFeature feature, Object value);

	protected EStructuralFeature getChildFeature(Object object, Object child) {
		return null;
	}

	protected Command createCommand(EditingDomain domain, Class< ? > commandClass, EStructuralFeature feature, Object value) {
		Object peer = getContainer(feature);
		if (peer == null && (commandClass == AddCommand.class || commandClass == SetCommand.class)) {
			return createParentCommand(domain, feature, createContainer(feature, value));
		}
		if (commandClass == AddCommand.class) {
			return createAddCommand(domain, peer, getChildFeature(peer, value), value);
		} else if (commandClass == SetCommand.class) {
			return createSetCommand(domain, peer, feature, value);
		} else if (commandClass == DeleteCommand.class) {
			return createDeleteCommand(domain, peer, feature);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createParentCommand(EditingDomain domain, EStructuralFeature feature, Object value) {
		ViewerItemProvider parentItemProvider = (ViewerItemProvider) getParent(this);
		if (parentItemProvider != null) {
			return parentItemProvider.createCommand(domain, AddCommand.class, feature, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	protected Command createAddCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		return AddCommand.create(domain, owner, feature, value);
	}

	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		return SetCommand.create(domain, owner, getChildFeature(owner, value), value);
	}

	protected Command createDeleteCommand(EditingDomain domain, Object object, EStructuralFeature feature) {
		return DeleteCommand.create(domain, object);
	}

}
