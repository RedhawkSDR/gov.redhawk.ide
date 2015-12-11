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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class DialogEditingFeatureForPattern extends AbstractCustomFeature implements IDialogEditingFeature {

	private final IDialogEditingPattern pattern;
	private boolean changesApplied = false;

	/*
	 * Constructor
	 */
	public DialogEditingFeatureForPattern(IFeatureProvider featureProvider, IDialogEditingPattern pattern) {
		super(featureProvider);
		this.pattern = pattern;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Edit " + pattern.getEditName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "&Edit " + pattern.getEditName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canDialogEdit(ICustomContext context) {
		return pattern.canDialogEdit(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean dialogEdit(ICustomContext context) {
		return pattern.dialogEdit(context);
	}

	@Override
	public void execute(ICustomContext context) {
		changesApplied = dialogEdit(context);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return canDialogEdit(context);
	}

	@Override
	public boolean hasDoneChanges() {
		return changesApplied;
	}

}
