/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.UsesPortPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.ProvidesPortPattern;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.pattern.DirectEditingFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;

public abstract class AbstractGraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	/**
	 * @param dtp
	 */
	public AbstractGraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		
		// Add Find By patterns
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());

		addPattern(new UsesPortPattern());
		addPattern(new ProvidesPortPattern());
	}

	/**
	 * Search for a non-null business object by traversing up a PictogramElement's parents.
	 * @param pictogramElement
	 * @return
	 */
	protected Object getNonNullBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
		Object businessObject = getBusinessObjectForPictogramElement(pictogramElement);
		while (businessObject == null && pictogramElement != null) {
			pictogramElement = (PictogramElement) pictogramElement.eContainer();
			businessObject = getBusinessObjectForPictogramElement(pictogramElement);
		}
		return businessObject;
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}
		// For component/device/service shapes, the editable text is a child of the inner rectangle, which does not
		// have a business object link, so search for a reasonable parent.
		Object businessObject = getNonNullBusinessObjectForPictogramElement(context.getPictogramElement());
		IDirectEditingFeature ret = null;
		for (IPattern pattern : this.getPatterns()) {
			if (checkPattern(pattern, businessObject)) {
				IPattern chosenPattern = null;
				IDirectEditingFeature f = new DirectEditingFeatureForPattern(this, pattern);
				if (checkFeatureAndContext(f, context)) {
					if (ret == null) {
						ret = f;
						chosenPattern = pattern;
					} else {
						traceWarning("getDirectEditingFeature", pattern, chosenPattern); //$NON-NLS-1$
					}
				}
			}
		}

		if (ret == null) {
			ret = getDirectEditingFeatureAdditional(context);
		}

		return ret;
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		ICreateConnectionFeature[] connectionFeatures = getCreateConnectionFeatures();
		return connectionFeatures;
	}

}
