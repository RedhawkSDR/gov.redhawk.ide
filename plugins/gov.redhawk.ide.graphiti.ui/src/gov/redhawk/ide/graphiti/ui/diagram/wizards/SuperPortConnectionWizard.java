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
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import gov.redhawk.diagram.util.InterfacesUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class SuperPortConnectionWizard extends Wizard {
	// TODO: Add validator to give warning on possible invalid connections?  Or just gray out non-suggested connections?
	
	public static class SuperPortConnectionWizardPage extends WizardPage {
		private static String CSI = "Component Supported Interface";
		private List<UsesPortStub> sourcePorts;
		private List<ConnectionTarget> targetPorts;
		private UsesPortStub source;
		private ConnectionTarget target;
		

		protected SuperPortConnectionWizardPage(List<UsesPortStub> sourcePorts, List<ConnectionTarget> targetPorts) {
			super("superPortConnectPage", "Multiple possible connections found", null);
			setDescription("Select desired source and target connection elements");
			this.setPageComplete(false);
			
			this.sourcePorts = sourcePorts;
			this.targetPorts = targetPorts;
			// TODO add propertyChangeListener if we decide to gray out source/target options based on selection, see SourceTargetValidator in ConnectPortWizard to get started.
		}

		@Override
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.None);
			composite.setLayout(new GridLayout(4, true));
			
			/** Source Group **/
			Group sourceGroup = new Group(composite, SWT.None);
			String sourceParentName = ((SadComponentInstantiation) sourcePorts.get(0).eContainer()).getId();
			sourceGroup.setText(sourceParentName + " (Source)");
			sourceGroup.setLayout(new FillLayout());
			sourceGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).hint(SWT.DEFAULT, 200).create());
			List<String> sourcePortNames = new ArrayList<String>();
			for (UsesPortStub port : this.sourcePorts) {
				sourcePortNames.add(port.getName());
			}
			
			final org.eclipse.swt.widgets.List sourcePortList = new org.eclipse.swt.widgets.List(sourceGroup, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			sourcePortList.setItems(sourcePortNames.toArray(new String[0]));
			sourcePortList.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					setSource(sourcePortList.getSelection()[0]);
					if (source != null && target != null) {
						if (!InterfacesUtil.areCompatible(source, target)) {
							setErrorMessage("Warning: Connection types are not an exact match, connection may not be possible.");
						} else {
							setErrorMessage(null);
						}
						setPageComplete(true);
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			
			/** Target Group **/
			Group targetGroup = new Group(composite, SWT.None);
			String targetParentName = ((SadComponentInstantiation) targetPorts.get(0).eContainer()).getId();
			targetGroup.setText(targetParentName + " (Target)");
			targetGroup.setLayout(new FillLayout());
			targetGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).hint(SWT.DEFAULT, 200).create());
			List<String> targetPortNames = new ArrayList<String>();
			for (ConnectionTarget port : this.targetPorts) {
				if (port instanceof ProvidesPortStub) {
					targetPortNames.add(((ProvidesPortStub)port).getName());
				} else if (port instanceof ComponentSupportedInterfaceStub) {
					targetPortNames.add(CSI);
				}
			}
			
			final org.eclipse.swt.widgets.List targetPortList = new org.eclipse.swt.widgets.List(targetGroup, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			targetPortList.setItems(targetPortNames.toArray(new String[0]));
			targetPortList.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					setTarget(targetPortList.getSelection()[0]);
					if (source != null && target != null) {
						if (!InterfacesUtil.areCompatible(source, target)) {
							setErrorMessage("Warning: Connection types are not an exact match, connection may not be possible.");
						} else {
							setErrorMessage(null);
						}
						setPageComplete(true);
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			
			setControl(composite);
		}
		
		public void setSource(String sourceName) {
			for (UsesPortStub port : sourcePorts) {
				if (sourceName.equals(port.getName())) {
					this.source = port;
				}
			}
		}
		
		public UsesPortStub getSource() {
			return source;
		}
		
		public void setTarget(String targetName) {
			for (ConnectionTarget port : targetPorts) {
				if (port instanceof ProvidesPortStub && targetName.equals(((ProvidesPortStub)port).getName())) {
					this.target = port;
				} else if (port instanceof ComponentSupportedInterfaceStub && targetName.equals(CSI)) {
					this.target = port;
				}
			}
		}
		
		public ConnectionTarget getTarget() {
			return target;
		}
		
	}
	
	private SuperPortConnectionWizardPage page;
	
	public SuperPortConnectionWizard(List<UsesPortStub> sourcePorts, List<ConnectionTarget> targetPorts) {
		setWindowTitle("Connect");
		this.page = new SuperPortConnectionWizardPage(sourcePorts, targetPorts);
	}
	
	@Override
	public void addPages() {
		addPage(page);
	}
	
	public SuperPortConnectionWizardPage getPage() {
		return page;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
