/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    cbrand - Bug 377475 - Fix AbstractCustomFeature.execute and canExecute
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.features;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class TutorialRenameEClassFeature extends AbstractCustomFeature {

	private boolean hasDoneChanges = false;

	public TutorialRenameEClassFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Re&name EClass"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Change the name of the EClass"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow rename if exactly one pictogram element
		// representing an EClass is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof EClass) {
				ret = true;
			}
		}
		return ret;
	}

	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof EClass) {
				EClass eClass = (EClass) bo;
				String currentName = eClass.getName();
				// ask user for a new class name
				String newName = ExampleUtil.askString("Rename EClass", getDescription(), currentName); //$NON-NLS-1$
				if (newName != null && !newName.equals(currentName)) {
					this.hasDoneChanges = true;
					eClass.setName(newName);
					updatePictogramElement(pes[0]);
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}
}
