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
package gov.redhawk.ide.internal.ui.event.model;

import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.model.sca.DomainConnectionException;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.sca.util.OrbSession;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.omg.CORBA.SystemException;
import org.omg.CosEventComm.PushConsumer;
import org.omg.CosEventComm.PushConsumerHelper;
import org.omg.CosEventComm.PushConsumerPOATie;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.InvalidObjectReference;
import CF.DomainManagerPackage.AlreadyConnected;
import CF.DomainManagerPackage.InvalidEventChannelName;
import CF.DomainManagerPackage.NotConnected;

public class DomainChannelListener extends ChannelListener {

	private ScaDomainManager domain;
	private PushConsumer ref;
	private String registrationId;

	public DomainChannelListener(IObservableList history, ScaDomainManager domain, String channel) {
		super(history, channel);
		this.domain = domain;
	}

	@Override
	public void connect(OrbSession session) throws CoreException {
		if (ref != null) {
			disconnect();
			return;
		}
		POA poa = session.getPOA();
		try {
			ref = PushConsumerHelper.narrow(poa.servant_to_reference(new PushConsumerPOATie(this)));
			registrationId = "eventViewer_" + System.getProperty("user.name") + "_" + System.currentTimeMillis();
			domain.registerWithEventChannel(ref, registrationId, getChannel());
		} catch (SystemException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		} catch (InvalidObjectReference e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		} catch (InvalidEventChannelName e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		} catch (AlreadyConnected e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		} catch (ServantNotActive e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		} catch (WrongPolicy e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel for monitor: "
				+ domain.getName() + "(" + getChannel() + ")", e));
		}

	}

	@Override
	public void disconnect() {
		if (ref == null) {
			return;
		}
		if (registrationId != null) {
			boolean disconnect = false;
			try {
				if (!domain.isConnected()) {
					domain.connect(null);
					disconnect = true;
				}
				domain.unregisterFromEventChannel(registrationId, getChannel());

			} catch (InvalidEventChannelName e) {
				// PASS
			} catch (DomainConnectionException e) {
				// PASS
			} catch (NotConnected e) {
				// PASS
			} finally {
				if (disconnect) {
					domain.disconnect();
				}
			}
			registrationId = null;
		}
		ref._release();
		ref = null;
	}

	public ScaDomainManager getDomain() {
		return domain;
	}

	@Override
	public String getFullChannelName() {
		return domain.getName() + "/" + getChannel();
	}

}
