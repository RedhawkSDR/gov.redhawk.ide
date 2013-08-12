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
package gov.redhawk.ide.spd.internal.ui.editor;

import java.util.Collection;
import java.util.Iterator;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.omg.CosEventChannelAdmin.EventChannelHelper;

public class PrfListener extends EContentAdapter {

	public static final String EVENTCHANNEL_REPID = EventChannelHelper.id();

	public static final String PROPERTY_EVENT = Uses.PORT_NAME_PROP_EVENTS;

	private static final EStructuralFeature[] COMPONENT_PATH = new EStructuralFeature[] {
	        SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, SpdPackage.Literals.DESCRIPTOR__COMPONENT,
	};

	private Properties properties;
	private final SoftPkg spd;

	private final EditingDomain editingDomain;

	public PrfListener(final EditingDomain editingDomain, final Resource mainResource) {
		this.editingDomain = editingDomain;
		this.spd = SoftPkg.Util.getSoftPkg(mainResource);
		if (this.spd != null && this.spd.getPropertyFile() != null && this.spd.getPropertyFile().getProperties() != null) {
			this.properties = this.spd.getPropertyFile().getProperties();
			this.properties.eAdapters().add(this);
		}
	}

	public void dispose() {
		if (this.properties != null) {
			this.properties.eAdapters().remove(this);
			this.properties = null;
		}
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		if (notification.getFeature() == PrfPackage.Literals.SIMPLE__KIND) {
			updateEventPorts();
		} else if (notification.getFeature() == PrfPackage.Literals.SIMPLE_SEQUENCE__KIND) {
			updateEventPorts();
		} else if (notification.getFeature() == PrfPackage.Literals.STRUCT__CONFIGURATION_KIND) {
			updateEventPorts();
		} else if (notification.getFeature() == PrfPackage.Literals.STRUCT_SEQUENCE__CONFIGURATION_KIND) {
			updateEventPorts();
		} else if (notification.getFeature() == PrfPackage.Literals.PROPERTIES__PROPERTIES) {
			updateEventPorts();
		}
	}

	private void updateEventPorts() {
		boolean supportEvents = false;
		final FeatureMap entries = this.properties.getProperties();
		for (final Iterator<Entry> i = entries.iterator(); i.hasNext() && !supportEvents;) {
			final Entry entry = i.next();
			final Object value = entry.getValue();
			if (value instanceof AbstractProperty) {
				supportEvents = ((AbstractProperty) value).isKind(PropertyConfigurationType.EVENT);
			}
		}

		final CompoundCommand command = new CompoundCommand();
		final SoftwareComponent component = ScaEcoreUtils.getFeature(this.spd, PrfListener.COMPONENT_PATH);
		if (component == null) {
			// SPD doesn't have a component, therefore, it can't support ports, abort.
			return;
		}

		ComponentFeatures componentFeatures = component.getComponentFeatures();
		if (componentFeatures == null) {
			componentFeatures = ScdFactory.eINSTANCE.createComponentFeatures();
			command.append(SetCommand.create(this.editingDomain, component, ScdPackage.SOFTWARE_COMPONENT__COMPONENT_FEATURES, componentFeatures));
		}
		Ports ports = componentFeatures.getPorts();
		if (ports == null) {
			ports = ScdFactory.eINSTANCE.createPorts();
			command.append(SetCommand.create(this.editingDomain, componentFeatures, ScdPackage.COMPONENT_FEATURES__PORTS, ports));
		}
		Interfaces interfaces = component.getInterfaces();
		if (interfaces == null) {
			interfaces = ScdFactory.eINSTANCE.createInterfaces();
			command.append(SetCommand.create(this.editingDomain, component, ScdPackage.SOFTWARE_COMPONENT__INTERFACES, interfaces));
		}

		boolean found = false;
		final FeatureMap group = ports.getGroup();
		if (group != null) {
			for (final Iterator<Entry> i = group.iterator(); i.hasNext() && !found;) {
				final Entry entry = i.next();
				if (entry.getValue() instanceof Uses) {
					final Uses port = (Uses) entry.getValue();
					if (port.getName().equals(PrfListener.PROPERTY_EVENT)) {
						found = true;
					}
				}
			}
		}
		if (supportEvents) {
			if (!found) {
				command.append(addPropertyEventPort(this.editingDomain, ports, interfaces));
			}
		} else {
			if (found) {
				command.append(removePropertyEventPort(this.editingDomain, ports, interfaces));
			}
		}

		final Command finalCommand = command.unwrap();
		if (finalCommand.canExecute()) {
			this.editingDomain.getCommandStack().execute(finalCommand);
		}
	}

	private Command addPropertyEventPort(final EditingDomain dom, final Ports ports, final Interfaces ifaces) {
		final Uses uses = ScdFactory.eINSTANCE.createUses();
		uses.setUsesName(PrfListener.PROPERTY_EVENT);
		uses.setRepID(PrfListener.EVENTCHANNEL_REPID);
		uses.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(PortType.RESPONSES));

		final CompoundCommand command = new CompoundCommand("Add Port Command");
		command.append(AddCommand.create(dom, ports, ScdPackage.Literals.PORTS__USES, uses));

		boolean addInterface = true;
		// Check if the interface already exists, otherwise add it
		for (final Interface iface : ifaces.getInterface()) {
			if (PrfListener.EVENTCHANNEL_REPID.equals(iface.getRepid())) {
				addInterface = false;
			}
		}
		if (addInterface) {
			final Interface i = ScdFactory.eINSTANCE.createInterface();
			i.setName("EventChannel");
			i.setRepid(PrfListener.EVENTCHANNEL_REPID);
			command.append(AddCommand.create(dom, ifaces, ScdPackage.Literals.INTERFACES__INTERFACE, i));
		}

		return command.unwrap();
	}

	private Command removePropertyEventPort(final EditingDomain dom, final Ports ports, final Interfaces ifaces) {
		final CompoundCommand command = new CompoundCommand("Remove Port Command");
		Interface iface = null;
		for (final FeatureMap.Entry entry : ports.getGroup()) {
			if (entry.getValue() instanceof AbstractPort) {
				final AbstractPort port = (AbstractPort) entry.getValue();
				if (PrfListener.PROPERTY_EVENT.equals(port.getName()) && PrfListener.EVENTCHANNEL_REPID.equals(port.getRepID())) {
					command.append(RemoveCommand.create(dom, ports, entry.getEStructuralFeature(), port));
					iface = port.getInterface();
				}
			}
		}

		// Remove the interface if nothing else uses it
		final Collection<Setting> result = EcoreUtil.UsageCrossReferencer.find(iface, ifaces.eResource());
		if (result.size() <= 1) {
			command.append(RemoveCommand.create(dom, ifaces, ScdPackage.Literals.INTERFACES__INTERFACE, iface));
		}

		return command.unwrap();
	}

	@Override
	protected void addAdapter(final Notifier notifier) {
		if (notifier instanceof Properties || notifier instanceof Simple || notifier instanceof SimpleSequence || notifier instanceof Struct
		        || notifier instanceof StructSequence || notifier instanceof Kind || notifier instanceof ConfigurationKind) {
			super.addAdapter(notifier);
		}
	}
}
