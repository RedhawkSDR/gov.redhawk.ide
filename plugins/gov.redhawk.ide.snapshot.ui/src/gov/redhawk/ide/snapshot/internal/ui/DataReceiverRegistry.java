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
package gov.redhawk.ide.snapshot.internal.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;

import gov.redhawk.ide.snapshot.datareceiver.IDataReceiverRegistry;
import gov.redhawk.ide.snapshot.datareceiver.IDataRecieverDesc;
import gov.redhawk.ide.snapshot.ui.SnapshotActivator;
import gov.redhawk.sca.internal.ui.ScaContentTypeRegistry;
import gov.redhawk.sca.ui.ScaUiPlugin;

/**
 * 
 */
public enum DataReceiverRegistry implements IExtensionChangeHandler, IDataReceiverRegistry {
	INSTANCE;
	
	private static final String EP_ID = "datareceiver";
	private ExtensionTracker tracker;
	
	private DataReceiverRegistry() {
		populateRegistry();
	}
	
	private void populateRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(SnapshotActivator.PLUGIN_ID, EP_ID);

		this.tracker = new ExtensionTracker(reg);

		if (ep != null) {
			final IFilter filter = ExtensionTracker.createExtensionPointFilter(ep);
			this.tracker.registerHandler(this, filter);
			final IExtension[] extensions = ep.getExtensions();
			for (final IExtension extension : extensions) {
				addExtension(this.tracker, extension);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiverRegistry#getRecieverDesc(java.lang.String)
	 */
	@Override
	public IDataRecieverDesc getRecieverDesc(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiverRegistry#getRecieverDescs()
	 */
	@Override
	public IDataRecieverDesc[] getRecieverDescs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		for (IConfigurationElement element : extension.getConfigurationElements()) {
			if ("DataReceiver".equals(element.getName())) {
				addDataReceiver(element);
			}
		}
		// TODO Auto-generated method stub
		
	}

	private void addDataReceiver(IConfigurationElement element) {
		element.getAttribute("name");
	}

	@Override
	public void removeExtension(IExtension extension, Object[] objects) {
		// TODO Auto-generated method stub
		
	}

}
