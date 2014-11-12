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
package gov.redhawk.ide.sad.graphiti.debug.internal.ui;

import gov.redhawk.ide.sad.graphiti.ui.diagram.RHGraphitiDiagramEditor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * The main purpose of this class is to provide a distinction between development and runtime editors
 * Add palette implementation logic now exists in the {@link RHToolBehaviorProvider}
 */
public class SandboxRHGraphitiDiagramEditor extends RHGraphitiDiagramEditor {

	public SandboxRHGraphitiDiagramEditor(final TransactionalEditingDomain editingDomain) {
		super(editingDomain);
	}
}
