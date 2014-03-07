/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class TutorialDiagramTypeProvider extends AbstractDiagramTypeProvider {

    private IToolBehaviorProvider[] toolBehaviorProviders;

    public TutorialDiagramTypeProvider() {
        super();
        setFeatureProvider(new TutorialFeatureProvider(this));
    }

    @Override
    public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
        if (toolBehaviorProviders == null) {
            toolBehaviorProviders =
                new IToolBehaviorProvider[] { new TutorialToolBehaviorProvider(
                    this) };
        }
        return toolBehaviorProviders;
    }
}
