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
package gov.redhawk.ide.spd.internal.ui.parts;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;

import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * 
 */
public class PropertyElementSelectorDialog extends ElementListSelectionDialog {
	private final class ElementHolder {
		private SoftPkg pkg;
		private Simple prop;

		private ElementHolder() {

		}
	};

	/**
	 * @param parent
	 * @param renderer
	 */
	public PropertyElementSelectorDialog(final Shell parent) {
		super(parent, PropertyElementSelectorDialog.createLabelProvider());
		setTitle("Select Property");
		setBlockOnOpen(true);
		setEmptyListMessage("No Properties to select.");
		setEmptySelectionMessage("No property selected.");
		this.setMessage("Select a property:");
		setElements(initElements());
		setAllowDuplicates(true);
		setMultipleSelection(false);
	}

	/**
	 * @return
	 */
	private Object[] initElements() {
		// Load all devices from the local SDR root
		final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();

		final ArrayList<ElementHolder> elements = new ArrayList<ElementHolder>();
		for (final SoftPkg deviceSpd : sdrRoot.getDevicesContainer().getComponents()) {
			final PropertyFile propertyFile = deviceSpd.getPropertyFile();
			if ((propertyFile != null) && (propertyFile.getProperties() != null)) {
				// Allocation properties *must* be simple
				for (final Simple prop : propertyFile.getProperties().getSimple()) {
					for (final Kind kind : prop.getKind()) {
						if (kind.getType().equals(PropertyConfigurationType.ALLOCATION)) {
							final ElementHolder eh = new ElementHolder();
							eh.pkg = deviceSpd;
							eh.prop = prop;
							elements.add(eh);
						}
					}
				}
			}

		}
		return elements.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFirstResult() {
		final Object retVal = super.getFirstResult();
		if (retVal instanceof ElementHolder) {
			final ElementHolder v = (ElementHolder) retVal;
			return v.prop;
		}
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getResult() {
		final ArrayList<Object> results = new ArrayList<Object>();
		final Object[] retVal = super.getResult();
		for (final Object obj : retVal) {
			if (obj instanceof ElementHolder) {
				results.add(((ElementHolder) obj).prop);
			} else {
				results.add(obj);
			}
		}
		return results.toArray();
	}

	/**
	 * @return
	 */
	private static ILabelProvider createLabelProvider() {
		return new LabelProvider() {

			@Override
			public String getText(final Object element) {
				final ElementHolder eh = (ElementHolder) element;
				String name = eh.prop.getName();
				if (name == null) {
					name = eh.prop.getId();
				}
				return eh.pkg.getName() + " : " + name;
			}

		};
	}

}
