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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;

public interface NestedPropertyItemProvider {
	public Command createAddChildCommand(EditingDomain domain, Object child, EStructuralFeature feature);

	public Command createRemoveChildCommand(EditingDomain domain, Object child, EStructuralFeature feature);
}
