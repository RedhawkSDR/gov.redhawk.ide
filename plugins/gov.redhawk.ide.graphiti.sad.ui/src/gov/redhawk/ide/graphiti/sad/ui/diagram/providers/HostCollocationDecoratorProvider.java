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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.tb.TextDecorator;

import gov.redhawk.core.graphiti.ui.diagram.providers.IDecoratorProvider;
import gov.redhawk.core.graphiti.ui.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.UsesDeviceRef;

/**
 * @since 2.0
 */
public class HostCollocationDecoratorProvider implements IDecoratorProvider {

	protected static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		HostCollocation hostCol = DUtil.getBusinessObject(pe, HostCollocation.class);

		// Create and return a decorator indicating the presence of any uses device refs
		if (hostCol != null && !hostCol.getUsesDeviceRef().isEmpty()) {
			int hcWidth = pe.getGraphicsAlgorithm().getWidth();

			List<String> usesDevIds = new ArrayList<>();
			for (UsesDeviceRef ref : hostCol.getUsesDeviceRef()) {
				usesDevIds.add(ref.getRefid());
			}
			String deviceString = StringUtils.join(usesDevIds, ",");

			// Image decorator
			ImageDecorator imageDecorator = new ImageDecorator(ImageProvider.IMG_USES_DEVICE);
			imageDecorator.setX((int) hcWidth - 150);
			imageDecorator.setY(0);

			// Text decorator - Sets a max string length
			TextDecorator textDecorator = new TextDecorator(StringUtils.abbreviate(deviceString.toString(), 17));
			textDecorator.setFontName("Sans");
			textDecorator.setX(imageDecorator.getX() + 25);
			textDecorator.setY(0);

			// ToolTip message
			StringBuffer message = new StringBuffer("Collocated Uses Devices: ");
			message.append("\n * " + StringUtils.join(usesDevIds, "\n * "));
			imageDecorator.setMessage(message.toString());
			textDecorator.setMessage(message.toString());

			return new IDecorator[] { imageDecorator, textDecorator };
		}
		return NO_DECORATORS;
	}

}
