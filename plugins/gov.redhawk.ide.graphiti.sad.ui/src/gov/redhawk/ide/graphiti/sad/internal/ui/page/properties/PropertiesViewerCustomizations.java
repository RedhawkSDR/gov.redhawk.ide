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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;

public class PropertiesViewerCustomizations extends XViewerCustomizations {

	@Override
	public List<CustomizeData> getSavedCustDatas() throws Exception {
		List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
		custDatas.add(PropertiesDefaultCustomizations.getConfigurableProperitesCustomization());
		custDatas.add(PropertiesDefaultCustomizations.getExecParamsCustomization());
		return custDatas;
	}
}
