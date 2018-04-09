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
package gov.redhawk.ide.graphiti.sad.ui.properties;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

import gov.redhawk.ide.graphiti.ui.properties.AbstractKeyValuePropertiesComposite;
import gov.redhawk.ide.graphiti.ui.properties.AbstractKeyValuePropertiesSection;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.Reservation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * @since 2.1
 */
public class SadReservationsPropertySection extends AbstractKeyValuePropertiesSection {

	private HostCollocation getSelectedHostCollocation() {
		EObject eObj = getEObject();
		if (!(eObj instanceof HostCollocation)) {
			return null;
		}

		return ((HostCollocation) eObj);
	}

	@Override
	protected AbstractKeyValuePropertiesComposite getTreeComposite(Composite parent, int style, int treeStyle) {
		return new ReservationsPropertyComposite(parent, style, treeStyle);
	}

	/**
	 * Add a new {@link Reservation} element to the host collocation
	 */
	protected Command createAddCommand() {
		Reservation newReservation = SadFactory.eINSTANCE.createReservation();
		newReservation.setKind("<Enter Kind>");
		newReservation.setValue("<Enter Value>");
		return AddCommand.create(getEditingDomain(), getSelectedHostCollocation(), SadPackage.Literals.HOST_COLLOCATION__RESERVATION, newReservation);
	}

	/**
	 * Remove a {@link Reservation} element from the host collocation.
	 */
	protected Command createRemoveCommand() {
		HostCollocation hostCollocation = getSelectedHostCollocation();
		Reservation reservation = (Reservation) ((StructuredSelection) getTreeViewer().getSelection()).getFirstElement();
		return RemoveCommand.create(getEditingDomain(), hostCollocation, SadPackage.Literals.HOST_COLLOCATION__RESERVATION, reservation);
	}

	@Override
	protected String getToolTipSuffix() {
		return "Reservation";
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		EObject eObj = getEObject();
		if (eObj instanceof HostCollocation) {
			HostCollocation hostCollocation = (HostCollocation) eObj;
			getTreeViewer().setInput(hostCollocation);
		}
	}
}
